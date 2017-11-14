package com.example.administrator.classcircle.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.li.ActivityList;
import com.example.administrator.classcircle.li.ChooseActivity;
import com.example.administrator.classcircle.li.VoteDetailActivity;
import com.example.administrator.classcircle.adapter.ListViewAdapter;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class Fragment01 extends BaseFragment {
    private boolean IsFirstSetAdapter=true;
    private SpringView mspringView;
    private PopupWindow mpopuWindow;
    private static final int REQUEST_CODE=1;
    private ImageView img_menu;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ClassId> myClassList = new ArrayList<>();
    private BmobRealTimeData brt=new BmobRealTimeData();
    private List<String> objectIdList=new ArrayList<>();
    private boolean isshowing=true;
    private  boolean HASSHOWPOPUPWINDOW=false;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_01;
    }

    @Override
    protected void initView() {

        mspringView= (SpringView) mRootView.findViewById(R.id.spriView);
        mspringView.setHeader(new DefaultHeader(getContext()));
        mspringView.setFooter(new DefaultFooter(getContext()));

        img_menu = (ImageView) mRootView.findViewById(R.id.img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isshowing){
                    isshowing=!isshowing;
                    showpopuwindow();
                }else{
                    isshowing=!isshowing;
                    mpopuWindow.dismiss();
                }


            }
        });

        mListView = (ListView) mRootView.findViewById(R.id.list_view);
        getdatafromBmob();
        mspringView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getdatafromBmob();
                        mspringView.onFinishFreshAndLoad();

                    }
                },2000 );
            }

            @Override
            public void onLoadmore() {


            }
        });

    }

       public void showpopuwindow(){
           View view1= LayoutInflater.from(getContext()).inflate(R.layout.activity_menu,null);
           mpopuWindow=new PopupWindow(view1, 350,
                   400);
           mpopuWindow.setContentView(view1);
           TextView textView1= (TextView) view1.findViewById(R.id.create_activity);
           TextView textView2= (TextView) view1.findViewById(R.id.finish_activity);
           textView1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
startActivity(new Intent(getContext(), ChooseActivity.class));

                   isshowing=!isshowing;
                   mpopuWindow.dismiss();
               }
           });
           textView2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   isshowing=!isshowing;
                   mpopuWindow.dismiss();
                   startActivity(new Intent(getContext(),ActivityList.class));
               }
           });
           View view2=LayoutInflater.from(getContext()).inflate(R.layout.fragment_01,null);
           View view3=img_menu;
           mpopuWindow.showAsDropDown(view3);
           //mpopuWindow.showAtLocation(view2, Gravity.TOP,80,30);

       };



    @Override
    protected void initData() {

       getNewestData();
    }

protected void getNewestData(){
    brt.start(new ValueEventListener() {
        @Override
        public void onConnectCompleted(Exception e) {
            if(e==null){
                brt.subTableUpdate("ClassId");
            }
        }

        @Override
        public void onDataChange(JSONObject jsonObject) {
            getdatafromBmob();
            //Toast.makeText(mActivity, "new data", Toast.LENGTH_SHORT).show();
        }
    });
}




    @Override
    protected void initListener() {


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isshowing){
                    isshowing=!isshowing;
                    mpopuWindow.dismiss();
                    return;
                }


                List<ClassId> mClassList=mAdapter.getmStudentList();

                List<String> objectId=mAdapter.getmobjectIdList();
                //Toast.makeText(mActivity,myClassList.get(0).getCreatedAt(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(mActivity,myClassList.get(0).getObjectId(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(getContext(),VoteDetailActivity.class);

                intent.putExtra("photoUrl",mClassList.get(position).getPhotoPath().get(0).getUrl());
                intent.putExtra("objectId",objectId.get(position));
                intent.putExtra("idNum",String.valueOf(mClassList.get(position).getIdNum()));
       // Toast.makeText(mActivity,String.valueOf(mClassList.get(position).getIdNum()), Toast.LENGTH_SHORT).show();
                intent.putExtra("classWords",mClassList.get(position).getClassWords());
                if(position>0){
                    intent.putExtra("lastVoteNum",String.valueOf(mClassList.get(position-1).getVoteNum()));
                }

                intent.putExtra("voteNum",String.valueOf(mClassList.get(position).getVoteNum()));
                intent.putExtra("voteRank",String.valueOf(position + 1));
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){

        }
    }


    private void getdatafromBmob() {
        BmobQuery<ClassId> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ClassId>() {
            @Override
            public void done(final List<ClassId> list, BmobException e) {
                if (e == null) {
                    myClassList.clear();
                    objectIdList.clear();
                   // Toast.makeText(mActivity, list.get(0).getObjectId(), Toast.LENGTH_SHORT).show();
//                    HttpResult hr=new HttpResult(1,list);
//                    LoadingPager(hr);

                    int size=list.size();
                    final int trr[]=new int[size];

                    for(int i=0;i<size;i++){
                        trr[i]=list.get(i).getVoteNum();

                    }
                    //找出最大的票数
                    int temp = 0;
                    for(int i = 0 ; i < size-1; i ++)
                    {
                        for(int j = 0 ;j < size-1-i ; j++)
                        {
                            if(trr[j] >trr[j+1])  //交换两数位置
                            {
                                temp =trr[j];
                                trr[j] = trr[j+1];
                                trr[j+1]= temp;
                            }
                        }
                      }
//Log.e("KKKK",String.valueOf(trr[0])+String.valueOf(trr[1])+String.valueOf(trr[2])+String.valueOf(trr[3]));

                    for(int j = 0 ;j < size ; j++){

                            int count=size;
                      int num=-1 ;

                        int k=size-j-1;
//
//                        if(num ==trr[k]){
//
//                            return;
//                        }

                        for(int i=0;i<count;i++){


                        if(trr[k]==list.get(i).getVoteNum() ){

                            //Toast.makeText(mActivity, "ddd", Toast.LENGTH_SHORT).show();


                            ClassId myclass=new ClassId();

                            num =list.get(i).getVoteNum();
                            //解决票数相同的障碍
                        if(k-1>=0 && trr[k-1]==num){
                            trr[k-1]=10000;
                        }else if(k-2>=0 && trr[k-2]==num){
                            trr[k-2]=10000;
                        }else if(k-3>=0 && trr[k-3]==num){
                            trr[k-3]=10000;
                        }else if(k-4>=0 && trr[k-4]==num){
                            trr[k-4]=10000;
                        }else if(k-5>=0 && trr[k-5]==num){
                            trr[k-5]=10000;
                        }
                            myclass.setIdNum(list.get(i).getIdNum());
    // Toast.makeText(mActivity,String.valueOf(list.get(i).getIdNum()), Toast.LENGTH_SHORT).show();
                            myclass.setVoteNum(list.get(i).getVoteNum());
                            myclass.setVoteRank(list.get(i).getVoteRank());
                            myclass.setClassName(list.get(i).getClassName());
                            myclass.setCreateTime(list.get(i).getCreateTime());
                            myclass.setClassWords(list.get(i).getClassWords());
                            myclass.setPhotoPath(list.get(i).getPhotoPath());
                            myClassList.add(myclass);
                            objectIdList.add(list.get(i).getObjectId());

                        }

                        }


                    }

                        mAdapter = new ListViewAdapter(getContext(),myClassList,objectIdList);
                        mListView.setAdapter(mAdapter);




                }
            }



        });}
}
