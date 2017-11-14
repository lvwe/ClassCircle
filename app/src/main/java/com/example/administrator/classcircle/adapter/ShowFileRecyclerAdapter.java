package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.classcircle.item.ShowFileItemView;
import com.example.administrator.classcircle.entity.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26 0026.
 */
public class ShowFileRecyclerAdapter extends RecyclerView.Adapter<ShowFileRecyclerAdapter.ShowFileViewHolder>{
    private static final String TAG = "ShowFileRecyclerAdapter";

    private Context mContext;
    private List<Data> mDataList;
    public OnRecyclerViewLongItemClickListener mOnLongItemClickListener;
    public OnRecyclerViewItemClickListener mOnItemClickListener;


    public void setOnLongItemClickListener(OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public ShowFileRecyclerAdapter(Context context, List<Data> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public ShowFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowFileViewHolder(new ShowFileItemView(mContext));
    }

    @Override
    public void onBindViewHolder(ShowFileViewHolder holder, final int position) {
        holder.mShowFileItemView.bindView(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ShowFileViewHolder extends  RecyclerView.ViewHolder{

        private ShowFileItemView mShowFileItemView;
        public ShowFileViewHolder(ShowFileItemView itemView) {
            super(itemView);
            this.mShowFileItemView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(v,getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongItemClickListener != null){
                        mOnLongItemClickListener.onLongItemClick(v,getAdapterPosition());
                    }
                    //返回为True  不会调用单击事件
                    return true;
                }
            });
        }

    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
