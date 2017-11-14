package com.example.administrator.classcircle.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.LoadCallBack.EmptyCallBack;
import com.example.administrator.classcircle.LoadCallBack.ErrorCallBack;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.LoadCallBack.LoadingCallBack;
import com.example.administrator.classcircle.LoadCallBack.PostUtil;
import com.example.administrator.classcircle.R;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.Convertor;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;

import java.util.Random;


/**
 * Created by Administrator on 2017/9/1 0001.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private InputMethodManager mInputMethodManager;
    private int PROGRESSDIALOG_FLAG = 1;
    private Handler progressDialogHandler;
    private LoadService loadService;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        init();
        initListener();
    }

    protected abstract int getLayoutRes();

    protected abstract void init();

    protected abstract void initListener();

    protected void goTo(Class activity,boolean finish){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
        if (finish){
            finish();
        }else {
            return;
        }
    }
    private Toast mToast;
    public void showToast(String msg){
        if (mToast == null){
            mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
    public void showProgress(String msg){
        if (mProgressDialog ==null){
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }
    public void hideProgress(){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.hide();
        mProgressDialog.dismiss();
    }
    public void hideKeyBoard(){
        if (mInputMethodManager == null){
            mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        }
        mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }



    public void showProgressBarDialog(int style, final int progress){
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
                //progress = 0;
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
                    //progress = 0;
                }else {
                    //  progress++;
                    // progressDialog.incrementProgressBy(1);
                    // 随机设置下一次递增进度 (50 +毫秒)
                    progressDialog.setProgress(progress);
                    progressDialogHandler.sendEmptyMessageDelayed(1, 50 + new Random().nextInt(500));
                }
            }

        };
        // 设置进度初始值
        //progress = (progress > 0) ? progress : 0;
        progressDialog.setProgress(progress);
        // 发送消息
        progressDialogHandler.sendEmptyMessage(PROGRESSDIALOG_FLAG);

    }

    protected void LoadingPager(final HttpResult mHttpResult, int time){
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingCallBack())
                .addCallback(new EmptyCallBack())
                .addCallback(new ErrorCallBack())
                .setDefaultCallback(LoadingCallBack.class)
                .build();
        loadService = loadSir.register(this, new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                loadService.showCallback(LoadingCallBack.class);
                PostUtil.postCallbackDelayed(loadService,SuccessCallback.class);
            }
        }, new Convertor<HttpResult>() {
            @Override
            public Class<? extends Callback> map(HttpResult httpResult) {
                Class<? extends Callback> resultCode = SuccessCallback.class;
                switch (httpResult.getResultCode()) {
                    case 1://成功回调
                        if (httpResult.getData().size() == 0) {
                            resultCode = EmptyCallBack.class;
                        } else {
                            resultCode = SuccessCallback.class;

                        }
                        break;
                    case 0:
                        resultCode = ErrorCallBack.class;
                        break;
                }
                return resultCode;
            }
        });
        // loadService.showWithConvertor(mHttpResult);
        //loadService.showSuccess();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // do net here...

                //callback
                loadService.showWithConvertor(mHttpResult);

            }
        },gettime(time));
    }

    private long gettime(int time) {
        if (time == 0) {
            return 500;
        } else if(time == 1){
            //  Toast.makeText(this, "time", Toast.LENGTH_SHORT).show();
            return 1000;
        }else{
            return 5000;
        }
    }
}
