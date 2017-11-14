package com.example.administrator.classcircle.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.StatusBarUtils;
import com.example.administrator.classcircle.adapter.ShowFileRecyclerAdapter;
import com.example.administrator.classcircle.entity.Data;
import com.example.administrator.classcircle.fragment.Fragment03;
import com.example.administrator.classcircle.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShowFileActivity extends BaseActivity {
    private static final String TAG = "ShowFileActivity";

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private ShowFileRecyclerAdapter mAdapter;
    private List<Data> mDataList = new ArrayList<>();
    private int mDataListPosition;
    private TextView mTvBack;
    private ImageView mImageViewAdd;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_show_file;
    }

    @Override
    protected void init() {
        StatusBarUtils.compat(this,getResources().getColor(R.color.colorPrimary));

        mDataList.clear();
        loadDataFromBmob();
        mTextView = (TextView) findViewById(R.id.id_header_tv);
        mTextView.setText("文件");
        mRecyclerView = (RecyclerView) findViewById(R.id.show_file_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShowFileRecyclerAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.VISIBLE);
        mImageViewAdd = (ImageView) findViewById(R.id.add);
        mImageViewAdd.setVisibility(View.VISIBLE);

        showProgress("加载中...");
    }

    private void loadDataFromBmob() {
        BmobQuery<Data> bmobQuery = new BmobQuery<Data>();
        //bmobQuery.addQueryKeys("uploadOther");
        bmobQuery.addWhereEqualTo("classId", SplashActivity.mClassID);
        bmobQuery.findObjects(new FindListener<Data>() {
                                  @Override
                                  public void done(List<Data> object, BmobException e) {
                                      if (e == null) {
                                          hideProgress();
                                          Log.d(TAG, "done: -------------共 " + object.size() + "条数据");
                                          for (int i = 0; i < object.size(); i++) {
                                              String name = object.get(i).getName();
                                              String time = object.get(i).getUpdatedAt();
                                              String fileName = object.get(i).getUploadFile().getFilename();
                                              String objId = object.get(i).getObjectId();
                                              String fileUrl = object.get(i).getUploadFile().getUrl();
                                              Data item = new Data();
                                              item.setName(name);
                                              item.setFileName(fileName);
                                              item.setObjId(objId);
                                              item.setDate(time);
                                              item.setFileUrl(fileUrl);
                                              mDataList.add(item);
                                          }
                                          mAdapter.notifyDataSetChanged();
                                          //注意：这里的Person对象中只有指定列的数据。
                                      } else {
                                          hideProgress();
                                          showToast("加载失败");
                                      }
                                  }
                              }

        );
    }

    @Override
    protected void initListener() {

        mAdapter.setOnItemClickListener(new ShowFileRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Item 单击事件
                String fileName = mDataList.get(position).getFileName();
                if (FileUtils.getExtension(fileName).equals("jpg") ||
                        FileUtils.getExtension(fileName).equals("gif") ||
                        FileUtils.getExtension(fileName).equals("png") ||
                        FileUtils.getExtension(fileName).equals("jpeg") ||
                        FileUtils.getExtension(fileName).equals("bmp")) {
                    goToActivity(ShowImgFileActivity.class, mDataList.get(position).getFileUrl());
                    Log.d(TAG, "onItemClick: -----go to activity+" + mDataList.get(position).getFileUrl());
                } else if (FileUtils.getExtension(fileName).equals("3gp") ||
                        FileUtils.getExtension(fileName).equals("mp4") ||
                        FileUtils.getExtension(fileName).equals("ogg") ||
                        FileUtils.getExtension(fileName).equals("wav") ||
                        FileUtils.getExtension(fileName).equals("mid")) {
                    goToActivity(ShowVideoFileActivity.class, mDataList.get(position).getFileUrl());
                    Log.d(TAG, "onItemClick: -----go to activity+" + mDataList.get(position).getFileUrl());
                } else {
                    goToActivity(ShowOtherFileActivity.class, mDataList.get(position).getObjId());
                }
            }
        });
        mAdapter.setOnLongItemClickListener(new ShowFileRecyclerAdapter.OnRecyclerViewLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, int position) {
                // Item 长按事件
                if (Fragment03.mIsStu.equals("true")) {
                    return;
                } else {
                    showAlertDialog();
                    mDataListPosition = position;
                }

            }
        });

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mImageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(ChooseFileActivity.class,false);
            }
        });

    }

    private void deleteDataFromBmob(int position) {
        Data data = new Data();
        data.setObjectId(mDataList.get(position).getObjId());
        data.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //删除成功
                    showProgress("删除完成");
                    mDataList.clear();
                    loadDataFromBmob();
                    hideProgress();
                } else {
                    //删除失败
                    showProgress("删除失败");
                    hideProgress();
                }
            }
        });
    }

    private void showAlertDialog() {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("系统提示");
        // 设置对话框消息
        isExit.setMessage("确定要删除该文件吗");
        // 添加选择按钮并注册监听
        isExit.setButton(AlertDialog.BUTTON_POSITIVE, "确定", listener);
        isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", listener);
        // 显示对话框
        isExit.show();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    //确定  删除数据
                    showProgress("删除中...");
                    deleteDataFromBmob(mDataListPosition);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;

                default:
                    break;

            }
        }
    };

    private void goToActivity(Class activity, String fileUrl) {
        Intent intent = new Intent(getBaseContext(), activity);
        intent.putExtra("FILE_URL", fileUrl);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataList.clear();
        showProgress("加载中...");
        loadDataFromBmob();

    }
}



