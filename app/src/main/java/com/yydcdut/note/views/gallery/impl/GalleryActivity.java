package com.yydcdut.note.views.gallery.impl;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.GalleryNavigationAdapter;
import com.yydcdut.note.adapter.vh.GalleryNavFooterViewHolder;
import com.yydcdut.note.bean.gallery.GalleryApp;
import com.yydcdut.note.presenters.gallery.impl.GalleryPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.gallery.IGalleryView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/4/2.
 */
public class GalleryActivity extends BaseActivity implements IGalleryView,
        NavigationView.OnNavigationItemSelectedListener, GalleryNavFooterViewHolder.OnNavFooterItemClickListener {
    @Inject
    GalleryPresenterImpl mGalleryPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    private MenuItem mPreviewMenu;

    private MediaPhotoFragment mMediaPhotoFragment;

    private GalleryNavigationAdapter mGalleryNavigationAdapter;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_gallery;
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
        mGalleryPresenter.attachView(this);
    }

    @Override
    public void initUiAndListener() {
        initToolBar();
        initDrawer();
        initThirdGalleryAppAdapter(mGalleryPresenter.getGalleryAppList());
        FragmentManager fragmentManager = getFragmentManager();
        mMediaPhotoFragment = MediaPhotoFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.layout_photo, mMediaPhotoFragment).commit();
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initThirdGalleryAppAdapter(List<GalleryApp> galleryAppList) {
        for (int i = 0; i < mNavigationView.getChildCount(); i++) {
            View view = mNavigationView.getChildAt(i);
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                mGalleryNavigationAdapter = new GalleryNavigationAdapter(recyclerView.getAdapter(), galleryAppList, this);
                recyclerView.setAdapter(mGalleryNavigationAdapter);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {
            FragmentManager fragmentManager = getFragmentManager();
            if (mMediaPhotoFragment == null) {
                mMediaPhotoFragment = MediaPhotoFragment.newInstance();
            }
            fragmentManager.beginTransaction().replace(R.id.layout_photo, mMediaPhotoFragment).commit();
        } else if (id == R.id.nav_file) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            if (mFilePhotoFragment == null) {
//                mFilePhotoFragment = FilePhotoFragment.newInstance();
//            }
//            fragmentManager.beginTransaction().replace(R.id.layout_photo, mFilePhotoFragment).commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery_main, menu);
        mPreviewMenu = menu.findItem(R.id.action_preview);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_preview) {
            mGalleryPresenter.jump2SelectedDetailActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public MenuItem getPreviewMenu() {
        return mPreviewMenu;
    }

    @Override
    public void onNavFooterItemClick(@NonNull GalleryApp galleryApp) {
        Intent jumpIntent = new Intent();
        jumpIntent.setType("image/*");
        jumpIntent.setPackage(galleryApp.getPackageName());
        jumpIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(jumpIntent);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGalleryPresenter.onReturnData(requestCode, resultCode, data);
        //// TODO: 16/4/5 第三方
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void jump2SelectedDetailActivity() {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PREVIEW_SELECTED, true);
        startActivityForResult(intent, BaseActivity.REQUEST_CODE);
    }

    @Override
    public void setPreviewMenuTitle(String title) {
        mPreviewMenu.setTitle(title);
    }

    @Override
    public void notifyDataChanged(int... positions) {
        mMediaPhotoFragment.notifyAdapterDataChanged();
    }
}
