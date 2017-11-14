package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ClassId;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.widget.HorizontalProgressBarWithNumber;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class ListViewAdapter extends BaseAdapter {
    private List<String> objectIdList;
    private List<ClassId> mStudentList;
    private Context mcontext;
    private LayoutInflater mLayoutInflater;

    public List<ClassId> getmStudentList(){
        return mStudentList;
    }
    public void setmStudentList(List<ClassId> mStudentList){
        this.mStudentList=mStudentList;
        notifyDataSetChanged();
        Toast.makeText(mcontext, "has notify", Toast.LENGTH_SHORT).show();
    }


    public  List<String> getmobjectIdList(){
        return objectIdList;
    }


    public  ListViewAdapter(Context mcontext, List<ClassId> studentList, List<String> objectIdList){
            mLayoutInflater = LayoutInflater.from(mcontext);
        this.mcontext=mcontext;
        this.mStudentList = studentList;
        this.objectIdList=objectIdList;
    }
    @Override
    public int getCount() {
        return mStudentList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_view_video_item,null);
            holder.classImg = (ImageView) convertView.findViewById(R.id.list_classImg);
            holder.className = (TextView) convertView.findViewById(R.id.list_className);
            holder.createTime = (TextView) convertView.findViewById(R.id.list_createTime);
            holder.voteNum = (TextView) convertView.findViewById(R.id.list_voteNum);
            holder.myProgress= (HorizontalProgressBarWithNumber) convertView.findViewById(R.id.myProgressBar);
            holder.myProgress.setMax(880);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ClassId classId=mStudentList.get(position);

        Glide.with(mcontext).load(classId.getPhotoPath().get(0).getUrl()).into(holder.classImg);
        holder.className.setText(classId.getClassName());
        holder.createTime.setText(classId.getCreateTime());
        holder.voteNum.setText(String.valueOf(classId.getVoteNum())+"票");
        holder.myProgress.setProgress(classId.getVoteNum());

       // Toast.makeText(mcontext, String.valueOf(mStudentList.size()), Toast.LENGTH_SHORT).show();
        return convertView;
    }



    public final class ViewHolder{
        public ImageView classImg;
        public TextView className;
        public TextView createTime;
        public TextView voteNum;
        public View view;
        public HorizontalProgressBarWithNumber myProgress;



    }




}
