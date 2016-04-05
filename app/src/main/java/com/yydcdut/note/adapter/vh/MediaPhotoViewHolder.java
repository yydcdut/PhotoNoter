package com.yydcdut.note.adapter.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

import com.yydcdut.note.R;
import com.yydcdut.note.widget.GridItemImageView;
import com.yydcdut.note.widget.PhotoCheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 16/3/19.
 */
public class MediaPhotoViewHolder extends RecyclerView.ViewHolder implements PhotoCheckBox.OnPhotoCheckedChangeListener {
    private OnItemClickListener mOnItemClickListener;
    private OnItemSelectListener mOnItemSelectListener;

    @Bind(R.id.img_item_photo)
    public GridItemImageView imageView;

    @Bind(R.id.img_item_bg)
    public GridItemImageView bgImageView;

    @Bind(R.id.cb_item_photo)
    public PhotoCheckBox checkBox;

    public MediaPhotoViewHolder(View itemView, int size, OnItemClickListener onItemClickListener, OnItemSelectListener onItemSelectListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnItemClickListener = onItemClickListener;
        mOnItemSelectListener = onItemSelectListener;
        imageView.setSize(size);
        bgImageView.setSize(size);
        checkBox.setOnPhotoCheckedChangeListener(this);
    }

    @OnClick(R.id.img_item_bg)
    public void onPhotoClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, getLayoutPosition(), getAdapterPosition());
        }
    }

    @Override
    public void onPhotoCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelectClick(buttonView, getLayoutPosition(), getAdapterPosition(), isChecked);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int layoutPosition, int adapterPosition);
    }

    public interface OnItemSelectListener {
        void onItemSelectClick(View v, int layoutPosition, int adapterPosition, boolean isSelected);
    }
}
