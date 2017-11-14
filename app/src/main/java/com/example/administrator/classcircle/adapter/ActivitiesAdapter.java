package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.li.ActivityDetailPager;

import java.util.List;

/**
 * Created by Administrator on 2017/10/7 0007.
 */

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>{

    private List<ActivityLists>  activityListsList;
    private Context mcontext;
    private List<String> objectIdList;

    public ActivitiesAdapter(List<ActivityLists>  activityListsList,Context mcontext, List<String> objectIdList){
        this.activityListsList=activityListsList;
        this.mcontext=mcontext;
        this.objectIdList=objectIdList;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView activity_title_name;
        public TextView activity_name;
        public TextView activity_start_time;
        public TextView activity_from_class;
        public TextView activity_meaning;
        public TextView activity_vote_num;
        public ImageView activity_img;
        public  View mview;


        public ViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
           activity_from_class= (TextView) itemView.findViewById(R.id.activity_from_class);
           activity_img= (ImageView) itemView.findViewById(R.id.activity_title_img);
            activity_meaning= (TextView) itemView.findViewById(R.id.activity_meaning);
           activity_name= (TextView) itemView.findViewById(R.id.activity_name);
           activity_start_time= (TextView) itemView.findViewById(R.id.activity_start_time);
           activity_title_name= (TextView) itemView.findViewById(R.id.activity_title_name);
            activity_vote_num= (TextView) itemView.findViewById(R.id.activity_vote_num);




        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View  convertView= LayoutInflater.from(mcontext).inflate(R.layout.activity_listitem,null);
        ViewHolder holder=new ViewHolder(convertView);



        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ActivityLists activityLists=activityListsList.get(position);
        holder.activity_vote_num.setText(String.valueOf(activityLists.getVoteNum()));
        holder.activity_from_class.setText(activityLists.getFromClass());
        holder.activity_meaning.setText(activityLists.getExplain());
        holder.activity_name.setText(activityLists.getName());
        holder.activity_start_time.setText(activityLists.getCreateTime());
        holder.activity_title_name.setText(activityLists.getTitle());
        Glide.with(mcontext).load(activityLists.getImgUrl()).into( holder.activity_img);
        holder.mview.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mcontext, ActivityDetailPager.class);
            intent.putExtra("title",activityLists.getTitle());
            intent.putExtra("name",activityLists.getName());
             intent.putExtra("time",activityLists.getCreateTime());
             intent.putExtra("fromClass",activityLists.getFromClass());
             intent.putExtra("img",activityLists.getImgUrl());
             intent.putExtra("meaning",activityLists.getExplain());
             intent.putExtra("voteNum",String.valueOf(activityLists.getVoteNum()));
        intent.putExtra("objectId",objectIdList.get(position));


        mcontext.startActivity(intent);
    }
});


    }



    @Override
    public int getItemCount() {
        return activityListsList.size();
    }



}
