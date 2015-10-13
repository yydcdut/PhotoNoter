package com.yydcdut.note.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yydcdut.note.bean.IObject;

import java.util.List;

/**
 * 数据适配器基类
 * Created by yyd on 15-3-28.
 */
public abstract class BaseListAdapter<T extends IObject> extends BaseAdapter {

    private Context mContext;
    private List<T> mGroup;

    public BaseListAdapter(Context context, List<T> group) {
        this.mContext = context;
        this.mGroup = group;
    }

    /**
     * 设置数据源
     */
    public void resetGroup(List<T> group) {
//        mGroup.clear();
        this.mGroup = group;
        this.notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(T data) {
        mGroup.add(data);
        this.notifyDataSetChanged();
    }

    /**
     * 移出数据
     */
    public void removeData(T data) {
        mGroup.remove(data);
        this.notifyDataSetChanged();
    }

    /**
     * 添加数据集
     */
    public void addGroup(List<T> group) {
        group.addAll(group);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getGroup() {
        return this.mGroup;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mGroup.size();
    }

    @Override
    public T getItem(int position) {
        return mGroup.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

}
