package com.example.administrator.classcircle.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyMyClassCardActivity extends BaseActivity {



    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;
    private static final String TAG = "ModifyMyClassCardActivi";
    private boolean changeMyCard = true;
    private TextView mTvBack;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_modify_my_class_card;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));

        mEditText  = (EditText) findViewById(R.id.id_modify_edt);
        mButton = (Button) findViewById(R.id.id_modify_save);
        mTextView = (TextView) findViewById(R.id.id_header_tv);
        mTextView.setText("修改名片");
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initListener() {
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String name = mEditText.getText().toString().trim();
                    showProgress("修改中...");
                    if (uploadBmob(name)){
                        changeFragmentMyCard(name);
                        hideProgress();
                        showToast("修改成功");
                        finish();
                    }else {
                        hideProgress();
                        showToast("修改失败");
                        finish();
                    }
                }
            });

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProgress();
                finish();
            }
        });
    }

    /**
     * 发送广播
     * @param name
     */
    private void changeFragmentMyCard(String name) {
        Intent intent = new Intent();
        intent.setAction("com.example.administrator.classcircle.MY_MODIFY_CARD_BROAD");
        intent.putExtra("name",name);
        sendBroadcast(intent);
    }


    /**
     * 将修改名字长传bmob服务器
     * @param name
     * @return
     */
    private boolean uploadBmob(String name) {
        User user = new User();
        user.setMyClassCard(name);
        user.update(SplashActivity.mObjID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    changeMyCard = true;
                }else {
                    changeMyCard = false;
                }
            }
        });

        Log.d(TAG, "done: -===changeMyCARD2"+changeMyCard);
        return changeMyCard;

    }


}
