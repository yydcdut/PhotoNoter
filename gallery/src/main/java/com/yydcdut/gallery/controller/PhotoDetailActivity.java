package com.yydcdut.gallery.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.adapter.PhotoDetailPagerAdapter;
import com.yydcdut.gallery.model.MediaPhoto;
import com.yydcdut.gallery.model.PhotoModel;
import com.yydcdut.gallery.utils.AppCompat;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/21.
 */
public class PhotoDetailActivity extends BaseActivity {

    @Bind(R.id.vp_detail)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        if (AppCompat.AFTER_LOLLIPOP) {
            AppCompat.setFullWindow(getWindow());
        }
        ButterKnife.bind(this);
        PhotoDetailPagerAdapter photoDetailPagerAdapter = null;
        if (getIntent().getBooleanExtra(INTENT_PREVIEW_SELECTED, false)) {
            photoDetailPagerAdapter = new PhotoDetailPagerAdapter(getSupportFragmentManager(), true, null);
            mViewPager.setAdapter(photoDetailPagerAdapter);
        } else {
            int initPage = getIntent().getIntExtra(INTENT_PAGE, 0);
            String folderName = getIntent().getStringExtra(INTENT_FOLDER);
            List<MediaPhoto> mediaPhotoList = PhotoModel.getInstance().findByMedia(this).get(folderName).getMediaPhotoList();
            photoDetailPagerAdapter = new PhotoDetailPagerAdapter(getSupportFragmentManager(), false, mediaPhotoList);
            mViewPager.setAdapter(photoDetailPagerAdapter);
            mViewPager.setCurrentItem(initPage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
