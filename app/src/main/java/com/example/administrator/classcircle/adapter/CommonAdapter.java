package com.example.administrator.classcircle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


/**
 * Created by Administrator on 2017/10/7 0007.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    Context mContext;
    List<T> mDataList;

    public CommonAdapter(Context context, List<T> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void addDataList(List<T> dataList){
        this.mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void clear(){
        this.mDataList.clear();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = convertView(position, convertView);
        return convertView;
    }

    public abstract View convertView(int position, View convertView);
}
