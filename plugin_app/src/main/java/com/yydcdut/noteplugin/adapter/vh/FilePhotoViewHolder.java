package com.yydcdut.noteplugin.adapter.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.noteplugin.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/27.
 */
public class FilePhotoViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.img_item_file)
    public ImageView mImageView;

    @Bind(R.id.layout_item_file)
    public View mFileLayout;

    @Bind(R.id.txt_item_file_name)
    public TextView mFileNameTextView;

    @Bind(R.id.txt_item_file_info)
    public TextView mFileInfoTextView;

    @Bind(R.id.txt_item_dir_name)
    public TextView mDirNameTextView;

    public FilePhotoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
