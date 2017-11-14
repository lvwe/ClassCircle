package com.example.administrator.classcircle.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.adapter.ShowNoticeRecyclerViewAdapter;
import com.example.administrator.classcircle.entity.Notice;
import com.example.administrator.classcircle.fragment.Fragment03;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShowNoticeActivity extends BaseActivity {
    private static final String TAG = "ShowNoticeActivity";

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private ShowNoticeRecyclerViewAdapter mAdapter;
    private static List<Notice> mNoticeList = new ArrayList<>();
    private int mNoticeListPosition;
    private TextView mTvBack;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_notice;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));

        mNoticeList.clear();
        loadNoticeFromBmob();
        mTextView = (TextView) findViewById(R.id.id_header_tv);
        mTextView.setText("通知");
        mRecyclerView = (RecyclerView) findViewById(R.id.show_notice_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mAdapter = new ShowNoticeRecyclerViewAdapter(this,mNoticeList);
        mRecyclerView.setAdapter(mAdapter);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);
        showProgress("加载中...");
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemLongClickListener(new ShowNoticeRecyclerViewAdapter.onRecyclerViewItemListener() {
            @Override
            public void onRecyclerViewItemLongClick(View v, int position) {
                mNoticeListPosition = position;
                Log.d(TAG, "onRecyclerViewItemLongClick: --"+Fragment03.mIsStu);
                if (Fragment03.mIsStu.equals("true")){
                    return;
                }else {
                    showAlertDialog();
                }

            }
        });

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    public void showAlertDialog(){
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("提示");
            // 设置对话框消息
            isExit.setMessage("确定要删除该条通知吗");
            // 添加选择按钮并注册监听
            isExit.setButton(AlertDialog.BUTTON_POSITIVE,"确定", listener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE,"取消", listener);
            // 显示对话框
            isExit.show();
    }
    DialogInterface.OnClickListener listener  = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case AlertDialog.BUTTON_POSITIVE:
                    //删除数据
                    showProgress("删除中...");
                    deleteDataFromBmob(mNoticeListPosition);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void deleteDataFromBmob(final int position) {
        Notice notice = new Notice();
        notice.setObjectId(mNoticeList.get(position).getObjId());
        notice.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //删除成功
                    showProgress("删除完成");
                    mNoticeList.clear();
                    loadNoticeFromBmob();
                    hideProgress();
                }else {
                    //删除失败
                    showProgress("删除失败");
                            hideProgress();
                }
            }
        });
    }

    private void loadNoticeFromBmob() {
        //列查询
        BmobQuery<Notice> bmobQuery = new BmobQuery<Notice>();
        //bmobQuery.addQueryKeys("uploadOther");
        bmobQuery.addWhereEqualTo("classId",SplashActivity.mClassID);
        bmobQuery.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> object, BmobException e) {
                if(e==null){
                    hideProgress();
                    Log.d(TAG, "done: -------------共 "+ object.size() +"条数据");
                    for (int i = 0;i<object.size();i++){
                        Notice item = new Notice();
                        item.setUploadName(object.get(i).getUploadName());
                        item.setUploadContent(object.get(i).getUploadContent());
                        if (object.get(i).getUploadOther() == null){
                            item.setUploadOther(null);
                        }else {
                            item.setUploadOther(object.get(i).getUploadOther());
                        }
                        item.setUploadTime(object.get(i).getCreatedAt());
                        item.setObjId(object.get(i).getObjectId());
                        mNoticeList.add(item);
                        Log.d(TAG, "init: -----------------------activity list size2 "+mNoticeList.size());
                    }
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "init: -----------------------activity list size "+mNoticeList.size());
                    //注意：这里的Person对象中只有指定列的数据。
                }else{
                    hideProgress();
                    showToast("加载失败");
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Log.d(TAG, "done: ----------------失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
