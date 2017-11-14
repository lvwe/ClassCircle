package com.example.administrator.classcircle.li;


import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.*;

public class CreateClassActivity extends BaseActivity {

    private Button create_class;
    private Button join_class;



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_create_class;
    }

    @Override
    protected void init() {
//        LoadSir loadSir = new LoadSir.Builder()
//                .addCallback(new EmptyCallBack())
//                .addCallback(new TimeOutCallBack())
//                .addCallback(new LoadingCallBack())
//                .setDefaultCallback(LoadingCallBack.class)
//                .build();
//        loadService= loadSir.getDefault().register(this, new Callback.OnReloadListener() {
//            @Override
//            public void onReload(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadService.showCallback(LoadingCallBack.class);
//                        SystemClock.sleep(500);
//                        loadService.showSuccess();
//                    }
//                }).start();
//
//            }
//        });
//        loadNet();
        initview();

    }

    private void initview() {
       create_class= (Button) findViewById(R.id.create_class);
       join_class= (Button) findViewById(R.id.join_class);

    }
//    protected void loadNet() {
//        // 进行网络访问...
//        // 进行回调
//        loadService.showSuccess();//成功回调
//      //  loadService.showCallback(EmptyCallBack.class);//其他回调
//    }
    @Override
    protected void initListener() {
        //创建班级
        create_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CreateClassActivity.this,AddClass.class));





            }
        });

        //加入班级;
        join_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateClassActivity.this,ClassZxingActivity.class));
            }
        });

    }





}
