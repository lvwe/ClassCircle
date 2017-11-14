package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.classcircle.item.ShowNoticeItemView;
import com.example.administrator.classcircle.activity.ShowNoticeInfoActivity;
import com.example.administrator.classcircle.entity.Notice;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17 0017.
 */

public class ShowNoticeRecyclerViewAdapter extends RecyclerView.Adapter<ShowNoticeRecyclerViewAdapter.ShowNoticeViewHolder>{
    private static final String TAG = "ShowNoticeRecyclerViewA";

    private onRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnItemLongClickListener(onRecyclerViewItemListener itemListener){
        mOnRecyclerViewItemListener = itemListener;
    }


    private Context mContext;
    private List<Notice> mNoticeList;

    public ShowNoticeRecyclerViewAdapter(Context context, List<Notice> noticeList) {
        mContext =  context;
        mNoticeList = noticeList;
    }

    @Override
    public ShowNoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowNoticeViewHolder(new ShowNoticeItemView(mContext) );
    }

    @Override
    public void onBindViewHolder(ShowNoticeViewHolder holder, final int position) {
        holder.mShowNoticeItemView.bindView(mNoticeList.get(position));
        holder.mShowNoticeItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowNoticeInfoActivity.class);
                intent.putExtra("objId",mNoticeList.get(position).getObjId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    public class ShowNoticeViewHolder extends RecyclerView.ViewHolder{

        private ShowNoticeItemView mShowNoticeItemView;

        public ShowNoticeViewHolder(final ShowNoticeItemView itemView) {
            super(itemView);
            this.mShowNoticeItemView = itemView;
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnRecyclerViewItemListener.onRecyclerViewItemLongClick(v,getAdapterPosition());

                    return true;
                }
            });
        }
    }
    public interface onRecyclerViewItemListener{
        void onRecyclerViewItemLongClick(View v, int position);
    }
}
