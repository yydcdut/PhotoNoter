package com.yydcdut.gallery.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yydcdut.gallery.fragment.PhotoDetailFragment;
import com.yydcdut.gallery.model.MediaPhoto;
import com.yydcdut.gallery.model.SelectPhotoModel;
import com.yydcdut.gallery.utils.YLog;

import java.util.List;

/**
 * Created by yuyidong on 16/3/23.
 */
public class PhotoDetailPagerAdapter extends FragmentPagerAdapter {
    private boolean misPreviewSelected;
    private List<MediaPhoto> mMediaPhotoList;

    public PhotoDetailPagerAdapter(@NonNull FragmentManager fm, @NonNull boolean isPreviewSelected,
                                   @Nullable List<MediaPhoto> mediaPhotoList) {
        super(fm);
        misPreviewSelected = isPreviewSelected;
        mMediaPhotoList = mediaPhotoList;
    }

    @Override
    public Fragment getItem(int position) {
        PhotoDetailFragment photoDetailFragment = PhotoDetailFragment.getInstance();
        Bundle bundle = new Bundle();
        if (misPreviewSelected) {
            bundle.putString(PhotoDetailFragment.PHOTO_PATH, SelectPhotoModel.getInstance().get(position));
        } else {
            bundle.putString(PhotoDetailFragment.PHOTO_PATH, mMediaPhotoList.get(position).getPath());
            YLog.i("yuyidong", "111111       " + mMediaPhotoList.get(position).getPath());
        }
        photoDetailFragment.setArguments(bundle);
        return photoDetailFragment;
    }

    @Override
    public int getCount() {
        if (misPreviewSelected) {
            return SelectPhotoModel.getInstance().getCount();
        } else {
            return mMediaPhotoList.size();
        }
    }
}
