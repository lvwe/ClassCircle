package com.example.administrator.classcircle.entity;

/**
 * Created by Administrator on 2017/9/11 0011.
 */

public class ClassInfo {

    private LoadedImage img;
    private String className;
    private String createTime;
    private int voteNum;
    private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public ClassInfo(){
        super();
    }
    public ClassInfo(LoadedImage img, String className, String createTime, int voteNum){
        this.className = className;
        this.createTime = createTime;
        this.img = img;
        this.voteNum = voteNum;
    }

    public LoadedImage getImg() {
        return img;
    }

    public void setLoadedImage(LoadedImage img) {
        this.img = img;
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

    public int getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(int voteNum) {
        this.voteNum = voteNum;
    }
}
