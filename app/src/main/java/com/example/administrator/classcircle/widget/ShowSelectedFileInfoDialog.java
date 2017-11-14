package com.example.administrator.classcircle.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.adapter.FileInfoSelectAdapter;
import com.example.administrator.classcircle.Application;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class ShowSelectedFileInfoDialog {

    private Context mContext;
    private Button mBtnClear;
    private TextView mTvTitle;
    private ListView mListView;
    AlertDialog mAlertDialog;
    FileInfoSelectAdapter mFileInfoSelectAdapter;
    public static Handler mHandler = new Handler();


    public ShowSelectedFileInfoDialog(Context context) {
        mContext = context;

        View contentView = View.inflate(context, R.layout.view_show_selected_file_info_dialog,null);
        mBtnClear = (Button) contentView.findViewById(R.id.btn_clear);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mListView = (ListView) contentView.findViewById(R.id.lv_result);

        String title =getAllSelectedFileDesc();
        mTvTitle.setText(title);
        mFileInfoSelectAdapter = new FileInfoSelectAdapter(mContext);
        mFileInfoSelectAdapter.setOnDataListChangedListener(new FileInfoSelectAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if (mFileInfoSelectAdapter.getCount() == 0){
                    hide();
                }
                mTvTitle.setText(getAllSelectedFileDesc());
            }
        });
        mListView.setAdapter(mFileInfoSelectAdapter);
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
        mFileInfoSelectAdapter.setOnDataListChangedListener(new FileInfoSelectAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                mHandler.sendEmptyMessage(0);
            }
        });

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
        if (mFileInfoSelectAdapter != null){
            mFileInfoSelectAdapter.notifyDataSetChanged();
        }
        this.hide();
    }

    public void show(){
        if (this.mAlertDialog != null){
            mFileInfoSelectAdapter.notifyDataSetChanged();
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
        if (mFileInfoSelectAdapter != null){
            mFileInfoSelectAdapter.notifyDataSetChanged();
        }
    }
}
