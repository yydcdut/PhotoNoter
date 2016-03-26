package com.yydcdut.noteplugin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.vh.PhotoViewHolder;
import com.yydcdut.noteplugin.model.MediaFolder;
import com.yydcdut.noteplugin.model.MediaPhoto;
import com.yydcdut.noteplugin.model.SelectPhotoModel;

import java.util.List;

/**
 * Created by yuyidong on 16/3/19.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private Context mContext;
    private int mSize;
    private List<MediaPhoto> mMediaPhotoList;

    private PhotoViewHolder.OnItemClickListener mOnItemClickListener;
    private PhotoViewHolder.OnItemSelectListener mOnItemSelectListener;

    public PhotoAdapter(@NonNull Context context, @NonNull int size, @NonNull MediaFolder mediaFolder,
                        @Nullable PhotoViewHolder.OnItemClickListener onItemClickListener,
                        @Nullable PhotoViewHolder.OnItemSelectListener onItemSelectListener) {
        mContext = context;
        mSize = size;
        mMediaPhotoList = mediaFolder.getMediaPhotoList();
        mOnItemClickListener = onItemClickListener;
        mOnItemSelectListener = onItemSelectListener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view, mSize, mOnItemClickListener, mOnItemSelectListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        MediaPhoto mediaPhoto = mMediaPhotoList.get(position);
        if (SelectPhotoModel.getInstance().contains(mediaPhoto.getPath())) {
            holder.checkBox.setCheckedWithoutCallback(true);
        } else {
            holder.checkBox.setCheckedWithoutCallback(false);
        }
        holder.imageView.setImageResource(R.drawable.ic_gf_default_photo);
        ImageLoader.getInstance().displayImage("file:/" + mediaPhoto.getThumbPath(), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mMediaPhotoList.size();
    }

    public void updateMediaFolder(@NonNull MediaFolder mediaFolder) {
        mMediaPhotoList = mediaFolder.getMediaPhotoList();
        notifyDataSetChanged();
    }
}
