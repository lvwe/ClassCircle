package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.*;

public class showTextzxingContentActivity extends com.example.administrator.classcircle.activity.BaseActivity {
private TextView textView;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_textzxing_content;
    }

    @Override
    protected void init() {
        textView= (TextView) findViewById(R.id.textView_content);
        Intent intent=getIntent();
        textView.setText(intent.getStringExtra("result"));

    }

    @Override
    protected void initListener() {

    }
}
