package com.example.administrator.classcircle.LoadCallBack;

import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.entity.ClassId;

import java.util.List;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class HttpResult {
    public int resultCode;
    public List<ClassId> data;
    public List<ActivityLists> listsList;

    public HttpResult(int resultCode, List<ClassId> data) {
        this.resultCode = resultCode;
        this.data = data;
    }
    public HttpResult( List<ActivityLists> listsList) {

        this.listsList = listsList;
    }

   public int getResultCode() {
        return resultCode;
    }

    public List<ClassId> getData() {
        return data;
    }
}

