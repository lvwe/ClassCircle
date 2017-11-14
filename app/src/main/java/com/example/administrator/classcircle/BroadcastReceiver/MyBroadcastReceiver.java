package com.example.administrator.classcircle.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.administrator.classcircle.fragment.Fragment02;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: --myName"+intent.getStringExtra("name"));
        Fragment02.mTvMyName.setText(intent.getStringExtra("name"));
    }
}
