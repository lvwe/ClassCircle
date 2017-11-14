package com.example.administrator.classcircle.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.classcircle.R;

public class PreviewFileActivity extends BaseActivity {
    private static final String TAG = "PreviewFileActivity";


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_file_info;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        Log.d(TAG, "init: -----"+url);

    }

    @Override
    protected void initListener() {

    }
}
