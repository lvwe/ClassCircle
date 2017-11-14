package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/9/26 0026.
 */

public class FileUtils {

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);

                }
            } catch (Exception e) {
                // Eat it
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {

            return uri.getPath();
        }

        return null;
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    //    dat=file:///storage/emulated/0/3296887429.mp4
    public static void getFileName(String path) {
        String[]  strs=path.split("/");
        String fileName = strs[strs.length-1].toString();
//        mEditText.setText(fileName+"/size:"+checkFileSize(path));
    }
    public static String checkFileSize(String path) {
        File f = new File(path);
        String fileSizeString = "";
        try {
            long fileS = FileUtils.getFileSize(f);
            DecimalFormat df = new DecimalFormat("#.00");

            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "K";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "M";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "G";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSizeString;
    }

    //fileName-->IMG_20170918_154100.jpg
    public static String getExtension(String filename) {
        String type = "";
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = filename.substring(dotIndex+1, filename.length()).toLowerCase();
        if (end == "") return type;
        return end;
    }
}
