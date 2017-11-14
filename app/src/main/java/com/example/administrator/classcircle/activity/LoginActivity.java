package com.example.administrator.classcircle.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.li.*;
import com.example.administrator.classcircle.utils.ThreadUtils;
import com.example.administrator.classcircle.entity.User;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.adapter.EMAChatClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.IOException;
import java.net.Socket;
import java.security.spec.PSSParameterSpec;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private ImageView mImageView;
    private EditText mUsername;
    private EditText mPassword;
    private Button mButton;
    private String mLoginPassword;
    private boolean isLoginFailed = false;

    private Handler mHandler = new Handler();
    private long mExitTime;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        mImageView = (ImageView) findViewById(R.id.id_login_img);
        mUsername = (EditText) findViewById(R.id.id_login_username);
        mPassword = (EditText) findViewById(R.id.id_login_password);
        mButton = (Button) findViewById(R.id.id_login_btnLogin);
    }

    @Override
    protected void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.mLoginUserName = mUsername.getText().toString().trim();
                mLoginPassword = mPassword.getText().toString().trim();
                if (SplashActivity.mLoginUserName.length() != 0) {
                    if (mLoginPassword.length() == 0) {
                        return;
                    } else {
                        hideKeyBoard();
                        showProgress("正在登录...");
                        isLoginFailed = false;
                        ThreadUtils.runOnBackgroundThread(new Runnable() {
                            @Override
                            public void run() {
                                getDataFromBmob(SplashActivity.mLoginUserName, mLoginPassword);
                            }
                        });
                    }
                    return;
                }
            }
        });
    }

    private void getDataFromBmob(final String mLoginUserName, final String loginPassword) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("userName", mLoginUserName);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        getUserInfo(list, mLoginUserName, loginPassword);
                    } else {
                        userNameNotExist();
                    }
                } else {
                    loginFail();
                }
            }
        });
    }

    private void getUserInfo(List<User> list, String userName, String passWord) {
        for (User user : list) {
            String getBmobPassword = user.getPassword();
            SplashActivity.mClassID = user.getClassId();
            SplashActivity.mObjID = user.getObjectId();
            if (getBmobPassword.equals(passWord)) {
                ThreadUtils.runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        RegisterAndLoginEMClient();
                    }
                });
            } else {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        passwordError();
                    }
                });
            }
        }
    }

    private void RegisterAndLoginEMClient() {
        try {
            EMClient.getInstance().createAccount(SplashActivity.mLoginUserName, mLoginPassword);
            loginEMClient();
        } catch (final HyphenateException e) {
            Log.d(TAG, "RegisterAndLoginEMClient: -----"+e.getErrorCode());
            if (e != null) {
                loginEMClient();
                return;
            } else {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        registerFail();
                    }
                });
            }
        }
    }

    private void loginEMClient() {
        EMClient.getInstance().login(SplashActivity.mLoginUserName, mLoginPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginSuccess();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                ThreadUtils.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 200) {
                            loginSuccess();
                        } else {
                            loginFail();
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    private void userNameNotExist() {
        isLoginFailed = true;
        hideProgress();
        showToast("用户名不存在");
    }

    private void passwordError() {
        isLoginFailed = true;
        hideProgress();
        showToast("密码错误");
    }

    private void loginFail() {
        isLoginFailed = true;
        hideProgress();
        showToast("登录失败");
    }

    private void registerFail() {
        isLoginFailed = true;
        hideProgress();
        showToast("账号或密码错误");
    }

    private void loginSuccess() {
        hideProgress();
        if (SplashActivity.mClassID != null && SplashActivity.mClassID.length() > 0) {
            goTo(MainActivity.class, true);
        } else {
            goTo(CreateClassActivity.class, true);
        }
        this.finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            showToast("再按一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
