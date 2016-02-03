package com.yydcdut.note.views.note.impl;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.note.impl.MapPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.note.IMapView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/1/8.
 */
public class MapActivity extends BaseActivity implements IMapView {
    @Bind(R.id.map)
    MapView mMapView;/* map */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    MapPresenterImpl mMapPresenter;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_map;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        Bundle bundle = getIntent().getExtras();
        mMapPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES), bundle.getInt(Const.PHOTO_POSITION),
                bundle.getInt(Const.COMPARATOR_FACTORY));
        mIPresenter = mMapPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mMapPresenter.attachView(this);
        initToolBarUI();
        mMapView.showZoomControls(false);//隐藏缩放控件
    }

    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(" ");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
                setResult(RESULT_NOTHING, null);
                finish();
                overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public BaiduMap getBaiduMap() {
        return mMapView.getMap();
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
        setResult(RESULT_NOTHING, null);
        finish();
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
    }
}
