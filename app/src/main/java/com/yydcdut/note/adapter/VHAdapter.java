package com.yydcdut.note.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yydcdut.note.adapter.vh.AbsVH;

import java.util.List;

/**
 * Created by yuyidong on 16/5/5.
 */
public abstract class VHAdapter<T, VH extends AbsVH> extends BaseAdapter {
    private Context mContext;
    private List<T> mData;

    private LayoutInflater mLayoutInflater;

    public VHAdapter(@NonNull Context context, @NonNull List<T> data) {
        mContext = context;
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh = null;
        if (convertView == null) {
            vh = onCreateVH(mLayoutInflater, getItemViewType(position), position);
            convertView = vh.itemView;
        } else {
            vh = (VH) convertView.getTag();
        }
        onBindVH(vh, position);
        convertView.setTag(vh);
        return vh.itemView;
    }

    public abstract VH onCreateVH(@NonNull LayoutInflater layoutInflater, int viewType, int position);

    public abstract void onBindVH(@NonNull VH holder, int position);

    @Nullable
    public Context getContext() {
        return mContext;
    }

    @Nullable
    public List<T> getData() {
        return mData;
    }

    public void resetData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
