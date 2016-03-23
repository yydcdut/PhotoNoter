package com.yydcdut.gallery.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.adapter.AbsPhotoPagerAdapter;
import com.yydcdut.gallery.adapter.PhotoAllPagerAdapter;
import com.yydcdut.gallery.adapter.PhotoSelectedPagerAdapter;
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

    private AbsPhotoPagerAdapter mPhotoPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        if (AppCompat.AFTER_LOLLIPOP) {
            AppCompat.setFullWindow(getWindow());
        }
        ButterKnife.bind(this);
        if (getIntent().getBooleanExtra(INTENT_PREVIEW_SELECTED, false)) {
            mPhotoPagerAdapter = new PhotoSelectedPagerAdapter();
            mViewPager.setAdapter(mPhotoPagerAdapter);
        } else {
            int initPage = getIntent().getIntExtra(INTENT_PAGE, 0);
            String folderName = getIntent().getStringExtra(INTENT_FOLDER);
            List<MediaPhoto> mediaPhotoList = PhotoModel.getInstance().findByMedia(this).get(folderName).getMediaPhotoList();
            mPhotoPagerAdapter = new PhotoAllPagerAdapter(mediaPhotoList);
            mViewPager.setAdapter(mPhotoPagerAdapter);
            mViewPager.setCurrentItem(initPage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
