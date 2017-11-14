package com.example.administrator.classcircle.li;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.entity.ClassOneDayLimitTime;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.widget.HorizontalProgressBarWithNumber;
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

public class VoteDetailActivity extends BaseActivity {
    private ImageView img_head;
    private TextView vote_num;
    private TextView vote_rank;
    private Button btn_vote;
    private int voteNum;
    private int lastVoteNum=-1;
    private String classWords;
    private int voteRank;
    private ClassId classId=new ClassId();
    private HorizontalProgressBarWithNumber progress;
    Date curDate = new Date(System.currentTimeMillis());
private ClassOneDayLimitTime odlt=new ClassOneDayLimitTime();

    @Override
    protected int getLayoutRes() {


        return R.layout.activity_vote_detail;
    }

    @Override
    protected void init() {

        // 显示标题栏左上角的返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 显示标题栏cal
        getSupportActionBar().setTitle("投票页");
        getIntentData();
            initViews();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_create_activity,menu);

        return true;
    }


    private void getIntentData() {
        Intent i=getIntent();
//        if(i.getStringExtra("lastVoteNum")!=null){
//            lastVoteNum=Integer.parseInt(i.getStringExtra("lastVoteNum"));
//        }

        voteNum=Integer.parseInt(i.getStringExtra("voteNum"));
        classWords=i.getStringExtra("classWords");

        voteRank=Integer.parseInt(i.getStringExtra("voteRank"));

    }

    private void initViews() {
        img_head= (ImageView) findViewById(R.id.img_head);
        vote_num= (TextView) findViewById(R.id.vote_num);
        vote_rank= (TextView) findViewById(R.id.vote_rank);
        btn_vote= (Button) findViewById(R.id.btn_vote);
//        if(voteNum==lastVoteNum ){
//           voteRank=voteRank-1;
//            vote_rank.setText(String.valueOf(voteRank));
//
//        }else {
//
//        }
        Glide.with(VoteDetailActivity.this).load(getIntent().getStringExtra("photoUrl")).into(img_head);
        HttpResult httpResult=new HttpResult(1,null);
        vote_rank.setText(String.valueOf(voteRank));
        vote_num.setText(getIntent().getStringExtra("voteNum"));



    }

    @Override
    protected void initListener() {
        btn_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存储第一次投票的时间
                BmobQuery<ClassOneDayLimitTime> bmobquery=new BmobQuery<>();
                bmobquery.findObjects(new FindListener<ClassOneDayLimitTime>() {
                    @Override
                    public void done(final List<ClassOneDayLimitTime> list, BmobException e) {
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
                                        //Toast.makeText(VoteDetailActivity.this, String.valueOf(s), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(VoteDetailActivity.this, "今天你已经投过票了哦，"+"请"+String.valueOf(h)+"时"
                                                +String.valueOf(m)+"分"+String.valueOf(ss)+"秒 后再投", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(VoteDetailActivity.this, ww, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        int votenum=Integer.parseInt(getIntent().getStringExtra("voteNum"))+1;
                                        vote_num.setText(String.valueOf(votenum));
                                        classId.setClassWords(getIntent().getStringExtra("classWords"));
                                        classId.setIdNum(Integer.parseInt(getIntent().getStringExtra("idNum")));
                                        // Toast.makeText(VoteDetailActivity.this,getIntent().getStringExtra("idNum"), Toast.LENGTH_SHORT).show();
                                        classId.setVoteRank(voteRank);
                                        classId.setVoteNum(votenum);
                                        classId.update(getIntent().getStringExtra("objectId"), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Toast.makeText(VoteDetailActivity.this, getIntent().getStringExtra("objectId"), Toast.LENGTH_SHORT).show();
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

//                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            long time= System.currentTimeMillis();
//                            String t1=format.format(time);

//                            long ss=(time-time1)/1000;
//                            Toast.makeText(ActivityDetailPager.this,String.valueOf(ss), Toast.LENGTH_SHORT).show();
                            odlt.setSecond(a);
                            odlt.setStudentId(SplashActivity.mLoginUserName);
                            odlt.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e==null){
                                        Toast.makeText(VoteDetailActivity.this, "投票成功ee", Toast.LENGTH_SHORT).show();

                                        int votenum=Integer.parseInt(getIntent().getStringExtra("voteNum"))+1;
                                        vote_num.setText(String.valueOf(votenum));
                                        classId.setClassWords(getIntent().getStringExtra("classWords"));
                                        classId.setIdNum(Integer.parseInt(getIntent().getStringExtra("idNum")));
                                        // Toast.makeText(VoteDetailActivity.this,getIntent().getStringExtra("idNum"), Toast.LENGTH_SHORT).show();
                                        classId.setVoteRank(voteRank);
                                        classId.setVoteNum(votenum);
                                        classId.update(getIntent().getStringExtra("objectId"), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Toast.makeText(VoteDetailActivity.this, getIntent().getStringExtra("objectId"), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }else{
                                        Toast.makeText(VoteDetailActivity.this, "error1", Toast.LENGTH_SHORT).show();
                                        Log.e("BBB",e.toString());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(VoteDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
                            Log.e("AAA",e.toString());
                        }
                    }
                });






























                //投一次后就要24小时后才能再投
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                Date endDate = new Date(System.currentTimeMillis());
//                 long diff = endDate.getTime() - curDate.getTime();
//                long miute=diff/1000;

//                int votenum=voteNum+1;
//                vote_num.setText(String.valueOf(votenum));
//                classId.setClassWords(getIntent().getStringExtra("classWords"));
//                classId.setIdNum(Integer.parseInt(getIntent().getStringExtra("idNum")));
//               // Toast.makeText(VoteDetailActivity.this,getIntent().getStringExtra("idNum"), Toast.LENGTH_SHORT).show();
//                classId.setVoteRank(voteRank);
//                classId.setVoteNum(votenum);
//                classId.update(getIntent().getStringExtra("objectId"), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if(e==null){
//                      Toast.makeText(VoteDetailActivity.this, getIntent().getStringExtra("objectId"), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });



            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }else if(item.getItemId()==R.id.item_activity){

        }




        return true;
    }
}
