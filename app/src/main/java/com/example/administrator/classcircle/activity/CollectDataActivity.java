package com.example.administrator.classcircle.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.BaseActivity;
import com.example.administrator.classcircle.activity.LoginActivity;
import com.example.administrator.classcircle.entity.Data;
import com.example.administrator.classcircle.entity.Notice;
import com.example.administrator.classcircle.utils.FileUtils;

import java.io.File;
import java.util.Random;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class CollectDataActivity extends BaseActivity {
    private static final String TAG = "CollectDataActivity";

    private EditText mEditText;
    private TextView mTvTitle;
    private Button mButtonConfirm;
    private Button mButtonCancel;
    private String mPath;

    private int progress = 0;
    private Handler progressDialogHandler;
    private int PROGRESSDIALOG_FLAG = 1;
    private int uploadValue = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_collect_data;
    }

    @Override
    protected void init() {
        mEditText = (EditText) findViewById(R.id.collect_data_add);
        mButtonConfirm = (Button) findViewById(R.id.collect_data_btnConfirm);
        mButtonCancel = (Button) findViewById(R.id.collect_data_btnCancel);
        mTvTitle = (TextView) findViewById(R.id.id_header_tv);
        mTvTitle.setText("上传文件");

    }

    @Override
    protected void initListener() {
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), C.FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast("Please install a File Manager");
                }


            }
        });
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPath == null || mEditText.getText().equals("")){
                    showToast("请选择文件");
                }else {
                    uploadDataToBmob();
                    showProgressBarDialog(ProgressDialog.STYLE_HORIZONTAL);
                }
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void uploadDataToBmob() {

        final BmobFile bmobFile;
        bmobFile = new BmobFile(new File(mPath));
        final Data data = new Data();
        data.setName(SplashActivity.mLoginUserName);
        data.setClassId(SplashActivity.mClassID);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //文件上传成功
                    data.setUploadFile(bmobFile);
                    data.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                //上传成功
                                Log.d(TAG, "done: ++++++文件上传成功objId:"+s);
                                uploadValue = 0;
                                showAlertDialog("","文件上传成功","","确定");
                            }else {
                                //上传失败
                                Log.d(TAG, "done: +++++++"+e.getErrorCode()+"--"+e.toString());
                                showAlertDialog("","文件上传失败","","确定");
                            }
                        }
                    });

                }else {
                    //文件上传失败
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                Log.d(TAG, "onProgress: ====="+value);
                uploadValue = value;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case C.FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri mUri1 =  data.getData();
                    mPath = FileUtils.getPath(this, mUri1);
                    File f = new File(mPath);
                    try {
                        if (FileUtils.getFileSize(f) > 10485760){
                            showToast("文件不可以大于10M");
                            return;
                        }
                        Log.d(TAG, "onActivityResult: ====="+data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFileName(mPath);
                    Log.d(TAG, "onActivityResult: ++++++++++++++++++"+ mPath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    dat=file:///storage/emulated/0/3296887429.mp4
    private void getFileName(String path) {
        String[]  strs=path.split("/");
        String fileName = strs[strs.length-1].toString();
        mEditText.setText(fileName+"/size:"+ FileUtils.checkFileSize(path));
    }

    private void showAlertDialog(String title,String msg,String pos,String nav) {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle(title);
//        isExit.setIcon(R.drawable.file_download);
        // 设置对话框消息
        isExit.setMessage(msg);
        // 添加选择按钮并注册监听
        isExit.setButton(AlertDialog.BUTTON_POSITIVE,pos, listener);
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE,nav, listener);
        // 显示对话框
        isExit.show();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case AlertDialog.BUTTON_POSITIVE:
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    mEditText.setText("");
                    mPath = null;
                    break;

                default:
                    break;

            }
        }
    };

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
                if (progress == C.ALERT_DIALOG_MAX_PROGRESS){
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
