package com.example.administrator.classcircle.activity;

import android.accounts.NetworkErrorException;
import android.os.Handler;
import android.util.Log;

import com.example.administrator.classcircle.C;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.entity.User;
import com.example.administrator.classcircle.li.CreateClassActivity;
import com.example.administrator.classcircle.utils.SharePreUtil;
import com.example.administrator.classcircle.utils.ThreadUtils;
import com.hyphenate.chat.EMClient;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    public static String mLoginUserName;
    public static String mClassID;
    public static String mObjID;
    public static String mImgUrl;

    private Handler mHandler = new Handler();


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFistRun = SharePreUtil.isFirstRun(getApplicationContext());
                if (isFistRun) {
                    try {
                        checkLoginState();
                    }catch (NetworkErrorException e){
                        showToast("网络连接超时");
                    }
                    goTo(GuideActivity.class, true);
                    SharePreUtil.saveFirstRun(getApplicationContext(), true);
                } else {
                    try {
                        checkLoginState();
                    }catch (NetworkErrorException e){
                        showToast("网络连接超时");
                    }
                }
            }
        }, C.SPLASH_DELAY_TIME);
    }

    private void checkLoginState() throws NetworkErrorException{
        if (EMClient.getInstance().isLoggedInBefore()) {
            mLoginUserName = EMClient.getInstance().getCurrentUser();
            queryMyInfo(mLoginUserName);
        } else {
            goTo(LoginActivity.class, true);
        }
    }

    private void queryMyInfo(final String userName) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("userName", userName);
        query.setLimit(50);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (User user : list) {
                        mClassID = user.getClassId();
                        mObjID = user.getObjectId();
                        if (user.getPic() != null) {
                            mImgUrl = user.getPic().getUrl();
                        }
                        if (mClassID.length() <= 0) {
                            goTo(CreateClassActivity.class, true);
                            return;
                        } else {
                            goTo(MainActivity.class, true);
                        }
                    }
                } else {
                    Log.d(TAG, "done: ----查询失败" + e.toString());
                    goTo(LoginActivity.class, true);

                }
            }

        });
    }

    @Override
    protected void initListener() {
    }
}
