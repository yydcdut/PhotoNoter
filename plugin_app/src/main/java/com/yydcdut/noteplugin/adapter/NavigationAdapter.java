package com.yydcdut.noteplugin.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.vh.NavFooterViewHolder;
import com.yydcdut.noteplugin.bean.GalleryApp;

import java.util.List;

/**
 * Created by yuyidong on 16/3/20.
 */
public class NavigationAdapter extends RecyclerView.Adapter {
    private RecyclerView.Adapter mSystemNavAdapter;
    private List<GalleryApp> mGalleryAppList;
    private NavFooterViewHolder.OnNavFooterItemClickListener mOnNavFooterItemClickListener;

    public NavigationAdapter(@NonNull RecyclerView.Adapter systemNavAdapter,
                             @Nullable List<GalleryApp> galleryAppList,
                             @Nullable NavFooterViewHolder.OnNavFooterItemClickListener listener) {
        mSystemNavAdapter = systemNavAdapter;
        mGalleryAppList = galleryAppList;
        mOnNavFooterItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() - 1 == position) {
            return -1;
        } else {
            return mSystemNavAdapter.getItemViewType(position);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof NavFooterViewHolder) {
            return;
        }
        mSystemNavAdapter.onViewRecycled(holder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = mSystemNavAdapter.onCreateViewHolder(parent, viewType);
        if (holder == null) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_footer_main, null);
            holder = new NavFooterViewHolder(view, mGalleryAppList, mOnNavFooterItemClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {

        } else {
            mSystemNavAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mSystemNavAdapter.getItemCount() + 1;
    }
}
