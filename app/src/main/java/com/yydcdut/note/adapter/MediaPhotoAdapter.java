package com.yydcdut.note.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.vh.MediaPhotoViewHolder;
import com.yydcdut.note.bean.gallery.MediaFolder;
import com.yydcdut.note.bean.gallery.MediaPhoto;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.util.List;
import java.util.Random;

/**
 * Created by yuyidong on 16/3/19.
 */
public class MediaPhotoAdapter extends RecyclerView.Adapter<MediaPhotoViewHolder> {
    private int mSize;
    private List<MediaPhoto> mMediaPhotoList;
    private Context mContext;
    private Random mRandom;

    private MediaPhotoViewHolder.OnItemClickListener mOnItemClickListener;
    private MediaPhotoViewHolder.OnItemSelectListener mOnItemSelectListener;

    public MediaPhotoAdapter(@NonNull Context context, @NonNull int size, @NonNull MediaFolder mediaFolder,
                             @Nullable MediaPhotoViewHolder.OnItemClickListener onItemClickListener,
                             @Nullable MediaPhotoViewHolder.OnItemSelectListener onItemSelectListener) {
        mContext = context;
        mSize = size;
        mMediaPhotoList = mediaFolder.getMediaPhotoList();
        mOnItemClickListener = onItemClickListener;
        mOnItemSelectListener = onItemSelectListener;
        mRandom = new Random();
    }

    @Override
    public MediaPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_media_photo, parent, false);
        return new MediaPhotoViewHolder(view, mSize, mOnItemClickListener, mOnItemSelectListener);
    }

    @Override
    public void onBindViewHolder(MediaPhotoViewHolder holder, int position) {
        MediaPhoto mediaPhoto = mMediaPhotoList.get(position);
        if (SelectPhotoModel.getInstance().contains(mediaPhoto.getPath())) {
            holder.checkBox.setCheckedWithoutCallback(true);
        } else {
            holder.checkBox.setCheckedWithoutCallback(false);
        }
        holder.imageView.setImageDrawable(new ColorDrawable(Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255))));
        ImageLoaderManager.displayImage("file:/" + mediaPhoto.getThumbPath(), holder.imageView, ImageLoaderManager.getGalleryOptions());
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
