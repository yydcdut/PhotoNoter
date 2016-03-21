package com.yydcdut.gallery.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.gallery.R;
import com.yydcdut.gallery.model.MediaPhoto;

import java.util.List;

/**
 * Created by yuyidong on 16/3/22.
 */
public class PhotoViewPager extends PagerAdapter {
    private List<MediaPhoto> mMediaPhotoList;

    public PhotoViewPager(List<MediaPhoto> mediaPhotoList) {
        mMediaPhotoList = mediaPhotoList;
    }

    @Override
    public int getCount() {
        return mMediaPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_pager_photo, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_item_detail);
        ImageLoader.getInstance().displayImage("file:/" + mMediaPhotoList.get(position).getPath(), imageView);
        container.addView(view); // 为viewpager增加布局
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
