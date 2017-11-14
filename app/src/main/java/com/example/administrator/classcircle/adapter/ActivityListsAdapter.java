package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.classcircle.entity.ActivityLists;
import com.example.administrator.classcircle.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/2 0002.
 */

public class ActivityListsAdapter extends BaseAdapter{
    private List<ActivityLists>  activityListsList;
    private Context mcontext;

    public ActivityListsAdapter(List<ActivityLists>  activityListsList,Context mcontext){
        this.activityListsList=activityListsList;
        this.mcontext=mcontext;
    }
    @Override
    public int getCount() {
        return activityListsList.size();
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
        ViewHodler viewHodler;
        if(convertView==null){
            convertView= LayoutInflater.from(mcontext).inflate(R.layout.activity_listitem,null);
            viewHodler=new ViewHodler();
            viewHodler.activity_from_class= (TextView) convertView.findViewById(R.id.activity_from_class);
            viewHodler.activity_img= (ImageView) convertView.findViewById(R.id.activity_title_img);
            viewHodler.activity_meaning= (TextView) convertView.findViewById(R.id.activity_meaning);
            viewHodler.activity_name= (TextView) convertView.findViewById(R.id.activity_name);
            viewHodler.activity_start_time= (TextView) convertView.findViewById(R.id.activity_start_time);
            viewHodler.activity_title_name= (TextView) convertView.findViewById(R.id.activity_title_name);
            //viewHodler.activity_vote= (Button) convertView.findViewById(R.id.activity_vote);

        }else{
            viewHodler= (ViewHodler) convertView.getTag();
        }
        ActivityLists activityLists=activityListsList.get(position);

        viewHodler.activity_from_class.setText(activityLists.getFromClass());
        viewHodler.activity_meaning.setText(activityLists.getExplain());
        viewHodler.activity_name.setText(activityLists.getName());
        viewHodler.activity_start_time.setText(activityLists.getCreateTime());
        viewHodler.activity_title_name.setText(activityLists.getTitle());
        Glide.with(mcontext).load(activityLists.getImgUrl()).into( viewHodler.activity_img);
        viewHodler.activity_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext, "you click", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
    public class ViewHodler{
        public TextView activity_title_name;
        public TextView activity_name;
        public TextView activity_start_time;
        public TextView activity_from_class;
        public TextView activity_meaning;
        public Button activity_vote;
        public ImageView activity_img;
    }
}
