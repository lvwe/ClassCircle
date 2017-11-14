package com.example.administrator.classcircle.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/9/22 0022.
 */

public class Notice extends BmobObject {
    private String uploadName;
    private String uploadContent;
    private BmobFile uploadOther;
    private String uploadTime;private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }


    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    private String objId;

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getUploadContent() {
        return uploadContent;
    }

    public void setUploadContent(String uploadContent) {
        this.uploadContent = uploadContent;
    }

    public BmobFile getUploadOther() {
        return uploadOther;
    }

    public void setUploadOther(BmobFile uploadOther) {
        this.uploadOther = uploadOther;
    }
}
