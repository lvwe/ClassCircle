package com.example.administrator.classcircle.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.utils.FileUtils;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.entity.Notice;

import java.io.File;
import java.util.Random;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class NoticeActivity extends BaseActivity {

    private static final String TAG = "NoticeActivity";
    private EditText mEditText;
    private EditText mEditTextAdd;
    private Button mButtonConfirm;
    private Button mButtonCancel;
    private TextView mTextView;
    private Button mButtonOther;
    private String mPath;
    private Uri mUri;
    private Uri mUri1;
    private int progress = 0;
    private Handler progressDialogHandler;
    private int PROGRESSDIALOG_FLAG = 1;
    private int uploadValue = 0;
    private TextView mTvBack;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_notice;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));
        mEditText = (EditText) findViewById(R.id.id_notice_edt);

        mButtonConfirm = (Button) findViewById(R.id.id_notice_btnConfirm);
        mButtonCancel = (Button) findViewById(R.id.id_notice_cancel);
        mEditTextAdd = (EditText) findViewById(R.id.id_notice_edt_add);
        mTextView = (TextView) findViewById(R.id.id_header_tv);
        mTextView.setText("发布通知");
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initListener() {

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String content = mEditText.getText().toString();
                if (content.length() >= 5) {
                    uploadBmob();
                } else {
                    showConfirmAlertDialog();
                }

            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("提示", "确定要取消发布通知吗", "确定", "取消");
            }
        });

        mEditTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), C.FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast("Please install a File Manager");
                }
            }
        });
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                finish();
            }
        });

    }

    private void uploadBmob() {
        String content = mEditText.getText().toString();
        final BmobFile bmobFile;
        final Notice notice = new Notice();
        notice.setUploadName(SplashActivity.mLoginUserName);
        notice.setUploadContent(content);
        notice.setClassId(SplashActivity.mClassID);
        if (mPath != null) {
            bmobFile = new BmobFile(new File(mPath));
            showProgressBarDialog(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            bmobFile = new BmobFile();
        }
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    showToast("上传文件成功:" + bmobFile.getFileUrl());
                    notice.setUploadOther(bmobFile);
                    notice.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                Log.d(TAG, "done: --------------上传成功");

                            } else {
                                Log.d(TAG, "done: --------------上传失败" + e.getErrorCode() + "  " + e.toString());

                            }
                        }
                    });
                    showAlertDialog("", "通知发布完成", "确定", "");
                    Log.d(TAG, "done: -------------" + bmobFile.getFileUrl());
                } else {
                    notice.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                Log.d(TAG, "done: --------------上传成功");
                                showAlertDialog("", "通知发布完成", "确定", "");

                            } else {
                                Log.d(TAG, "done: --------------上传失败" + e.getErrorCode() + "  " + e.toString());
                                hideProgress();
                            }
                        }
                    });
//                    showToast("上传文件失败:"  + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                Log.d(TAG, "onProgress: " + value);
                uploadValue = value;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case C.FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri mUri1 = data.getData();

                    mPath = FileUtils.getPath(this, mUri1);
                    File f = new File(mPath);
                    try {
                        if (FileUtils.getFileSize(f) > 10485760) {
                            showToast("文件不可以大于10M");
                            return;
                        }
                        Log.d(TAG, "onActivityResult: =====" + data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFileName(mPath);

                    Log.d(TAG, "onActivityResult: ++++++++++++++++++" + mPath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    dat=file:///storage/emulated/0/3296887429.mp4
    private void getFileName(String path) {
        String[] strs = path.split("/");
        String fileName = strs[strs.length - 1].toString();
        mEditTextAdd.setText(fileName + "/size:" + FileUtils.checkFileSize(path));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showAlertDialog("", "确定要取消发布通知吗", "确定", "取消");
        }
        return false;
    }

    private void showAlertDialog(String title, String msg, String pos, String nav) {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle(title);
        // 设置对话框消息
        isExit.setMessage(msg);
        // 添加选择按钮并注册监听
        isExit.setButton(AlertDialog.BUTTON_POSITIVE, pos, listener);
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE, nav, listener);
        // 显示对话框
        isExit.show();
    }

    private void showConfirmAlertDialog() {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("提示");
        // 设置对话框消息
        isExit.setMessage("通知不能少于五个字");
        // 添加选择按钮并注册监听
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "确定", listener);
        // 显示对话框
        isExit.show();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;

                default:
                    break;

            }
        }
    };

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
                progressDialogHandler.removeMessages(PROGRESSDIALOG_FLAG);
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
        progressDialogHandler.sendEmptyMessage(PROGRESSDIALOG_FLAG);

    }
}
