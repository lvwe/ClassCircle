package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.classcircle.item.ClassMemberItemView;
import com.example.administrator.classcircle.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17 0017.
 */

public class MyClassMemberRecyclerViewAdapter extends RecyclerView.Adapter<MyClassMemberRecyclerViewAdapter.ClassMemberViewHolder>{

    private Context mContext;
    private List<User> mUserList;

    public MyClassMemberRecyclerViewAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public ClassMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClassMemberViewHolder(new ClassMemberItemView(mContext) );
    }



    @Override
    public void onBindViewHolder(ClassMemberViewHolder holder, final int position) {
        holder.mClassMemberItemView.bindView(mUserList.get(position));
        holder.mClassMemberItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "bindView"+position, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ClassMemberViewHolder extends RecyclerView.ViewHolder{

        private ClassMemberItemView mClassMemberItemView;

        public ClassMemberViewHolder(ClassMemberItemView itemView) {
            super(itemView);
            this.mClassMemberItemView = itemView;
        }
    }
}
