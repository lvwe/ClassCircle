package com.example.administrator.classcircle.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.utils.DownloadUtil;
import com.example.administrator.classcircle.utils.OpenFileUtils;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.ThreadUtils;
import com.example.administrator.classcircle.entity.Notice;

import java.io.File;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class ShowNoticeInfoActivity extends BaseActivity {
    private static final String TAG = "ShowNoticeInfoActivity";

    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvName;
    private TextView mTvUploadTime;
    private TextView mTvContent;
    private Button mButton;
    private String mOtherUrl;
    private String mFilePath;
    private List<File> mFileList;
    private TextView mTvBack;

    private int progress = 0;
    private Handler progressDialogHandler;
    private int PROGRESSDIALOG_FLAG = 1;
    private int uploadValue = 0;
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_notice_info;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));

        mTvTitle = (TextView) findViewById(R.id.id_header_tv);
        mTvTitle.setText("通知详情");
        mTvTime = (TextView) findViewById(R.id.show_notice_info_TvNotice);
        mTvName = (TextView) findViewById(R.id.show_notice_info_TvName);
        mTvUploadTime = (TextView) findViewById(R.id.show_notice_info_TvTime);
        mTvContent = (TextView) findViewById(R.id.show_notice_info_TvContent);
        mButton = (Button) findViewById(R.id.show_notice_info_BtnDownload);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String ogjId = null;
        if (intent != null){
            ogjId = intent.getStringExtra("objId");
            intent.clone();

        }
        showProgress("加载中");

        loadDataFromBmob(ogjId);
    }



    private void loadDataFromBmob(String ogjId) {
        Log.d(TAG, "loadDataFromBmob: -------------"+ogjId);

        BmobQuery<Notice> bmobQuery = new BmobQuery<Notice>();

        bmobQuery.getObject(ogjId, new QueryListener<Notice>() {
            @Override
            public void done(Notice notice, BmobException e) {
                if (e == null){
                    //查询成功
                    String time = notice.getCreatedAt();
                    String name = notice.getUploadName();
                    if (notice.getUploadOther() == null){
                        mButton.setVisibility(View.GONE);
                    }else {
                        mButton.setVisibility(View.VISIBLE);
                        mOtherUrl = notice.getUploadOther().getUrl();
                    }


                    String content = notice.getUploadContent();
                    //time   2017-09-23 13:02:56
                    String year = time.substring(0,4);
                    String month = time.substring(5,7);
                    String day = time.substring(8,10);
                    String hm = time.substring(11,16);
                    mTvTime.setText(year+"年"+month+"月"+day+" 群公告");
                    mTvName.setText(name);
                    mTvUploadTime.setText(month+"月"+day+"日 "+hm);
                    mTvContent.setText(content);
                    Log.d(TAG, "done: ++++"+time);
                    Log.d(TAG, "done: ++++"+name);
                    Log.d(TAG, "done: ++++"+mOtherUrl);
                    Log.d(TAG, "done: ++++"+content);

                    hideProgress();

                }else {
                    //查询失败
                    Log.d(TAG, "done: ------------"+e.toString()+" --"+e.getErrorCode());
                    hideProgress();
                }
            }
        });
    }




    @Override
    protected void initListener() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFilePath != null){
                    Intent intent =  OpenFileUtils.openFile(mFilePath);
                    startActivity(intent);
                }else {
                    showProgressBarDialog(ProgressDialog.STYLE_HORIZONTAL);
                    ThreadUtils.runOnBackgroundThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadFile(mOtherUrl);
                        }
                    });
                }
            }
        });

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void downloadFile(String other) {
        DownloadUtil.get().download(other, "classCricle", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFilePath = DownloadUtil.mFile.getPath();
                        showToast("下载成功");
                        mButton.setText("打开附件");

                    }
                });
            }

            @Override
            public void onDownloadProgress(int progress) {
                Log.d(TAG, "onDownloadProgress: ================="+progress);
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

    private String getNameFromUrl(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }

    private void isExistsFile(String classCricle, String nameFromUrl) {
        File f = new File(classCricle);
        List<File> fileList = getFile(f);
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            Log.i("文件名字", file.getName());
            if (nameFromUrl.equals(file.getName())){
                Log.d(TAG, "ExistsFile: ------"+nameFromUrl+"==="+file.getName());
                mButton.setText("打开附件");
                mFilePath = file.getPath();
                Log.d(TAG, "ifExistsFile2: -------------"+mFilePath);
            }
        }
    }
    public List<File> getFile(File file) {
        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isFile()) {
                mFileList.add(f);
            } else {
                getFile(f);
            }
        }
        return mFileList;
    }
    private void showProgressBarDialog(int style){
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
                progressDialogHandler.removeMessages(PROGRESSDIALOG_FLAG);
                progress = 0;
                progressDialog.setProgress(progress);
            }
        });
        progressDialog.show();

        progressDialogHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progress >= C.ALERT_DIALOG_MAX_PROGRESS){
                    //消失 并重置初始值
                    progressDialog.dismiss();
                    progress = 0;
                }else {
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
        progressDialogHandler.sendEmptyMessage(PROGRESSDIALOG_FLAG);

    }
}
