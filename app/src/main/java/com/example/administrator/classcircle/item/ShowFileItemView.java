package com.example.administrator.classcircle.item;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.entity.Data;
import com.example.administrator.classcircle.entity.Notice;
import com.example.administrator.classcircle.utils.FileUtils;
import com.example.administrator.classcircle.utils.ThreadUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/9/17 0017.
 */

public class ShowFileItemView extends LinearLayout {
    private static final String TAG = "ClassMemberItemView";

    private ImageView mImageView;
    private TextView mTvUploadName;
    private TextView mTvName;
    private TextView mTvTime;


    public ShowFileItemView(Context context) {
        this(context, null);
    }

    public ShowFileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_show_file, this);
        mImageView = (ImageView) view.findViewById(R.id.show_file_img);
        mTvUploadName = (TextView) view.findViewById(R.id.show_file_uploadName);
        mTvName = (TextView) view.findViewById(R.id.show_file_name);
        mTvTime = (TextView) view.findViewById(R.id.show_file_time);

    }

    public void bindView(Data data) {
        // name-->a001
        //time-->2017-09-28 16:42:31
        //fileName-->IMG_20170918_154100.jpg
        String fileName = data.getFileName();
        String fileUrl = data.getFileUrl();
        String time = data.getDate();
        String name = data.getName();
        if (FileUtils.getExtension(fileName).equals("jpg") ||
                FileUtils.getExtension(fileName).equals("gif") ||
                FileUtils.getExtension(fileName).equals("png") ||
                FileUtils.getExtension(fileName).equals("jpeg") ||
                FileUtils.getExtension(fileName).equals("bmp")) {
            Glide.with(getContext())
                    .load(fileUrl)
                    .placeholder(R.drawable.file_jpg)
                    .error(R.drawable.file_jpg)
                    .centerCrop()
                    .into(mImageView);
        } else if (FileUtils.getExtension(fileName).equals("3gp") ||
                FileUtils.getExtension(fileName).equals("mp4") ||
                FileUtils.getExtension(fileName).equals("ogg") ||
                FileUtils.getExtension(fileName).equals("wav") ||
                FileUtils.getExtension(fileName).equals("mid")) {
            mImageView.setImageResource(R.drawable.file_video);
        } else if (FileUtils.getExtension(fileName).equals("ppt")) {
            mImageView.setImageResource(R.drawable.file_ppt);
        } else if (FileUtils.getExtension(fileName).equals("txt")) {
            mImageView.setImageResource(R.drawable.file_txt);
        } else if (FileUtils.getExtension(fileName).equals("docx")) {
            mImageView.setImageResource(R.drawable.file_doc);
        } else if (FileUtils.getExtension(fileName).equals("pdf")) {
            mImageView.setImageResource(R.drawable.file_pdf);
        } else if (FileUtils.getExtension(fileName).equals("zip")) {
            mImageView.setImageResource(R.drawable.file_zip);
        }else if (FileUtils.getExtension(fileName).equals("xlsx")) {
            mImageView.setImageResource(R.drawable.file_xls);
        }else if (FileUtils.getExtension(fileName).equals("apk")) {
            mImageView.setImageResource(R.mipmap.apk);
        }else if (FileUtils.getExtension(fileName).equals("mp3")) {
            mImageView.setImageResource(R.mipmap.mp3);
        } else {
            mImageView.setImageResource(R.drawable.file_else);
        }
        mTvUploadName.setText(name);
        mTvName.setText(fileName);
        //time-->2017-09-28 16:42:31
        mTvTime.setText(time.substring(5,16));
        Log.d(TAG, "bindView: +++++++++"+fileName);
        Log.d(TAG, "bindView: +++++++++"+fileUrl);

    }
}
