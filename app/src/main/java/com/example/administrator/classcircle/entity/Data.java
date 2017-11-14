package com.example.administrator.classcircle.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.*;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/9/26 0026.
 */

public class Data extends BmobObject {
    private String name;
    private BmobFile uploadFile;
    private String objId;
    private String fileName;
    private String date;
    private String fileUrl;
    private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(BmobFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }
}
