package com.yydcdut.noteplugin.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yuyidong on 16/3/23.
 */
public class PhotoDetailPagerAdapter extends PagerAdapter implements PhotoViewAttacher.OnPhotoTapListener {
    private List<String> mPhotoPathList;

    public PhotoDetailPagerAdapter(List<String> photoPathList) {
        mPhotoPathList = photoPathList;
    }

    @Override
    public int getCount() {
        return mPhotoPathList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final PhotoView photoView = new PhotoView(container.getContext());
        ImageLoader.getInstance().displayImage("file:/" + mPhotoPathList.get(position), photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnPhotoTapListener(this);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        if (mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick(view);
        }
    }

    private OnPhotoClickListener mOnPhotoClickListener;

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        mOnPhotoClickListener = onPhotoClickListener;
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(View view);
    }
}
