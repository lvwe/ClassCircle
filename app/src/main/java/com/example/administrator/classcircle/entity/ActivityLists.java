package com.example.administrator.classcircle.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/2 0002.
 */

public class ActivityLists extends BmobObject{
    String Title;
    String name;
    String FromClass;
    String Explain;
    String ImgUrl;
    String CreateTime;
    int VoteNum;

    public int getVoteNum() {
        return VoteNum;
    }

    public void setVoteNum(int voteNum) {
        VoteNum = voteNum;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromClass() {
        return FromClass;
    }

    public void setFromClass(String fromClass) {
        FromClass = fromClass;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}
