package com.yydcdut.gallery.adapter.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.view.GridItemImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by yuyidong on 16/3/19.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    private OnItemClickListener mOnItemClickListener;
    private OnItemSelectListener mOnItemSelectListener;

    @Bind(R.id.img_item_photo)
    public GridItemImageView imageView;

    @Bind(R.id.img_item_bg)
    public GridItemImageView bgImageView;

    @Bind(R.id.cb_item_photo)
    public CheckBox checkBox;

    public PhotoViewHolder(View itemView, int size, OnItemClickListener onItemClickListener, OnItemSelectListener onItemSelectListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnItemClickListener = onItemClickListener;
        mOnItemSelectListener = onItemSelectListener;
        imageView.setSize(size);
    }

    @OnClick(R.id.img_item_photo)
    public void onPhotoClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, getLayoutPosition(), getAdapterPosition());
        }
    }

    @OnCheckedChanged(R.id.cb_item_photo)
    public void onCheckBoxCheck(CompoundButton buttonView, boolean isChecked) {
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelectClick(buttonView, getLayoutPosition(), getAdapterPosition(), isChecked);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int layoutPosition, int adapterPosition);
    }

    public interface OnItemSelectListener {
        boolean onItemSelectClick(View v, int layoutPosition, int adapterPosition, boolean isSelected);
    }
}
