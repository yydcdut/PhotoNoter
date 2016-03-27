package com.yydcdut.noteplugin.controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.NavigationAdapter;
import com.yydcdut.noteplugin.adapter.vh.NavFooterViewHolder;
import com.yydcdut.noteplugin.bean.GalleryApp;
import com.yydcdut.noteplugin.fragment.FilePhotoFragment;
import com.yydcdut.noteplugin.fragment.MediaPhotoFragment;
import com.yydcdut.noteplugin.model.SelectPhotoModel;
import com.yydcdut.noteplugin.utils.AppCompat;
import com.yydcdut.noteplugin.utils.Jumper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavFooterViewHolder.OnNavFooterItemClickListener {
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
    private FilePhotoFragment mFilePhotoFragment;

    private NavigationAdapter mNavigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        List<GalleryApp> galleryAppList = new ArrayList<>(info.size());
        for (int i = 0; i < info.size(); i++) {
            ActivityInfo activityInfo = info.get(i).activityInfo;
            galleryAppList.add(new GalleryApp(
                    activityInfo.loadIcon(getPackageManager()),
                    activityInfo.packageName,
                    activityInfo.loadLabel(getPackageManager()) + ""));
        }

        for (int i = 0; i < mNavigationView.getChildCount(); i++) {
            View view = mNavigationView.getChildAt(i);
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                mNavigationAdapter = new NavigationAdapter(recyclerView.getAdapter(), galleryAppList, this);
                recyclerView.setAdapter(mNavigationAdapter);
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mMediaPhotoFragment = MediaPhotoFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.layout_photo, mMediaPhotoFragment).commit();
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
        getMenuInflater().inflate(R.menu.main, menu);
        mPreviewMenu = menu.findItem(R.id.action_preview);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_preview) {
            if (SelectPhotoModel.getInstance().getCount() != 0) {
                Jumper.jump2DetailActivityPreviewSelected(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mMediaPhotoFragment == null) {
                mMediaPhotoFragment = MediaPhotoFragment.newInstance();
            }
            fragmentManager.beginTransaction().replace(R.id.layout_photo, mMediaPhotoFragment).commit();
        } else if (id == R.id.nav_file) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mFilePhotoFragment == null) {
                mFilePhotoFragment = FilePhotoFragment.newInstance();
            }
            fragmentManager.beginTransaction().replace(R.id.layout_photo, mFilePhotoFragment).commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNavFooterItemClick(GalleryApp galleryApp) {
        Intent jumpIntent = new Intent();
        jumpIntent.setType("image/*");
        jumpIntent.setPackage(galleryApp.getPackageName());
        jumpIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(jumpIntent);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public MenuItem getPreviewMenu() {
        return mPreviewMenu;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_CODE && resultCode == BaseActivity.CODE_RESULT_CHANGED) {
            mMediaPhotoFragment.notifyAdapterDataChanged();
            if (SelectPhotoModel.getInstance().getCount() == 0) {
                mPreviewMenu.setTitle(getResources().getString(R.string.action_view));
            } else {
                mPreviewMenu.setTitle(getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
