package com.example.administrator.classcircle.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.classcircle.entity.ClassId;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public  class FindClassImformation {

    static ClassId classid=new ClassId();


    public static ClassId getImformation(){

        BmobQuery<ClassId> bmobQuery=new BmobQuery<>();
        bmobQuery.getObject(getImformation().getObjectId(), new QueryListener<ClassId>() {
            @Override
            public void done(ClassId classId, BmobException e) {
                classid=classId;
            }
        });

        return classid;

    }
    public static void getClassIdNum(final Context mcontext){
        final List<Integer> listIdNum=new ArrayList<>();
        BmobQuery<ClassId> bmobQuery=new BmobQuery<>();
        bmobQuery.addQueryKeys("IdNum");
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(List<ClassId> list, BmobException e) {
                if(e==null){
                    int count=list.size();
//                    Toast.makeText(mcontext,String.valueOf(count),Toast.LENGTH_LONG).show();

//                    if(listIdNum.get(0)==list.get(0).getIdNum()){
//                        return;
//                    }
                    for(int i=0;i<count;i++){
                        listIdNum.add(list.get(i).getIdNum());
      //                Toast.makeText(mcontext,String.valueOf(listIdNum.get(i)),Toast.LENGTH_LONG).show();


                    }
                    GetClassId.setClassId(listIdNum);

                }else{
                    Toast.makeText(mcontext,"no data be found",Toast.LENGTH_LONG).show();
                }


            }
        });





    }
}
