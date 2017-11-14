package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class FileInfoSelectAdapter extends BaseAdapter {

    private Context mContext;
    private Map<String, FileInfo> mDataHashMap;
    private String[] mKeys;
    List<Map.Entry<String,FileInfo>> mFileInfoMapList;
    OnDataListChangedListener mOnDataListChangedListener;


    public FileInfoSelectAdapter(Context context) {
        mContext = context;
        mDataHashMap = Application.getAppContent().getFileInfoMap();
        mFileInfoMapList = new ArrayList<Map.Entry<String,FileInfo>>(mDataHashMap.entrySet());
    }

    public void setOnDataListChangedListener(OnDataListChangedListener listener){
        this.mOnDataListChangedListener = listener;
    }

    public void notifyDataSetChanged(){

        mDataHashMap = Application.getAppContent().getFileInfoMap();
        mFileInfoMapList = new ArrayList<Map.Entry<String,FileInfo>>(mDataHashMap.entrySet());
                super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFileInfoMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileInfoMapList.get(position).getValue();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FileInfo fileInfo  = (FileInfo) getItem(position);
//        final FileInfo fileInfo  = AppContent.getAppContent().getFileInfoMap().get(position);
        FileSenderHolder holder = null;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_transfer,null);
            holder = new FileSenderHolder();
            holder.mIvShortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut_transfer);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTvProgress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.mPbFile = (ProgressBar) convertView.findViewById(R.id.pb_file);
            holder.mBtnOpration = (Button) convertView.findViewById(R.id.btn_operation_transfer);
            holder.mIvTick = (ImageView) convertView.findViewById(R.id.iv_tick_transfer);
            convertView.setTag(holder);
        }else{
            holder = (FileSenderHolder) convertView.getTag();
        }
        if(fileInfo != null){
            //初始化
//            holder.mPbFile.setVisibility(View.INVISIBLE);
            holder.mPbFile.setVisibility(View.VISIBLE);
            holder.mBtnOpration.setVisibility(View.INVISIBLE);
            holder.mIvTick.setVisibility(View.VISIBLE);
            holder.mIvTick.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_del));

            if(ShowFileUtils.isApkFile(fileInfo.getFilePath()) || ShowFileUtils.isMp4File(fileInfo.getFilePath())){ //Apk格式 或者MP4格式需要 缩略图
                holder.mIvShortcut.setImageBitmap(fileInfo.getBitmap());
            }else if(ShowFileUtils.isJpgFile(fileInfo.getFilePath())){//图片格式
                Glide.with(mContext)
                        .load(fileInfo.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.jpg)
                        .crossFade()
                        .into(holder.mIvShortcut);
            }else if(ShowFileUtils.isMp3File(fileInfo.getFilePath())){//音乐格式
                holder.mIvShortcut.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mp3));
            }else {
                holder.mIvShortcut.setImageDrawable(mContext.getResources().getDrawable(R.drawable.file_else));
            }

            holder.mTvName.setText(fileInfo.getFilePath());
            holder.mTvProgress.setText(ShowFileUtils.getFileSize(fileInfo.getSize()));

            holder.mIvTick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application.getAppContent().getFileInfoMap().remove(fileInfo.getFilePath());
                    notifyDataSetChanged();
                    if (mOnDataListChangedListener != null)
                        mOnDataListChangedListener.onDataChanged();
                }
            });
        }
        return convertView;
    }


    static class FileSenderHolder {
        ImageView mIvShortcut;
        TextView mTvName;
        TextView mTvProgress;
        ProgressBar mPbFile;
        Button mBtnOpration;
        ImageView mIvTick;

    }

    public interface OnDataListChangedListener{
        void onDataChanged();
    }



}

