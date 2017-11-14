package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class PixelUtil {

    private static Context mContext;

    public static void initContent(Context context) {
        mContext = context;
    }

    ;

    public static int dp2px(float value) {
        float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    public static int dp2px(float value, Context context) {
        float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    public static int px2dp(float value) {
        float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    public static int px2dp(float value, Context context) {
        float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    public static int sp2px (float value) {
        Resources r;
        if (mContext == null){
            r = Resources.getSystem();
        }else {
            r = mContext.getResources();
        }
        float spValue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spValue + 0.5f);
    }

    public static int px2sp(float value) {
        float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    public static int px2sp(float value, Context context) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }



}
