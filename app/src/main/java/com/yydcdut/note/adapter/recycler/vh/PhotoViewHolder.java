package com.yydcdut.note.adapter.recycler.vh;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/10/14.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    @BindView(R.id.img_item_album)
    public AppCompatImageView imageView;
    @BindView(R.id.layout_item_album_check)
    public AppCompatImageView checkLayout;

    public PhotoViewHolder(View itemView, int size, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
        } else {
            layoutParams.height = size;
        }
        itemView.setLayoutParams(layoutParams);
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, getLayoutPosition(), getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            return mOnItemLongClickListener.onItemLongClick(v, getLayoutPosition(), getAdapterPosition());
        }
        return false;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int layoutPosition, int adapterPosition);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View v, int layoutPosition, int adapterPosition);
    }
}
