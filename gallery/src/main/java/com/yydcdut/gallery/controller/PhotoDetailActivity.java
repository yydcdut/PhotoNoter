package com.yydcdut.gallery.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.adapter.PhotoViewPager;
import com.yydcdut.gallery.model.MediaPhoto;
import com.yydcdut.gallery.model.PhotoModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/21.
 */
public class PhotoDetailActivity extends BaseActivity {

    @Bind(R.id.vp_detail)
    ViewPager mViewPager;

    private PhotoViewPager mPhotoViewPager;

    private int mInitPage;
    private List<MediaPhoto> mMediaPhotoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        mInitPage = getIntent().getIntExtra(INTENT_PAGE, 0);
        String folderName = getIntent().getStringExtra(INTENT_FOLDER);
        mMediaPhotoList = PhotoModel.getInstance().findByMedia(this).get(folderName).getMediaPhotoList();
        mPhotoViewPager = new PhotoViewPager(mMediaPhotoList);
        mViewPager.setAdapter(mPhotoViewPager);
        mViewPager.setCurrentItem(mInitPage);

    }
}
