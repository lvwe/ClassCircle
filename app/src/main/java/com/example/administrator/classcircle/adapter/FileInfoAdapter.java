package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.Application;

import java.util.List;

/**
 * Created by Administrator on 2017/10/7 0007.
 */
public class FileInfoAdapter extends CommonAdapter<FileInfo> {

    private int mType = FileInfo.TYPE_APK;

    public FileInfoAdapter(Context context, List<FileInfo> dataList) {
        super(context, dataList);
    }

    public FileInfoAdapter(Context context, List<FileInfo> dataList, int type) {
        super(context, dataList);
        this.mType = type;
    }

    @Override
    public View convertView(int position, View convertView) {
        FileInfo fileInfo = getDataList().get(position);
        if (mType == FileInfo.TYPE_APK) {
            ApkViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_apk, null);

                viewHolder = new ApkViewHolder();
                viewHolder.mIvShotCut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = (TextView) convertView.findViewById(R.id.tv_size);
                viewHolder.mOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ApkViewHolder) convertView.getTag();
            }
            if (getDataList() != null && getDataList().get(position) != null) {
                viewHolder.mIvShotCut.setImageBitmap(fileInfo.getBitmap());
                viewHolder.mTvName.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                if (Application.getAppContent().isExist(fileInfo)) {
                    viewHolder.mOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mOkTick.setVisibility(View.GONE);
                }
            }
        } else if (mType == FileInfo.TYPE_JPG) {
            JpgViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_jpg, null);
                viewHolder = new JpgViewHolder();
                viewHolder.mIvShotCut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.mOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (JpgViewHolder) convertView.getTag();
            }
            if (mDataList != null && mDataList.get(position) != null) {
                Glide.with(getContext())
                        .load(fileInfo.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.jpg)
                        .crossFade()
                        .into(viewHolder.mIvShotCut);
                if (Application.getAppContent().isExist(fileInfo)) {
                    viewHolder.mOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mOkTick.setVisibility(View.GONE);
                }
            }

        } else if (mType == FileInfo.TYPE_MP3) {
            Mp3ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp3, null);
                viewHolder = new Mp3ViewHolder();

                viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = (TextView) convertView.findViewById(R.id.tv_size);
                viewHolder.mOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (Mp3ViewHolder) convertView.getTag();
            }
            if (getDataList() != null && getDataList().get(position) != null) {


                viewHolder.mTvName.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                if (Application.getAppContent().isExist(fileInfo)) {
                    viewHolder.mOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mOkTick.setVisibility(View.GONE);
                }
            }


        } else if (mType == FileInfo.TYPE_MP4) {

            Mp4ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp4, null);
                viewHolder = new Mp4ViewHolder();
                viewHolder.mIvShotCut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mTvSize = (TextView) convertView.findViewById(R.id.tv_size);
                viewHolder.mOkTick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (Mp4ViewHolder) convertView.getTag();
            }
            if (getDataList() != null && getDataList().get(position) != null) {
                if (fileInfo.getSize() <= 0){
                    viewHolder.mIvShotCut.setImageResource(R.mipmap.mp4);

                }else {
                    viewHolder.mIvShotCut.setImageBitmap(fileInfo.getBitmap());
                }
                viewHolder.mTvName.setText(fileInfo.getName() == null ? "" : fileInfo.getName());
                viewHolder.mTvSize.setText(fileInfo.getSizeDesc() == null ? "" : fileInfo.getSizeDesc());

                if (Application.getAppContent().isExist(fileInfo)) {
                    viewHolder.mOkTick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mOkTick.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }

    static class ApkViewHolder {
        ImageView mIvShotCut;
        ImageView mOkTick;
        TextView mTvName;
        TextView mTvSize;
    }

    static class JpgViewHolder {
        ImageView mIvShotCut;
        ImageView mOkTick;
    }

    static class Mp3ViewHolder {
        ImageView mIvShotCut;
        ImageView mOkTick;
        TextView mTvName;
        TextView mTvSize;
    }

    static class Mp4ViewHolder {
        ImageView mIvShotCut;
        ImageView mOkTick;
        TextView mTvName;
        TextView mTvSize;
    }
}
