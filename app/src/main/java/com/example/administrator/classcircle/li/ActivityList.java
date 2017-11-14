package com.example.administrator.classcircle.li;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.LoadCallBack.HttpResult;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.adapter.ActivitiesAdapter;
import com.liaoinstan.springview.widget.SpringView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ValueEventListener;

public class ActivityList extends BaseActivity {
    private SpringView activity_spriview;
    private ListView mListView;
    //private ActivityListsAdapter mAdapter;
    private ActivitiesAdapter mAdapter;
    private RecyclerView mrecyerView;
    private BmobRealTimeData brt=new BmobRealTimeData();
    private List<ActivityLists> activityListsList=new ArrayList<>();
    private List<String> objectIdList=new ArrayList<>();

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_list;
    }

    @Override
    protected void init() {
        initViews();
        getNewestData();

    }

    private void getNewestData() {
        brt.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                if(e==null){
                    brt.subTableUpdate("ActivityLists");
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
                getdatafromBmob();
            }
        });
    }

    private void initViews() {
        activity_spriview= (SpringView) findViewById(R.id.activity_spriView);
        mrecyerView= (RecyclerView) findViewById(R.id.activity_listView);
        getdatafromBmob();

    }

    private void getdatafromBmob() {
        BmobQuery<ActivityLists> als=new BmobQuery<>();
        als.findObjects(new FindListener<ActivityLists>() {
            @Override
            public void done(List<ActivityLists> list, BmobException e) {
                if(e==null){

                    //start
                    List<ClassId> l=new ArrayList<ClassId>();
                    ClassId c=new ClassId();
                    l.add(c);
                    //end
                    HttpResult hr=new HttpResult(1,l);
                    LoadingPager(hr,0);


                    activityListsList.clear();
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


                                ActivityLists activityLists=new ActivityLists();

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
                                activityLists.setName(list.get(i).getName());
                                // Toast.makeText(mActivity,String.valueOf(list.get(i).getIdNum()), Toast.LENGTH_SHORT).show();
                                activityLists.setVoteNum(list.get(i).getVoteNum());
                                activityLists.setTitle(list.get(i).getTitle());
                                activityLists.setImgUrl(list.get(i).getImgUrl());
                                activityLists.setCreateTime(list.get(i).getCreateTime());
                                activityLists.setExplain(list.get(i).getExplain());
                                activityLists.setFromClass(list.get(i).getFromClass());
                                activityListsList.add(activityLists);
                                objectIdList.add(list.get(i).getObjectId());

                            }

                        }


                    }

                    mAdapter=new ActivitiesAdapter(activityListsList,ActivityList.this,objectIdList);
                    LinearLayoutManager manager=new LinearLayoutManager(ActivityList.this);
                    mrecyerView.setLayoutManager(manager);
                    mrecyerView.setAdapter(mAdapter);
                }

            }
        });

    }

    @Override
    protected void initListener() {

    }
}
