package com.yydcdut.note.controller.home;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.NavigationCategoryAdapter;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.RoundedImageView;

import java.util.List;

public abstract class NavigationActivity extends BaseActivity {
    public static final int USER_ONE = 1;
    public static final int USER_TWO = 2;
    /* User */
    public TextView mUserName;
    public RoundedImageView mUserPhoto;
    public RoundedImageView mUserPhotoTwo;
    public ImageView mUserBackground;
    /* Cloud */
    public View mCloudSyncImage;
    public TextView mCloudUseText;
    public ProgressBar mCloudUseProgress;
    /* Drawer */
    private ListView mMenuListView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggleCompat mDrawerToggle;
    /* Fragment */
    private FrameLayout mRelativeDrawer;

    private NavigationListener mNavigationListener;

    private NavigationCategoryAdapter mCategoryAdapter;

    public static final String CURRENT_POSITION = "CURRENT_POSITION";

    @Override
    public int setContentView() {
        return R.layout.navigation_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMenuListView = (ListView) findViewById(R.id.lv_navigation);
        mMenuListView.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        if (LollipopCompat.AFTER_LOLLIPOP) {
            try {
                Resources.Theme theme = this.getTheme();
                TypedArray typedArray = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
                mDrawerLayout.setStatusBarBackground(typedArray.getResourceId(0, 0));
            } catch (Exception e) {
                e.getMessage();
            }

        }
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));

        mDrawerToggle = new ActionBarDrawerToggleCompat(this, mDrawerLayout, toolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRelativeDrawer = (FrameLayout) this.findViewById(R.id.relativeDrawer);

        if (mMenuListView != null) {
            createUserDefaultHeader();
            createFooter();
            onUserInformation();
            onCloudInformation();
            initNavigationListener();
            setNavigationAdapter();
        }

        int position = getCheckedPosition();
        mNavigationListener.onItemClickNavigation(position, R.id.container);
    }

    public abstract int getCheckedPosition();

    public abstract void initNavigationListener();

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
            if (mOnDrawerListener != null) {
                mOnDrawerListener.onDrawerClose();
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            supportInvalidateOptionsMenu();
            if (mOnDrawerListener != null) {
                mOnDrawerListener.onDrawerOpen();
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
                mNavigationListener.onItemClickNavigation(realPosition, R.id.container);
            }
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    }

    private OnClickListener mOnHeaderAndFooterClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_userPhoto:
                    mNavigationListener.onClickUserPhotoNavigation(v, USER_ONE);
                    mDrawerLayout.closeDrawer(mRelativeDrawer);
                    break;
                case R.id.img_userPhotoTwo:
                    mNavigationListener.onClickUserPhotoNavigation(v, USER_TWO);
                    break;
                case R.id.img_list_footer_cloud:
                    mNavigationListener.onClickCloudSync(v);
                    break;
            }
        }
    };

    /**
     * 设置Adapter
     */
    private void setNavigationAdapter() {
        if (mNavigationListener == null) {
            throw new RuntimeException(getString(R.string.start_navigation_listener));
        }

        List<Category> list = setCategoryAdapter();
        mCategoryAdapter = new NavigationCategoryAdapter(NavigationActivity.this, list);
        mMenuListView.setAdapter(mCategoryAdapter);
    }

    public abstract List<Category> setCategoryAdapter();

    public NavigationCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }

    private void createUserDefaultHeader() {
        View header = getLayoutInflater().inflate(R.layout.layout_navigation_list_header, mMenuListView, false);

        mUserName = (TextView) header.findViewById(R.id.txt_userName);
        mUserPhoto = (RoundedImageView) header.findViewById(R.id.img_userPhoto);
        mUserPhoto.setOnClickListener(mOnHeaderAndFooterClickListener);
        mUserPhotoTwo = (RoundedImageView) header.findViewById(R.id.img_userPhotoTwo);
        mUserPhotoTwo.setOnClickListener(mOnHeaderAndFooterClickListener);
        mUserBackground = (ImageView) header.findViewById(R.id.img_userBackground);
        mMenuListView.addHeaderView(header);
    }

    private void createFooter() {
        View footer = getLayoutInflater().inflate(R.layout.navigation_list_footer, null, false);
        mCloudSyncImage = footer.findViewById(R.id.img_list_footer_cloud);
        mCloudSyncImage.setOnClickListener(mOnHeaderAndFooterClickListener);
        mCloudUseText = (TextView) footer.findViewById(R.id.txt_list_footer_use);
        mCloudUseProgress = (ProgressBar) footer.findViewById(R.id.pb_list_footer);
        mCloudUseProgress.setProgress(0);
        mMenuListView.addFooterView(footer);
    }

    /**
     * User information
     */
    public abstract void onUserInformation();

    /**
     * footer cloud
     */
    public abstract void onCloudInformation();

    /**
     * Open drawer
     */
    public void openDrawer() {
        mDrawerLayout.openDrawer(mRelativeDrawer);
    }

    /**
     * Close drawer
     */
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRelativeDrawer);
    }

    @Override
    public void onBackPressed() {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 设置navigation监听器
     *
     * @param navigationListener listener.
     */
    public void setNavigationListener(NavigationListener navigationListener) {
        this.mNavigationListener = navigationListener;
    }

    public interface NavigationListener {

        /**
         * ListView的点击了哪个item
         *
         * @param position
         * @param layoutContainerId
         */
        void onItemClickNavigation(int position, int layoutContainerId);

        /**
         * Click user photo navigation
         *
         * @param v     view.
         * @param which 点击的哪一个
         */
        void onClickUserPhotoNavigation(View v, final int which);

        /**
         * 点击异步云同步
         *
         * @param v
         */
        void onClickCloudSync(View v);
    }

    private OnDrawerListener mOnDrawerListener;

    public void setOnDrawerListener(OnDrawerListener listener) {
        mOnDrawerListener = listener;
    }

    public interface OnDrawerListener {
        void onDrawerOpen();

        void onDrawerClose();
    }

}
