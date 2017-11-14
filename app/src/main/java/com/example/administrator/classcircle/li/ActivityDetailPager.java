package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.entity.OneDayLimitTime;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.BaseActivity;
import com.example.administrator.classcircle.activity.SplashActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ActivityDetailPager extends BaseActivity {
    public TextView activity_detail_name;
    public TextView activity_detail_title;
    public TextView activity_detail_time;
    public TextView activity_detail_class;
    public TextView activity_detail_meaning;
    public TextView activity_detail_num;
    public ImageView activity_detail_img;
    private Button activity_detail_vote;
    private String objectId;
   private  boolean isfirstVote=true;
    private int voteNum;
    private ActivityLists als=new ActivityLists();
    private OneDayLimitTime odlt=new OneDayLimitTime();




    @Override
    protected int getLayoutRes() {
        return R.layout.activity_detail_pager;
    }

    @Override
    protected void init() {
        getObject();
        initViews();

    }

    private void getObject() {
        Intent i=getIntent();
        objectId=i.getStringExtra("objectId");
    }

    private void initViews() {
        activity_detail_class= (TextView) findViewById(R.id.activity_detail_class);
        activity_detail_img= (ImageView) findViewById(R.id.activity_detail_img);
        activity_detail_meaning= (TextView) findViewById(R.id.activity_detail_meaning);
        activity_detail_name= (TextView) findViewById(R.id.activity_detail_name);
        activity_detail_time= (TextView)findViewById(R.id.activity_detail_time);
        activity_detail_title= (TextView)findViewById(R.id.activity_detail_title);
        activity_detail_num= (TextView)findViewById(R.id.activity_detail_voteNum);
        activity_detail_vote= (Button) findViewById(R.id.activity_detail_vote);

        //初始化数据
        activity_detail_class.setText(getIntent().getStringExtra("fromClass"));
        activity_detail_meaning.setText(getIntent().getStringExtra("meaning"));
        activity_detail_time.setText(getIntent().getStringExtra("time"));
        activity_detail_name.setText(getIntent().getStringExtra("name"));
        activity_detail_title.setText(getIntent().getStringExtra("title"));
        activity_detail_num.setText(getIntent().getStringExtra("voteNum"));
        Glide.with(this).load(getIntent().getStringExtra("img")).into(activity_detail_img);






    }

    @Override
    protected void initListener() {
        activity_detail_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //存储第一次投票的时间
                BmobQuery<OneDayLimitTime> bmobquery=new BmobQuery<>();
                bmobquery.findObjects(new FindListener<OneDayLimitTime>() {
                 @Override
                 public void done(final List<OneDayLimitTime> list, BmobException e) {
                        if(e==null){

                            for(int i=0;i<list.size();i++){

                                if(list.get(i).getStudentId().equals(SplashActivity.mLoginUserName)){
                                    //已经投过票，判断投票时间有没有超过一天
                                   //Date curDate = new Date(System.currentTimeMillis());

                                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    long time1= System.currentTimeMillis();
                                    //String t1=format.format(time1);
                                    Date time3= null;
                                    try {
                                        time3 = format.parse(list.get(i).getSecond());
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                   long time4=time3.getTime();

                                    long di=time1- time4;

//                                    Toast.makeText(ActivityDetailPager.this, "wo", Toast.LENGTH_SHORT).show();
//                                    long diff = di - Long.parseLong(list.get(i).getSecond());
//                                    Toast.makeText(ActivityDetailPager.this, String.valueOf(diff), Toast.LENGTH_SHORT).show();

                                    long s=di/1000;
                                   // long realS=s-67320;
                                    long oneDay=24*3600;
                                    long nextTime=oneDay-s;
                                    long h=nextTime/3600;
                                    long m=nextTime%3600/60;
                                    long ss=nextTime%3600%60;

                                    if(s<oneDay){
                                        //Toast.makeText(ActivityDetailPager.this, String.valueOf(s), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(ActivityDetailPager.this, "今天你已经投过票了哦，"+"请"+String.valueOf(h)+"时"
                                                +String.valueOf(m)+"分"+String.valueOf(ss)+"秒  后再投"

                                                , Toast.LENGTH_SHORT).show();
                                        return ;
                                    }else{

                                        //再次投票，刷新时间，重新定义再次投票完到24小时的时间
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date curDate = new Date(System.currentTimeMillis());
                                        String a= df.format(curDate);

                                        odlt.setStudentId(SplashActivity.mLoginUserName);
                                        odlt.setSecond(a);
                                        final String ww=list.get(i).getObjectId();
                                        odlt.update( list.get(i).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Toast.makeText(ActivityDetailPager.this, ww, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        int votenum=Integer.parseInt(getIntent().getStringExtra("voteNum"))+1;
                                        activity_detail_num.setText(String.valueOf(votenum));
                                        als.setVoteNum(votenum);
                                        als.update(objectId, new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){

                                                    // Toast.makeText(ActivityDetailPager.this, objectId, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        return;
                                    }




                                }

                            }


                            //还没投过票，开始存储投票信息
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           Date curDate = new Date(System.currentTimeMillis());
                            String a= df.format(curDate);

                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long time= System.currentTimeMillis();
                            String t1=format.format(time);

//                            long ss=(time-time1)/1000;
//                            Toast.makeText(ActivityDetailPager.this,String.valueOf(ss), Toast.LENGTH_SHORT).show();
                            odlt.setSecond(a);
                            odlt.setStudentId(SplashActivity.mLoginUserName);
                                odlt.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            Toast.makeText(ActivityDetailPager.this, "投票成功ee", Toast.LENGTH_SHORT).show();

                                            int votenum=Integer.parseInt(getIntent().getStringExtra("voteNum"))+1;
                                            activity_detail_num.setText(String.valueOf(votenum));
                                            als.setVoteNum(votenum);
                                            als.update(objectId, new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){

                                                        // Toast.makeText(ActivityDetailPager.this, objectId, Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                        }else{
                                            Toast.makeText(ActivityDetailPager.this, "error1", Toast.LENGTH_SHORT).show();
                                            Log.e("BBB",e.toString());
                                        }
                                    }
                                });
                            }else{
                            Toast.makeText(ActivityDetailPager.this, "error", Toast.LENGTH_SHORT).show();
                            Log.e("AAA",e.toString());
                        }
                 }
             });
            }
        });

    }
}
