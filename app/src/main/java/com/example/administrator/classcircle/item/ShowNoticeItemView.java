package com.example.administrator.classcircle.item;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.entity.Notice;

/**
 * Created by Administrator on 2017/9/17 0017.
 */

public class ShowNoticeItemView extends RelativeLayout {
    private static final String TAG = "ClassMemberItemView";

    private TextView mTvContent;
    private TextView mTvAdd;
    private TextView mTvName;
    private TextView mTvTime;


    public ShowNoticeItemView(Context context) {
        this(context, null);
    }

    public ShowNoticeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_show_notice, this);
        mTvContent = (TextView) view.findViewById(R.id.show_notice_content);
        mTvAdd = (TextView) view.findViewById(R.id.show_notice_add);
        mTvName = (TextView) view.findViewById(R.id.show_notice_name);
        mTvTime = (TextView) view.findViewById(R.id.show_notice_time);

    }

    public void bindView(Notice notice) {
//        mTvContent.setText("Java是一门面向对象编程语言，不仅吸收了C++语言的各种优点...");
//        mTvAdd.setText("含附件");
//        mTvName.setText("A001");
//        mTvTime.setText("9月10日 18:20");

        mTvContent.setText(notice.getUploadContent());
        if (notice.getUploadOther() == null){
            mTvAdd.setVisibility(GONE);
        }else {
            mTvAdd.setVisibility(VISIBLE);
            mTvAdd.setText("含附件");
        }

        mTvName.setText(notice.getUploadName());
        mTvTime.setText(notice.getUploadTime());

        Log.d(TAG, "bindView: ++++++++++++++++++++++++mTvContent"+notice.getUploadContent());
        Log.d(TAG, "bindView: ++++++++++++++++++++++++mTvAdd"+notice.getUploadOther());
        Log.d(TAG, "bindView: ++++++++++++++++++++++++mTvName"+notice.getUploadName());
        Log.d(TAG, "bindView: ++++++++++++++++++++++++mTvTime"+notice.getUploadTime());


    }
}
