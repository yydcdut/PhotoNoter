package com.yydcdut.note.controller.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.login.LoginActivity;
import com.yydcdut.note.controller.login.UserCenterActivity;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.observer.CategoryChangedObserver;
import com.yydcdut.note.model.observer.PhotoNoteChangedObserver;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.io.File;
import java.util.List;


/**
 * Created by yuyidong on 15-3-23.
 */
public class HomeActivity extends NavigationActivity implements NavigationActivity.NavigationListener,
        NavigationActivity.OnDrawerListener, PhotoNoteChangedObserver, CategoryChangedObserver {
    /**
     * 数据
     */
    private List<Category> mListData;
    /**
     * 相册的fragment
     */
    private AlbumFragment mFragment;
    /**
     * 当前的category的label
     */
    private String mCategoryLabel;
    /**
     * 退出程序用
     */
    private long mLastBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String category = savedInstanceState.getString(Const.CATEGORY_LABEL, "NULL");
            if (!category.equals("NULL")) {
                mCategoryLabel = category;
            }
        }
    }

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        initReceiver();
        return super.setContentView();
    }

    @Override
    public void initNavigationListener() {
        setNavigationListener(this);
        setOnDrawerListener(this);
    }

    @Override
    public List<Category> setCategoryAdapter() {
        mListData = CategoryDBModel.getInstance().findAll();
        PhotoNoteDBModel.getInstance().addObserver(this);
        CategoryDBModel.getInstance().addObserver(this);
        return mListData;
    }

    @Override
    public void onUserInformation() {
        mUserBackground.setImageDrawable(getResources().getDrawable(R.drawable.bg_user_background));
        updateQQUserInfo();
        updateEvernoteUserInfo();
    }

    @Override
    public int getCheckedPosition() {
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).isCheck()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onCloudInformation() {

    }

    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {
        //更新位置信息
        CategoryDBModel.getInstance().setCategoryMenuPosition(mListData.get(position));
        getCategoryAdapter().resetGroup(mListData);
        mCategoryLabel = mListData.get(position).getLabel();
        if (mFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mFragment = new AlbumFragment().newInstance();
            Bundle bundle = new Bundle();
            bundle.putString(Const.CATEGORY_LABEL, mCategoryLabel);
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(layoutContainerId, mFragment).commit();
        } else {
            mFragment.changePhotos4Category(mCategoryLabel);
        }
    }

    public void changeCategoryAfterSaving(Category category) {
        //更新位置信息
        CategoryDBModel.getInstance().setCategoryMenuPosition(category);
        mListData = CategoryDBModel.getInstance().refresh();
        getCategoryAdapter().notifyDataSetChanged();
        mCategoryLabel = category.getLabel();
        mFragment.changePhotos4Category(mCategoryLabel);
    }

    @Override
    public void onClickUserPhotoNavigation(View v, final int which) {
        switch (which) {
            case USER_ONE:
                if (UserCenter.getInstance().isLoginQQ()) {
                    startActivityForResult(new Intent(HomeActivity.this, UserCenterActivity.class), REQUEST_NOTHING);
                } else {
                    startActivityForResult(new Intent(HomeActivity.this, LoginActivity.class), REQUEST_NOTHING);
                }
                break;
            case USER_TWO:
                if (UserCenter.getInstance().isLoginEvernote()) {
                    startActivityForResult(new Intent(HomeActivity.this, UserCenterActivity.class), REQUEST_NOTHING);
                } else {
                    startActivityForResult(new Intent(HomeActivity.this, LoginActivity.class), REQUEST_NOTHING);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_DATA_QQ) {
            updateQQUserInfo();
            openDrawer();
        } else if (resultCode == RESULT_DATA_EVERNOTE) {
            updateEvernoteUserInfo();
            openDrawer();
        } else if (resultCode == RESULT_DATA_USER) {
            updateQQUserInfo();
            updateEvernoteUserInfo();
            openDrawer();
        }
    }

    private void updateQQUserInfo() {
        if (UserCenter.getInstance().getQQ() != null && UserCenter.getInstance().isLoginQQ()) {
            IUser qqUser = UserCenter.getInstance().getQQ();
            if (new File(FilePathUtils.getQQImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), mUserPhoto);
            } else {
                ImageLoaderManager.displayImage(qqUser.getNetImagePath(), mUserPhoto);
                FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), ImageLoaderManager.loadImageSync(qqUser.getNetImagePath()));
            }
            mUserName.setVisibility(View.VISIBLE);
            mUserName.setText(qqUser.getName());
        } else {
            mUserName.setVisibility(View.INVISIBLE);
            mUserPhoto.setImageResource(R.drawable.ic_no_user);
        }
    }

    private void updateEvernoteUserInfo() {
        if (UserCenter.getInstance().isLoginEvernote()) {
            mUserPhotoTwo.setImageResource(R.drawable.ic_evernote_color);
        } else {
            mUserPhotoTwo.setImageResource(R.drawable.ic_evernote_gray);
        }
    }

    @Override
    public void onClickCloudSync(View v) {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.cloud_sync_rotation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mCloudSyncImage.startAnimation(operatingAnim);
    }

    @Override
    public void onDrawerOpen() {
        try {
            mFragment.isMenuSelectModeAndChangeIt();
        } catch (Exception e) {
            //有时候mFragment会为空
        }
    }

    @Override
    public void onDrawerClose() {

    }

    @Override
    public void onBackPressed() {
        try {
            if (mFragment.ifRevealOpenAndCloseIt()) {//就在那个函数里面关闭了
            } else if (mFragment.isMenuSelectModeAndChangeIt()) {//就在那个函数里面换了模式了
            } else if (mFragment.isLayoutRevealOpen()) {//不做其他操作
            } else {
                if (System.currentTimeMillis() - mLastBackTime > 2000) {
                    Toast.makeText(HomeActivity.this, "再点击一次退出!", Toast.LENGTH_SHORT).show();
                    mLastBackTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            //有时候mFragment会为空
        }
    }

    @Override
    public void initUiAndListener() {

    }

    @Override
    public void startActivityAnimation() {

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
            //迫不得已的解决办法
            if (mCategoryLabel == null) {
                List<Category> categoryList = CategoryDBModel.getInstance().findAll();
                for (Category category : categoryList) {
                    if (category.isCheck()) {
                        mCategoryLabel = category.getLabel();
                    }
                }
            }

            //从另外个进程过来的数据
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_PROCESS, false)) {
                int number = PhotoNoteDBModel.getInstance().findByCategoryLabelByForce(mCategoryLabel, -1).size();
                Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
                category.setPhotosNumber(number);
                CategoryDBModel.getInstance().update(category);
                getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
            }

            //从Service中来
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_SERVICE, false)) {
                getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().refresh());
            }
        }
    };

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        unregisterReceiver(mUpdatePhotoNoteList);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Const.CATEGORY_LABEL, mCategoryLabel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCategoryLabel = savedInstanceState.getString(Const.CATEGORY_LABEL);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onUpdate(final int CRUD, String categoryLabel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (CRUD) {
                    case PhotoNoteChangedObserver.OBSERVER_PHOTONOTE_DELETE:
                    case PhotoNoteChangedObserver.OBSERVER_PHOTONOTE_CREATE:
                        int number = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, -1).size();
                        Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
                        if (category.getPhotosNumber() != number) {
                            category.setPhotosNumber(number);
                            CategoryDBModel.getInstance().update(category);
                            getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onUpdate(final int CRUD) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (CRUD) {
                    case CategoryChangedObserver.OBSERVER_CATEGORY_DELETE:
                        mListData = CategoryDBModel.getInstance().findAll();
                        String beforeLabel = mCategoryLabel;
                        for (Category category : mListData) {
                            if (category.isCheck()) {
                                mCategoryLabel = category.getLabel();
                                break;
                            }
                        }
                        getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
                        if (!mCategoryLabel.equals(beforeLabel)) {
                            mFragment.changePhotos4Category(mCategoryLabel);
                        }
                        break;
                    case CategoryChangedObserver.OBSERVER_CATEGORY_MOVE:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_CREATE:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_RENAME:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_SORT:
                        getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
                        break;
                }
            }
        });
    }
}
