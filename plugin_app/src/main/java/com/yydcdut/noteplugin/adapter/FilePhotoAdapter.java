package com.yydcdut.noteplugin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.vh.FilePhotoViewHolder;
import com.yydcdut.noteplugin.bean.TreeFile;

/**
 * Created by yuyidong on 16/3/27.
 */
public class FilePhotoAdapter extends RecyclerView.Adapter<FilePhotoViewHolder> {
    private final TreeFile mRoot;
    private TreeFile mCurrentNode;

    public FilePhotoAdapter(TreeFile root) {
        mRoot = root;
        mCurrentNode = root;
    }

    @Override
    public FilePhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_photo, parent, false);
        return new FilePhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilePhotoViewHolder holder, int position) {
        TreeFile treeFile = mCurrentNode.getChildren().get(position);
        if (treeFile.getChildren() == null) {//文件
            holder.mFileLayout.setVisibility(View.VISIBLE);
            holder.mDirNameTextView.setVisibility(View.GONE);
            holder.mFileNameTextView.setText(treeFile.getFileName());
            holder.mFileInfoTextView.setText(treeFile.getFileName());
        } else {//目录
            holder.mFileLayout.setVisibility(View.GONE);
            holder.mDirNameTextView.setVisibility(View.VISIBLE);
            holder.mDirNameTextView.setText(treeFile.getFileName());
        }
    }

    @Override
    public int getItemCount() {
        return mCurrentNode.getChildren() == null ? 0 : mCurrentNode.getChildren().size();
    }
}
