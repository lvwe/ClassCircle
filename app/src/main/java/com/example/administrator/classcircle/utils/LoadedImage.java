package com.example.administrator.classcircle.utils;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class LoadedImage {
    Bitmap mBitmap;
    public LoadedImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }
    public Bitmap getBitmap(){
        return mBitmap;
    }
}
