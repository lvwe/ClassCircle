package com.example.administrator.classcircle.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/12 0012.
 */

public class Msg extends BmobObject {

    public static final int TYPE_RECEIVED=1;
    public static final int TYPE_SEND=0;
    private String content;
    private String name;
    private String ImgUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    private String studentId;







    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
