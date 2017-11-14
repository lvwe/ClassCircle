package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.Application;
import com.example.administrator.classcircle.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27 0027.
 */

public class FileSendAdapter extends BaseAdapter {
    private static final String TAG = "FileSendAdapter";

    private Context mContext;
    private Map<String, FileInfo> mDataHashMap;
    List<Map.Entry<String, FileInfo>> fileInfoMapList;
    private OnDataListChangedListener mOnDataListChangedListener;

    public FileSendAdapter(Context mContext) {
        this.mContext = mContext;
        mDataHashMap = Application.getAppContent().getFileInfoMap();
        fileInfoMapList = new ArrayList<Map.Entry<String, FileInfo>>(mDataHashMap.entrySet());
    }

    public void setOnDataListChangedListener(FileSendAdapter.OnDataListChangedListener listener) {
        this.mOnDataListChangedListener = listener;
    }

    public void notifyDataSetChanged() {
        mDataHashMap = Application.getAppContent().getFileInfoMap();
        fileInfoMapList = new ArrayList<Map.Entry<String, FileInfo>>(mDataHashMap.entrySet());
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileInfoMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileInfoMapList.get(position).getValue();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfo fileInfo = (FileInfo) getItem(position);
        FileSenderHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_transfer, null);
            holder = new FileSenderHolder();
            holder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut_transfer);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.pb_file = (ProgressBar) convertView.findViewById(R.id.pb_file);
            holder.btn_operation = (Button) convertView.findViewById(R.id.btn_operation_transfer);
            holder.iv_tick = (ImageView) convertView.findViewById(R.id.iv_tick_transfer);
            convertView.setTag(holder);
        } else {
            holder = (FileSenderHolder) convertView.getTag();
        }
        if (fileInfo != null) {
            holder.pb_file.setVisibility(View.VISIBLE);
            holder.iv_tick.setVisibility(View.GONE);
            if (ShowFileUtils.isApkFile(fileInfo.getFilePath()) || ShowFileUtils.isMp4File(fileInfo.getFilePath())) { //Apk格式 或者MP4格式需要 缩略图
                holder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
            } else if (ShowFileUtils.isJpgFile(fileInfo.getFilePath())) {//图片格式
                Glide.with(mContext)
                        .load(fileInfo.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.jpg)
                        .crossFade()
                        .into(holder.iv_shortcut);

            } else if (ShowFileUtils.isMp3File(fileInfo.getFilePath())) {//音乐格式
                holder.iv_shortcut.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mp3));
            }
            holder.tv_name.setText(ShowFileUtils.getFileNames(fileInfo.getFilePath()));
            if (fileInfo.getResult() == FileInfo.FLAG_SUCCESS) { //文件传输成功
                long total = fileInfo.getSize();
                holder.pb_file.setVisibility(View.GONE);
                holder.tv_progress.setText(ShowFileUtils.getFileSize(total) + "/" + ShowFileUtils.getFileSize(total));

                holder.btn_operation.setVisibility(View.INVISIBLE);
                holder.iv_tick.setVisibility(View.VISIBLE);
            } else if (fileInfo.getResult() == FileInfo.FLAG_FAILURE) { //文件传输失败
                holder.pb_file.setVisibility(View.GONE);
            } else {//文件传输中
                long progress = fileInfo.getProcceed();
                long total = fileInfo.getSize();
                if (progress == 0) {
                    holder.tv_progress.setText(ShowFileUtils.getFileSize(progress) + "/" + ShowFileUtils.getFileSize(total));
                } else {
                    long fileProgress = total / 100 * progress;
                    Log.d(TAG, "getView: +++" + progress + "--" + total + "--" + fileProgress);
                    holder.tv_progress.setText(ShowFileUtils.getFileSize(fileProgress) + "/" + ShowFileUtils.getFileSize(total));

                }

                int percent = (int) (progress * 100 / total);
                holder.pb_file.setMax(100);
                holder.pb_file.setProgress((int) progress);

                //TODO 传输过程中取消的问题
                holder.btn_operation.setText(mContext.getString(R.string.str_cancel));
                holder.btn_operation.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //可否通过广播来实现？
                    }
                });
            }
        }
        return convertView;
    }

    static class FileSenderHolder {
        ImageView iv_shortcut;
        TextView tv_name;
        TextView tv_progress;
        ProgressBar pb_file;
        Button btn_operation;
        ImageView iv_tick;
    }

    public interface OnDataListChangedListener {
        void onDataChanged();
    }

}
