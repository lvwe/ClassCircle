package com.example.administrator.classcircle.utils;

import com.example.administrator.classcircle.entity.ClassId;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class GetClassId {
    static List<Integer> listIdNum=new ArrayList<>();

    public static void setClassId(List<Integer> ida){
        listIdNum.addAll(ida);
    }

    public static List<Integer> getClassId(){
        return listIdNum;
    }
    public void getClassIdNum(final String result, final int num) {

        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.addQueryKeys("IdNum");
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if (e == null) {


                }
            }
        });




    }


}
