package com.example.administrator.classcircle.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.adapter.MyClassMemberRecyclerViewAdapter;
import com.example.administrator.classcircle.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyClassMemberActivity extends BaseActivity {
    private static final String TAG = "MyClassMemberActivity";


    private RecyclerView mRecyclerView;
    private MyClassMemberRecyclerViewAdapter mAdapter;
    private List<User> mUserList = new ArrayList<>();
    private TextView mTvTitle;
    private FrameLayout mFrameLayoutLoading;
    private TextView mTvBack;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_my_class_member;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));


        mUserList.clear();
        loadUserFromBmob();

        mRecyclerView = (RecyclerView) findViewById(R.id.id_class_member_recyclerView);
        mTvTitle = (TextView) findViewById(R.id.id_header_tv);
        mTvTitle.setText("班级成员");
        mFrameLayoutLoading = (FrameLayout) findViewById(R.id.loading_layout);
        mFrameLayoutLoading.setVisibility(View.VISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyClassMemberRecyclerViewAdapter(this, mUserList);
        mRecyclerView.setAdapter(mAdapter);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);
    }

    private void loadUserFromBmob() {
        String classId = SplashActivity.mClassID;
        BmobQuery<User> bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("classId", classId);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        User user = new User();
//                        String picUrl = list.get(i).getPic();

                        user = list.get(i);
//                        String myRole = list.get(i).getIsStudent();
//                        String myName = list.get(i).getMyClassCard();
//                        Log.d(TAG, "done: ---myName" + myName);
//                        BmobFile bmobFile = list.get(i).getPic();
//                        bmobFile.setUrl(picUrl);
//                        user.setPic(bmobFile);

//                        user.setIsStudent(myRole);
//                        user.setMyClassCard(myName);
                        mUserList.add(user);
                        Log.d(TAG, "done: -----user" + list.size());
                    }
                    mAdapter.notifyDataSetChanged();
                    mFrameLayoutLoading.setVisibility(View.GONE);
                } else {
                    showToast("查询失败");
                }
            }
        });
    }

    @Override
    protected void initListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideProgress();
                finish();
            }
        });

    }
}
