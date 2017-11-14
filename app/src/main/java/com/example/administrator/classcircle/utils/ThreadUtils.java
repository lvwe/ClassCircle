package com.example.administrator.classcircle.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class ThreadUtils {
    private static Executor sExecutor = Executors.newSingleThreadExecutor();
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void runOnBackgroundThread(Runnable runnable){
        sExecutor.execute(runnable);
    }
    public static void runUiThread(Runnable runnable){
        sHandler.post(runnable);
    }
}
