package com.example.administrator.classcircle.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.entity.Data;
import com.example.administrator.classcircle.utils.DownloadUtil;
import com.example.administrator.classcircle.utils.FileUtils;
import com.example.administrator.classcircle.utils.OpenFileUtils;
import com.example.administrator.classcircle.utils.ThreadUtils;

import java.io.IOException;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class ShowOtherFileActivity extends BaseActivity {
    private static final String TAG = "ShowOtherFileActivity";

    private TextView mTvTime;
    private TextView mTvName;
    private ImageView mIvFileTypeImg;
    private TextView mTvFileName;
    private String fileUrl;
    private TextView mTvHeadTitle;
    private FrameLayout mFrameLayout;
    private Button mButton;
    private String mFilePath = null;

    private int progress = 0;
    private Handler progressDialogHandler;
    private int PROGRESS_DIALOG_FLAG = 1;
    private int uploadValue = 0;
    private TextView mTvBack;
    private Button mBtnPreview;
    private TextView mTvNotPreview;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean isFirstPlay = true;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_other_file;
    }

    @Override
    protected void init() {

        StatusBarUtils.compat(this, getResources().getColor(R.color.colorPrimary));
        initLayout();
        loadDataFromBmob();

    }

    private void loadDataFromBmob() {
        final Intent intent = getIntent();
        String objId = intent.getStringExtra("FILE_URL");
        BmobQuery<Data> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objId, new QueryListener<Data>() {
            @Override
            public void done(Data data, BmobException e) {
                if (e == null) {
                    String name = data.getName();
                    String time = data.getCreatedAt();
                    fileUrl = data.getUploadFile().getFileUrl();
                    String fileName = data.getUploadFile().getFilename();
                    mTvName.setText(name);
                    mTvTime.setText(String.format("%s 上传", time));
                    mTvFileName.setText(fileName);
                    if (FileUtils.getExtension(fileName).equals("ppt")) {
                        mIvFileTypeImg.setImageResource(R.drawable.file_ppt);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("txt")) {

                        mIvFileTypeImg.setImageResource(R.drawable.file_txt);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("docx")) {

                        mIvFileTypeImg.setImageResource(R.drawable.file_doc);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("pdf")) {
                        mIvFileTypeImg.setImageResource(R.drawable.file_pdf);
                        mBtnPreview.setVisibility(View.INVISIBLE);

                    } else if (FileUtils.getExtension(fileName).equals("zip")) {

                        mIvFileTypeImg.setImageResource(R.drawable.file_zip);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("xlsx")) {

                        mIvFileTypeImg.setImageResource(R.drawable.file_xls);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("apk")) {

                        mIvFileTypeImg.setImageResource(R.mipmap.apk);
                        mBtnPreview.setVisibility(View.INVISIBLE);
                    } else if (FileUtils.getExtension(fileName).equals("mp3")) {
                        mTvNotPreview.setVisibility(View.INVISIBLE);
                        mIvFileTypeImg.setImageResource(R.mipmap.mp3);
                    } else {
                        mBtnPreview.setVisibility(View.INVISIBLE);
                        mIvFileTypeImg.setImageResource(R.drawable.file_else);
                    }

                    mFrameLayout.setVisibility(View.GONE);
                } else {
                    //查询失败
                    showToast("查询失败");
                }
            }
        });
    }

    @Override
    protected void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilePath != null) {
                    Intent intent = OpenFileUtils.openFile(mFilePath);
                    startActivity(intent);
                } else {
                    showProgressBarDialog(ProgressDialog.STYLE_HORIZONTAL);
                    ThreadUtils.runOnBackgroundThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadFile();
                        }
                    });

                }
            }
        });

        mBtnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowOtherFileActivity.this, PreviewFileActivity.class);
                intent.putExtra("URL", fileUrl);
                startActivity(intent);
            }
        });
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaPlayer.isPlaying() && isFirstPlay) {
                    try {
                        mMediaPlayer.setDataSource(fileUrl);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        isFirstPlay = false;
                        mBtnPreview.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
                        mBtnPreview.setText("暂停");
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("播放出错");
                    }
                } else if (mMediaPlayer.isPlaying()) {
                    mBtnPreview.setText("预览");
                    mBtnPreview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    mMediaPlayer.pause();
                } else {
                    mBtnPreview.setText("暂停");
                    mBtnPreview.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
                    mMediaPlayer.start();
                }
            }
        });
    }

    private void downloadFile() {
        DownloadUtil.get().download(fileUrl, "classCircle", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFilePath = DownloadUtil.mFile.getPath();
                        showToast("下载成功");
                        mButton.setText("打开文件");

                    }
                });
            }

            @Override
            public void onDownloadProgress(int progress) {
                uploadValue = progress;
            }

            @Override
            public void onDownloadFailed() {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("下载失败");
                        progress = 101;
                        mButton.setText("重新下载");
                    }
                });
            }
        });
    }

    private void showProgressBarDialog(int style) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.file_download);

        progressDialog.setTitle("数据处理中");
        progressDialog.setMessage("请稍后");
        progressDialog.setProgressStyle(style);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMax(C.ALERT_DIALOG_MAX_PROGRESS);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogHandler.removeMessages(PROGRESS_DIALOG_FLAG);
                progress = 0;
                progressDialog.setProgress(progress);
            }
        });
        progressDialog.show();

        progressDialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progress >= C.ALERT_DIALOG_MAX_PROGRESS) {
                    //消失 并重置初始值
                    progressDialog.dismiss();
                    progress = 0;
                } else {
                    //  progress++;
                    // progressDialog.incrementProgressBy(1);
                    // 随机设置下一次递增进度 (50 +毫秒)
                    progress = uploadValue;
                    progressDialog.setProgress(progress);
                    progressDialogHandler.sendEmptyMessageDelayed(1, 50 + new Random().nextInt(500));
                }
            }

        };
        // 设置进度初始值
        progress = (progress > 0) ? progress : 0;
        progressDialog.setProgress(progress);
        // 发送消息
        progressDialogHandler.sendEmptyMessage(PROGRESS_DIALOG_FLAG);

    }

    private void initLayout() {

        mTvTime = (TextView) findViewById(R.id.show_other_file_time);
        mTvName = (TextView) findViewById(R.id.show_other_file_headName);
        mIvFileTypeImg = (ImageView) findViewById(R.id.show_other_file_fileImg);
        mTvFileName = (TextView) findViewById(R.id.show_other_file_fileName);
        mTvHeadTitle = (TextView) findViewById(R.id.id_header_tv);
        mTvHeadTitle.setText("下载文件");
        mFrameLayout = (FrameLayout) findViewById(R.id.show_other_file_loading);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);
        mButton = (Button) findViewById(R.id.show_file_button);
        mBtnPreview = (Button) findViewById(R.id.preview_file);
        mTvNotPreview = (TextView) findViewById(R.id.text_not_preview);
        mTvNotPreview.setVisibility(View.GONE);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }
}