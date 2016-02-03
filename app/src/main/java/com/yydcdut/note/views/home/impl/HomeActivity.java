package com.yydcdut.note.views.home.impl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.CategoryAdapter;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.presenters.home.IHomePresenter;
import com.yydcdut.note.presenters.home.impl.HomePresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.home.IHomeView;
import com.yydcdut.note.views.login.impl.LoginActivity;
import com.yydcdut.note.views.login.impl.UserCenterActivity;
import com.yydcdut.note.widget.RoundedImageView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements IHomeView, View.OnClickListener {
    private static final int LAYOUT_FRAGMENT_ID = R.id.container;
    private long mLastBackTime = 0;//退出程序用

    private AlbumFragment mFragment;//相册的fragment
    private CategoryAdapter mCategoryAdapter;
    /* User header */
    public TextView mUserName;
    public RoundedImageView mUserPhoto;
    public ImageView mUserPhotoTwo;
    public ImageView mUserBackground;
    /* Cloud footer */
    public View mCloudSyncImage;
    public TextView mCloudUseText;
    public ProgressBar mCloudUseProgress;
    /* Drawer */
    @Bind(R.id.lv_navigation)
    ListView mMenuListView;
    @Bind(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.relativeDrawer)
    FrameLayout mRelativeDrawer;//Fragment
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Inject
    HomePresenterImpl mHomePresenter;

    private ActionBarDrawerToggleCompat mDrawerToggle;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return R.layout.navigation_main;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mHomePresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initReceiver();
        if (savedInstanceState != null) {
            int categoryId = savedInstanceState.getInt(Const.CATEGORY_ID_4_PHOTNOTES, -1);
            if (categoryId != -1) {
                mHomePresenter.setCategoryId(categoryId);
            }
        }
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mHomePresenter.attachView(this);
        mMenuListView.setOnItemClickListener(new DrawerItemClickListener());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (AppCompat.AFTER_LOLLIPOP) {
            try {
                TypedArray typedArray = this.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark});
                mDrawerLayout.setStatusBarBackground(typedArray.getResourceId(0, 0));
            } catch (Exception e) {
                e.getMessage();
            }

        }
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));

        mDrawerToggle = new ActionBarDrawerToggleCompat(this, mDrawerLayout, mToolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        createUserDefaultHeader();
        createFooter();
        mHomePresenter.setAdapter();
        mHomePresenter.updateQQInfo();
        mHomePresenter.updateEvernoteInfo();
        mUserBackground.setImageDrawable(getResources().getDrawable(R.drawable.bg_user_background));

        mHomePresenter.setCheckCategoryPosition();
    }

    /**
     * 注册广播
     */
    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        registerReceiver(mUpdatePhotoNoteList, intentFilter);
    }

    /**
     * 广播，收到广播之后发消息
     * 这里面只做UI方面的处理，不做数据存储方面的处理
     */
    private BroadcastReceiver mUpdatePhotoNoteList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHomePresenter.updateFromBroadcast(intent.getBooleanExtra(Const.TARGET_BROADCAST_PROCESS, false),
                    intent.getBooleanExtra(Const.TARGET_BROADCAST_SERVICE, false));
        }
    };

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        unregisterReceiver(mUpdatePhotoNoteList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    private class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

        public ActionBarDrawerToggleCompat(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar) {
            super(
                    activity,
                    drawerLayout, toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close);
        }

        @Override
        public void onDrawerClosed(View view) {
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            supportInvalidateOptionsMenu();
            try {
                mFragment.isMenuSelectModeAndChangeIt();
            } catch (Exception e) {
                //有时候mFragment会为空
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * ListView的Item点击事件
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //因为有header
            int realPosition = (position - 1);
            if (position == parent.getCount() - 1) {
                //footer
                return;
            }

            if (position != 0) {
                //更新位置信息
                mHomePresenter.setCheckedCategoryPosition(realPosition);
            }
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_userPhoto:
                mHomePresenter.drawerUserClick(IHomePresenter.USER_ONE);
                mDrawerLayout.closeDrawer(mRelativeDrawer);
                break;
            case R.id.img_userPhotoTwo:
                mHomePresenter.drawerUserClick(IHomePresenter.USER_TWO);
                mDrawerLayout.closeDrawer(mRelativeDrawer);
                break;
            case R.id.img_list_footer_cloud:
                mHomePresenter.drawerCloudClick();
                break;
        }
    }

    private void createUserDefaultHeader() {
        View header = getLayoutInflater().inflate(R.layout.navigation_list_header, mMenuListView, false);
        mUserName = (TextView) header.findViewById(R.id.txt_userName);
        mUserPhoto = (RoundedImageView) header.findViewById(R.id.img_userPhoto);
        mUserPhoto.setOnClickListener(this);
        mUserPhotoTwo = (ImageView) header.findViewById(R.id.img_userPhotoTwo);
        mUserPhotoTwo.setOnClickListener(this);
        mUserBackground = (ImageView) header.findViewById(R.id.img_userBackground);
        mMenuListView.addHeaderView(header);
    }

    private void createFooter() {
        View footer = getLayoutInflater().inflate(R.layout.navigation_list_footer, null, false);
        mCloudSyncImage = footer.findViewById(R.id.img_list_footer_cloud);
        mCloudSyncImage.setOnClickListener(this);
        mCloudUseText = (TextView) footer.findViewById(R.id.txt_list_footer_use);
        mCloudUseProgress = (ProgressBar) footer.findViewById(R.id.pb_list_footer);
        mCloudUseProgress.setProgress(0);
        mMenuListView.addFooterView(footer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_DATA_QQ) {
            mHomePresenter.updateQQInfo();
            openDrawer();
        } else if (resultCode == RESULT_DATA_EVERNOTE) {
            mHomePresenter.updateEvernoteInfo();
            openDrawer();
        } else if (resultCode == RESULT_DATA_USER) {
            mHomePresenter.updateQQInfo();
            mHomePresenter.updateEvernoteInfo();
            openDrawer();
        }
    }


    @Override
    public void onBackPressed() {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        } else {
            try {
                if (mFragment.ifRevealOpenAndCloseIt()) {//就在那个函数里面关闭了
                } else if (mFragment.isMenuSelectModeAndChangeIt()) {//就在那个函数里面换了模式了
                } else if (mFragment.isLayoutRevealOpen()) {//不做其他操作
                } else {
                    if (System.currentTimeMillis() - mLastBackTime > 2000) {
                        Toast.makeText(this, "再点击一次退出!", Toast.LENGTH_SHORT).show();
                        mLastBackTime = System.currentTimeMillis();
                    } else {
                        mHomePresenter.killCameraService();
                        super.onBackPressed();
                    }
                }
            } catch (Exception e) {
                //有时候mFragment会为空
            }
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mRelativeDrawer);
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mRelativeDrawer);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRelativeDrawer);
    }

    @Override
    public void cloudSyncAnimation() {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.cloud_sync_rotation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mCloudSyncImage.startAnimation(operatingAnim);
    }

    @Override
    public void updateQQInfo(boolean isLogin, String name, String imagePath) {
        if (isLogin) {
            mUserName.setVisibility(View.VISIBLE);
            mUserName.setText(name);
            ImageLoaderManager.displayImage(imagePath, mUserPhoto);
        } else {
            mUserName.setVisibility(View.INVISIBLE);
            mUserPhoto.setImageResource(R.drawable.ic_no_user);
        }
    }

    @Override
    public void updateEvernoteInfo(boolean isLogin) {
        if (isLogin) {
            mUserPhotoTwo.setImageResource(R.drawable.ic_evernote_color);
        } else {
            mUserPhotoTwo.setImageResource(R.drawable.ic_evernote_gray);
        }
    }

    @Override
    public void setCategoryList(List<Category> list) {
        mCategoryAdapter = new CategoryAdapter(this, list);
        mMenuListView.setAdapter(mCategoryAdapter);
    }

    @Override
    public void updateCategoryList(List<Category> list) {
        mCategoryAdapter.resetGroup(list);
    }

    @Override
    public void notifyCategoryDataChanged() {
        mCategoryAdapter.notifyDataSetChanged();
    }


    @Override
    public void changeFragment(int categoryId) {
        if (mFragment == null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            mFragment = new AlbumFragment().newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt(Const.CATEGORY_ID_4_PHOTNOTES, categoryId);
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(LAYOUT_FRAGMENT_ID, mFragment).commit();
        } else {
            mFragment.changePhotos4Category(categoryId);
        }
    }

    @Override
    public void changePhotos4Category(int categoryId) {
        mFragment.changePhotos4Category(categoryId);
    }

    @Override
    public void jump2LoginActivity() {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_NOTHING);
    }

    @Override
    public void jump2UserCenterActivity() {
        startActivityForResult(new Intent(this, UserCenterActivity.class), REQUEST_NOTHING);
    }

    @Override
    public void setCheckPosition(int position) {
        mHomePresenter.setCheckedCategoryPosition(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Const.CATEGORY_ID_4_PHOTNOTES, mHomePresenter.getCategoryId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mHomePresenter.setCategoryId(savedInstanceState.getInt(Const.CATEGORY_ID_4_PHOTNOTES));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver();
        mHomePresenter.detachView();
        super.onDestroy();
    }

    public void changeCategoryAfterSaving(Category category) {
        mToolbar.setTitle(category.getLabel());
        mHomePresenter.changeCategoryAfterSaving(category);
    }

    public void changeTitle(String title) {
        mToolbar.setTitle(title);
    }
}
