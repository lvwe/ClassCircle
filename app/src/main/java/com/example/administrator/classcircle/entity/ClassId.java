package com.example.administrator.classcircle.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/9/11 0011.
 */

public class ClassId extends BmobObject{


    public int getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(int voteNum) {
        this.voteNum = voteNum;
    }
    private String classWords;

    public int getVoteRank() {
        return voteRank;
    }

    public void setVoteRank(int voteRank) {
        this.voteRank = voteRank;
    }

    public String getClassWords() {
        return classWords;
    }

    public void setClassWords(String classWords) {
        this.classWords = classWords;
    }

    private int voteRank;
    private int voteNum;
    private String className;
    private String createTime;
    private int IdNum;
     private List<BmobFile> photoPath;






    public List<BmobFile> getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(List<BmobFile> photoPath) {
        this.photoPath = photoPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIdNum() {
        return IdNum;
    }

    public void setIdNum(int IdNum) {
        this.IdNum = IdNum;
    }
}
