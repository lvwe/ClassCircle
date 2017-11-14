package com.example.administrator.classcircle.li;

import android.os.Handler;

import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends BaseActivity {
    private int index;
    private Handler mHandler = new Handler();
    private final static int DELAY_TIME = 2000;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        check_has_or_no_class();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(index==0){
                    goTo(CreateClassActivity.class);
                }else{
                    goTo(CreateClassActivity.class);
                }

            }
        },DELAY_TIME);
    }

    private void check_has_or_no_class() {
        BmobQuery<ClassId> query = new BmobQuery<>();
        query.addQueryKeys("objectId");
        query.findObjects(new FindListener<ClassId>() {
            @Override

            public void done(List<ClassId> list, BmobException e) {
                if (e== null) {

                    //HttpResult resultCode=new HttpResult(1,list);

                    index = list.size();
//                    ObjectId = list.get(index).getObjectId();


                }

            }
        });
    }

    @Override
    protected void initListener() {

    }


}
