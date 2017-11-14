package com.example.administrator.classcircle.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    private ProgressDialog mProgressDialog;
    private InputMethodManager mInputMethodManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = LayoutInflater.from(getContext()).inflate(getLayoutRes(),container,false);
            initView();
            initListener();
            initData();
        }
        return mRootView;
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    private Toast mToast;
    public void showToast(String msg){
        if (mToast == null){
            mToast = Toast.makeText(getContext(),"",Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
    protected void goTo(Class activity){
        Intent intent = new Intent(getContext(),activity);
        startActivity(intent);
    }

    private void showProgress(String msg){
        if (mProgressDialog ==null){
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }
    private void hideProgress(String msg){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.hide();
    }
    /*private void hideKeyBoard(){
        if (mInputMethodManager == null){
            mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        }
        mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }*/



}
