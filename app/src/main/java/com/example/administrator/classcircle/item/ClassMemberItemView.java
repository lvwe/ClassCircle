package com.example.administrator.classcircle.item;

import android.content.Context;
import android.graphics.Color;
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
import com.example.administrator.classcircle.entity.User;
import com.example.administrator.classcircle.utils.GlideCircleTransform;

/**
 * Created by Administrator on 2017/9/17 0017.
 */

public class ClassMemberItemView extends RelativeLayout {
    private static final String TAG = "ClassMemberItemView";

    private ImageView mImageView;
    private TextView mTvRole;
    private TextView mTvName;



    public ClassMemberItemView(Context context) {
        this(context,null);
    }

    public ClassMemberItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_class_mebmer,this);
         mImageView = (ImageView) view.findViewById(R.id.id_class_member_item_img);
        mTvRole = (TextView) view.findViewById(R.id.id_class_member_item_tvRole);
        mTvName = (TextView) view.findViewById(R.id.id_class_member_item_tvName);
    }
    public void bindView(User user){
        if (user != null){
//            String picUrl = user.getPic().getUrl();
            if (user.getPic() != null ){
            Glide.with(getContext())
                    .load(user.getPic().getFileUrl())
                    .placeholder(R.drawable.file_jpg)
                    .transform(new GlideCircleTransform(getContext()))
                    .into(mImageView);
            }else {
                mImageView.setImageResource(R.drawable.file_jpg);
            }

            if (user.getIsStudent() != null){
                String myRole = user.getIsStudent();
                Log.e(TAG, "bindView: ----"+myRole);
                if (myRole.equals("false")){
                    mTvRole.setText("管理员");
                }else {
                    mTvRole.setBackgroundResource(R.drawable.bg_class_member_stu);
                    mTvRole.setText("学生");
                }
            }else {
                mTvRole.setBackgroundResource(R.drawable.bg_class_member_stu);
                mTvRole.setText("学生");
            }

            if (user.getMyClassCard() != null && user.getMyClassCard().length() > 0){

                mTvName.setText(user.getMyClassCard());
            }else {
                mTvName.setTextColor(getResources().getColor(R.color.linear_02));
                mTvName.setText("暂无班级昵称");
            }

        }else {
            return;
        }

    }
}
