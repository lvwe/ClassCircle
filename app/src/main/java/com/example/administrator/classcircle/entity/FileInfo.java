package com.example.administrator.classcircle.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/6 0006.
 */

public class FileInfo implements Serializable {

    public static final String EXTEND_APK = ".apk";
    public static final String EXTEND_JPEG = ".jpeg";
    public static final String EXTEND_JPG = ".jpg";
    public static final String EXTEND_PNG = ".png";
    public static final String EXTEND_MP3 = ".mp3";
    public static final String EXTEND_MP4 = ".mp4";

    public static final int TYPE_APK = 1;
    public static final int TYPE_JPG = 2;
    public static final int TYPE_MP3 = 3;
    public static final int TYPE_MP4 = 4;


    public static final int FLAG_SUCCESS = 1;
    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_FAILURE = -1;

    private String filePath;
    private int fileType;
    private long size;
    private String name;
    private String sizeDesc;
    private Bitmap bitmap;
    private String extra;
    private long procceed;
    private int result;

    public FileInfo(){};

    public FileInfo(String filePath, long size) {
        this.filePath = filePath;
        this.size = size;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getProcceed() {
        return procceed;
    }

    public void setProcceed(long procceed) {
        this.procceed = procceed;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSizeDesc() {
        return sizeDesc;
    }

    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = sizeDesc;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
