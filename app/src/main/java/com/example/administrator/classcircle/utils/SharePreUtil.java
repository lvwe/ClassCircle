package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class SharePreUtil {
    private static final String SHARED_PREFERENCES_NAME = "fist_run_sp";
    private static final String FIRST_RUN = "first_run";
    private static final String USER_NAME = "USER_NAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String IS_STUDENT = "IS_STUDENT";

    public static boolean isFirstRun(Context context){
        SharedPreferences sf = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE
        );
        return sf.getBoolean(FIRST_RUN,true);
    }
    public static void saveFirstRun(Context context,boolean b){
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FIRST_RUN,false);
        editor.commit();
    }

}
