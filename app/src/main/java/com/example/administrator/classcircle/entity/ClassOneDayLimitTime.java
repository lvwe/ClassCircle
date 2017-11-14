package com.example.administrator.classcircle.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class ClassOneDayLimitTime extends BmobObject{

    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    private String second;

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
