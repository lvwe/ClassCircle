package com.example.administrator.classcircle.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.classcircle.Application;
import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.adapter.FileInfoSelectAdapter;
import com.example.administrator.classcircle.entity.FileInfo;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class ShowFile extends AppCompatActivity {

    private Context mContext;
    private Button mBtnClear;
    private TextView mTvTitle;
    private ListView mListView;

    AlertDialog mAlertDialog;

    FileInfoSelectAdapter mFileInfoSelectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_show_selected_file_info_dialog);

        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mListView = (ListView) findViewById(R.id.lv_result);

        String title =getAllSelectedFileDesc();
        mTvTitle.setText(title);
        mFileInfoSelectAdapter = new FileInfoSelectAdapter(this);
        notifyDataSetChanged();
        mFileInfoSelectAdapter.setOnDataListChangedListener(new FileInfoSelectAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if (mFileInfoSelectAdapter.getCount() == 0){
//                    hide();
                }
                mTvTitle.setText(getAllSelectedFileDesc());
            }
        });
        mListView.setAdapter(mFileInfoSelectAdapter);
//        this.mAlertDialog = new AlertDialog.Builder(mContext)
//                .setView(contentView)
//                .create();

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelectedFile();
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

        title = this.getResources().getString(R.string.str_selected_file_info_detail)
                .replace("{count}",String.valueOf(entrySet.size()))
                .replace("{size}",String.valueOf(ShowFileUtils.getFileSize(totalSize)));
        return title;
    }

    private void clearAllSelectedFile(){
        Application.getAppContent().getFileInfoMap().clear();
        if (mFileInfoSelectAdapter != null){
            mFileInfoSelectAdapter.notifyDataSetChanged();
        }
//        this.hide();
        this.finish();
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
