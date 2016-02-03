package com.yydcdut.note.adapter.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.widget.GridItemImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/10/14.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    @Bind(R.id.img_item_album)
    public GridItemImageView imageView;
    @Bind(R.id.layout_item_album_check)
    public GridItemImageView checkLayout;

    public PhotoViewHolder(View itemView, int size, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
        imageView.setSize(size);
        checkLayout.setSize(size);
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
