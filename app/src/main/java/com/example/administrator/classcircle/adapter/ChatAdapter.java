package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.Msg;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.SplashActivity;

import java.util.List;


/**
 * Created by Administrator on 2017/9/18 0018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private static final String TAG = "ChatAdapter";

    private List<Msg> msgList;
    private Context mcontext;


    public ChatAdapter(List<Msg> msgList, Context mcontext){
        this.msgList=msgList;

        this.mcontext=mcontext;
    }


    static class ViewHolder extends  RecyclerView.ViewHolder{
      LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView leftName;
        TextView rightName;
        ImageView leftImg;
        ImageView rightImg;

        public ViewHolder(View itemView) {
            super(itemView);
          leftlayout= (LinearLayout) itemView.findViewById(R.id.left_layout);
           rightlayout= (LinearLayout) itemView.findViewById(R.id.right_layout);
            leftMsg= (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg= (TextView) itemView.findViewById(R.id.right_msg);
            leftName= (TextView) itemView.findViewById(R.id.left_name);
            rightName= (TextView) itemView.findViewById(R.id.right_name);
            leftImg= (ImageView) itemView.findViewById(R.id.chat_img_left);
            rightImg= (ImageView) itemView.findViewById(R.id.chat_img_right);


        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
                    Msg msg=msgList.get(position);
                     if(msg.getStudentId()!=null){

                         Log.d(TAG, "onBindViewHolder: ---"+msg.getStudentId()+"--"+SplashActivity.mLoginUserName);

                    if(msg.getStudentId().equals(SplashActivity.mLoginUserName)){
                        holder.leftlayout.setVisibility(View.GONE);
                        holder.rightlayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setText(msg.getContent());
                        holder.rightName.setText(msg.getName());
                        if (msg.getImgUrl() == null){
                            holder.rightImg.setImageResource(R.drawable.weather);
                        }else {
                            Glide.with(mcontext).load(msg.getImgUrl()).into( holder.rightImg);
                        }


                    }else{
                        holder.leftlayout.setVisibility(View.VISIBLE);
                        holder.rightlayout.setVisibility(View.GONE);
                        holder.leftMsg.setText(msg.getContent());
                        holder.leftName.setText(msg.getName());
                        Glide.with(mcontext).load(msg.getImgUrl()).into( holder.leftImg);
                    } }else{
                         holder.leftlayout.setVisibility(View.GONE);
                         holder.rightlayout.setVisibility(View.GONE);
                     }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }




}
