/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yydcdut.note.controller.home;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.yydcdut.note.utils.LollipopUtils;
import com.yydcdut.note.view.RoundedImageView;

import java.util.List;


public abstract class NavigationActivity extends BaseActivity {

    public static final int USER_ONE = 1;
    public static final int USER_TWO = 2;

    public TextView mUserName;
    public RoundedImageView mUserPhoto;
    public RoundedImageView mUserPhotoTwo;
    public ImageView mUserBackground;

    public View mCloudSyncImage;
    public TextView mCloudUseText;
    public ProgressBar mCloudUseProgress;

    private ListView mList;
    private Toolbar mToolbar;

    private int mCurrentPosition = 0;

    private DrawerLayout mDrawerLayout;
    private FrameLayout mRelativeDrawer;

    private ActionBarDrawerToggleCompat mDrawerToggle;
    private NavigationLiveoListener mNavigationListener;

    public static final String CURRENT_POSITION = "CURRENT_POSITION";

    /**
     * onCreate(Bundle savedInstanceState).
     *
     * @param savedInstanceState onCreate(Bundle savedInstanceState).
     */
    public abstract void onCreateInit(Bundle savedInstanceState);

    @Override
    public int setContentView() {
        return R.layout.navigation_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            setCurrentPosition(savedInstanceState.getInt(CURRENT_POSITION));
        }

        mList = (ListView) findViewById(R.id.list);
        mList.setOnItemClickListener(new DrawerItemClickListener());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mDrawerToggle = new ActionBarDrawerToggleCompat(this, mDrawerLayout, mToolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRelativeDrawer = (FrameLayout) this.findViewById(R.id.relativeDrawer);

        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (LollipopUtils.AFTER_LOLLIPOP) {
            try {
                Resources.Theme theme = this.getTheme();
                TypedArray typedArray = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
                mDrawerLayout.setStatusBarBackground(typedArray.getResourceId(0, 0));
            } catch (Exception e) {
                e.getMessage();
            }

        }
        LollipopUtils.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));

        if (mList != null) {
            mountListNavigation(savedInstanceState);
        }

        if (savedInstanceState == null) {
            mNavigationListener.onItemClickNavigation(mCurrentPosition, R.id.container);
        }

        setCheckedItemNavigation(mCurrentPosition, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
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
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        mNavigationListener.onPrepareOptionsMenuNavigation(menu, mCurrentPosition, drawerOpen);
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
            if (mOnDrawerListener != null) {
                mOnDrawerListener.onClose();
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            supportInvalidateOptionsMenu();
            if (mOnDrawerListener != null) {
                mOnDrawerListener.onOpen();
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int mPosition = (position - 1);
            if (position == parent.getCount() - 1) {
                //footer
                return;
            }

            if (position != 0) {
                mNavigationListener.onItemClickNavigation(mPosition, R.id.container);
                setCurrentPosition(mPosition);
                setCheckedItemNavigation(mPosition, true);
            }

            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    }

    public void setCheckedItemNavigation(int position, boolean checked) {
        this.mCategoryAdapter.resetCheck();
        this.mCategoryAdapter.setChecked(position, checked);
    }

    private OnClickListener onClickHeaderAndFooter = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.userPhoto:
                    mNavigationListener.onClickUserPhotoNavigation(v, USER_ONE);
                    mDrawerLayout.closeDrawer(mRelativeDrawer);
                    break;
                case R.id.userPhotoTwo:
                    mNavigationListener.onClickUserPhotoNavigation(v, USER_TWO);
                    break;
                case R.id.img_list_footer_cloud:
                    mNavigationListener.onClickCloudSync(v);
                    break;
                case R.id.userData:

                    break;
            }
        }
    };


    private void mountListNavigation(Bundle savedInstanceState) {
        createUserDefaultHeader();
        createFooter();
        onUserInformation();
        onUserAccounts();
        onCloudInformation();
        onCreateInit(savedInstanceState);
        setAdapterNavigation();
    }

    public abstract List<Category> setCategoryAdapter();

    private NavigationCategoryAdapter mCategoryAdapter;

    private void setAdapterNavigation() {

        if (mNavigationListener == null) {
            throw new RuntimeException(getString(R.string.start_navigation_listener));
        }

        List<Category> list = setCategoryAdapter();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCheck()) {
                mCurrentPosition = i;
            }
        }
        mCategoryAdapter = new NavigationCategoryAdapter(NavigationActivity.this, list);
        mList.setAdapter(mCategoryAdapter);
    }

    public NavigationCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }

    /**
     * Create user default header
     */
    private void createUserDefaultHeader() {
        View header = getLayoutInflater().inflate(R.layout.navigation_list_header, mList, false);

        mUserName = (TextView) header.findViewById(R.id.userName);
        mUserPhoto = (RoundedImageView) header.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(onClickHeaderAndFooter);
        mUserPhotoTwo = (RoundedImageView) header.findViewById(R.id.userPhotoTwo);
        mUserPhotoTwo.setOnClickListener(onClickHeaderAndFooter);
        mUserBackground = (ImageView) header.findViewById(R.id.userBackground);
        mList.addHeaderView(header);
    }

    private void createFooter() {
        View footer = getLayoutInflater().inflate(R.layout.navigation_list_footer, null, false);
        mCloudSyncImage = footer.findViewById(R.id.img_list_footer_cloud);
        mCloudSyncImage.setOnClickListener(onClickHeaderAndFooter);
        mCloudUseText = (TextView) footer.findViewById(R.id.txt_list_footer_use);
        mCloudUseProgress = (ProgressBar) footer.findViewById(R.id.pb_list_footer);
        mCloudUseProgress.setProgress(0);
        mList.addFooterView(footer);
    }

    /**
     * User information
     */
    public abstract void onUserInformation();

    public abstract void onUserAccounts();

    /**
     * footer cloud
     */
    public abstract void onCloudInformation();

    /**
     * Starting listener navigation
     *
     * @param navigationListener listener.
     */
    public void setNavigationListener(NavigationLiveoListener navigationListener) {
        this.mNavigationListener = navigationListener;
    }

    /**
     * First item of the position selected from the list
     *
     * @param position ...
     */
    public void setDefaultStartPositionNavigation(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * Position in the last clicked item list
     *
     * @param position ...
     */
    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * get position in the last clicked item list
     */
    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }

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

    public interface NavigationLiveoListener {

        /**
         * Item click Navigation (ListView.OnItemClickListener)
         *
         * @param position          position of the item that was clicked.
         * @param layoutContainerId Default layout. Tell the replace - FragmentManager.beginTransaction().replace(layoutContainerId, yourFragment).commit()
         */
        public void onItemClickNavigation(int position, int layoutContainerId);

        /**
         * Prepare options menu navigation (onPrepareOptionsMenu(Menu menu))
         *
         * @param menu     menu.
         * @param position last position of the item that was clicked.
         * @param visible  use to hide the menu when the navigation is open.
         */
        public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible);

        /**
         * Click user photo navigation
         *
         * @param v     view.
         * @param which 点击的哪一个
         */
        public void onClickUserPhotoNavigation(View v, final int which);

        /**
         * 点击异步云同步
         *
         * @param v
         */
        public void onClickCloudSync(View v);
    }

    public interface OnDrawerListener {
        public void onOpen();

        public void onClose();
    }

    private OnDrawerListener mOnDrawerListener;

    public void setOnDrawerListener(OnDrawerListener listener) {
        mOnDrawerListener = listener;
    }


}
