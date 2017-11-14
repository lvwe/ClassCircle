package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.classcircle.LoadCallBack.EmptyCallBack;
import com.example.administrator.classcircle.LoadCallBack.ErrorCallBack;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.LoadCallBack.LoadingCallBack;
import com.example.administrator.classcircle.LoadCallBack.PostUtil;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.Convertor;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private LoadService loadService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
//        Bmob.initialize(this, "03754392fcddae692e757dc580b72bc2");
        init();
        initListener();
    }







    protected abstract int getLayoutRes();

    protected abstract void init();

    protected abstract void initListener();

    protected void goTo(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
        finish();
    }
    protected void LoadingPager(final HttpResult mHttpResult,int time){
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
        } else {
          //  Toast.makeText(this, "time", Toast.LENGTH_SHORT).show();
            return 5000;
        }
    }
}
