package com.example.administrator.classcircle.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.adapter.FileSendAdapter;
import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.Application;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/27 0027.
 */

public class ShowSendFileInfoDialog {

    private Context mContext;
    private Button mBtnClear;
    private TextView mTvTitle;
    private ListView mListView;
    AlertDialog mAlertDialog;
    FileSendAdapter mFileSendAdapter;
    public static Handler mHandler = new Handler();

    public ShowSendFileInfoDialog(Context context) {
        mContext = context;

        View contentView = View.inflate(context, R.layout.view_show_selected_file_info_dialog,null);
        mBtnClear = (Button) contentView.findViewById(R.id.btn_clear);
        mBtnClear.setVisibility(View.GONE);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mListView = (ListView) contentView.findViewById(R.id.lv_result);
        String title =getAllSelectedFileDesc();
        mTvTitle.setText(title);
        mFileSendAdapter = new FileSendAdapter(mContext);

        mFileSendAdapter.setOnDataListChangedListener(new FileSendAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if (mFileSendAdapter.getCount() == 0){
                    hide();
                }
                mTvTitle.setText(getAllSelectedFileDesc());
            }
        });

        mListView.setAdapter(mFileSendAdapter);
        this.mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(contentView)
                .create();

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelectedFile();
                mHandler.sendEmptyMessage(0);
            }
        });
        mFileSendAdapter.setOnDataListChangedListener(new FileSendAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                mHandler.sendEmptyMessage(0);
            }
        });


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 3){
                    mFileSendAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private String getAllSelectedFileDesc(){
        String title = "";
        long totalSize = 0;
        Set<Map.Entry<String,FileInfo>> entrySet = Application.getAppContent().getFileInfoMap().entrySet();
        for (Map.Entry<String,FileInfo> entry :entrySet){
            FileInfo fileInfo = entry.getValue();
            totalSize = totalSize+fileInfo.getSize();
        }

        title = mContext.getResources().getString(R.string.str_selected_file_info_detail)
                .replace("{count}",String.valueOf(entrySet.size()))
                .replace("{size}",String.valueOf(ShowFileUtils.getFileSize(totalSize)));
        return title;
    }

    private void clearAllSelectedFile(){
        Application.getAppContent().getFileInfoMap().clear();
        if (mFileSendAdapter != null){
            mFileSendAdapter.notifyDataSetChanged();
        }
        this.hide();
    }

    public void show(){
        if (this.mAlertDialog != null){
            mFileSendAdapter.notifyDataSetChanged();
            mTvTitle.setText(getAllSelectedFileDesc());
            this.mAlertDialog.show();

        }
    }

    private void hide() {
        if (this.mAlertDialog != null){
            this.mAlertDialog.hide();
        }
    }


    public void notifyDataSetChanged(){
        if (mFileSendAdapter != null){
            mFileSendAdapter.notifyDataSetChanged();
        }
    }
}
