package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/10/8 0008.
 */

public class ToastUtils {
    static Toast sToast;
    public static void show(Context context,String text){
        try {
        if (sToast != null){
            sToast.setText(text);
        }else {
            sToast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        }
        sToast.show();
        }catch (Exception e){//子线程中Toast异常情况处理
            Looper.prepare();
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}
