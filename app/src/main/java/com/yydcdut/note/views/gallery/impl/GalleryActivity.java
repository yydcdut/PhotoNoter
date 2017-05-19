package com.yydcdut.note.views.gallery.impl;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.recycler.GalleryNavigationAdapter;
import com.yydcdut.note.adapter.recycler.vh.GalleryNavFooterViewHolder;
import com.yydcdut.note.entity.gallery.GalleryApp;
import com.yydcdut.note.presenters.gallery.impl.GalleryPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.ThemeHelper;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.gallery.IGalleryView;
import com.yydcdut.note.widget.StatusBarView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 16/4/2.
 */
public class GalleryActivity extends BaseActivity implements IGalleryView,
        NavigationView.OnNavigationItemSelectedListener, GalleryNavFooterViewHolder.OnNavFooterItemClickListener {
    public static final String INTENT_DATA = "path";
    @Inject
    GalleryPresenterImpl mGalleryPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.nav_view)
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
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return R.layout.activity_gallery;
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
        mGalleryPresenter.attachView(this);
        mIPresenter = mGalleryPresenter;
    }

    @Override
    public void initUiAndListener() {
        initToolBar();
        initDrawer();
        int color = ThemeHelper.getPrimaryColor(this);
        setDrawerStatusBar(color);
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

    /**
     * 设置statusBar
     *
     * @param color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDrawerStatusBar(int color) {
        if (!AppCompat.AFTER_KITKAT) {
            return;
        }
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        StatusBarView statusBarView = createStatusBarView(color, 255);
        ViewGroup contentLayout = (ViewGroup) mDrawerLayout.getChildAt(0);
        contentLayout.addView(statusBarView, 0);
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarSize(), 0, 0);
        }
        ViewGroup drawer = (ViewGroup) mDrawerLayout.getChildAt(1);
        mDrawerLayout.setFitsSystemWindows(false);
        contentLayout.setFitsSystemWindows(false);
        contentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
        addTranslucentView(100);
    }

    private void addTranslucentView(int statusBarAlpha) {
        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        if (contentView.getChildCount() > 1) {
            contentView.removeViewAt(1);
        }
        contentView.addView(createStatusBarView(Color.TRANSPARENT, statusBarAlpha));
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

    @Override
    public void finishWithData(ArrayList<String> data) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(INTENT_DATA, data);
        setResult(RESULT_DATA_IMAGE, intent);
        finish();
    }

    @Override
    public void finishWithoutData() {
        finish();
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        mGalleryPresenter.finishActivityAndReturnData();
    }
}
