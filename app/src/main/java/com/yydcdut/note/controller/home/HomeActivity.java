package com.yydcdut.note.controller.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.controller.login.BindUserActivity;
import com.yydcdut.note.controller.login.LoginActivity;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.compare.ComparatorFactory;
import com.yydcdut.note.view.RoundedImageView;

import java.io.File;
import java.util.List;


/**
 * Created by yuyidong on 15-3-23.
 */
public class HomeActivity extends NavigationActivity implements NavigationActivity.NavigationLiveoListener, NavigationActivity.OnDrawerListener {
    /**
     * 数据
     */
    List<Category> mListData;
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
    public int setContentView() {
        initReceiver();
        return super.setContentView();
    }

    @Override
    public void onUserInformation() {
        mUserBackground.setImageDrawable(getResources().getDrawable(R.drawable.bg_user_background));
        int number = UserCenter.getInstance().existUserNumber();
        if (number == 0) {
            user1Manager(null);
            user2Manager(null, false);
        } else if (number == 1) {
            String userType = UserCenter.getInstance().getFirstUserType();
            IUser iUser = UserCenter.getInstance().userFactory(userType);
            user1Manager(iUser);
            user2Manager(null, true);
        } else {
            String userType = UserCenter.getInstance().getFirstUserType();
            IUser iUser = UserCenter.getInstance().userFactory(userType);
            user1Manager(iUser);
            String userType2 = UserCenter.getInstance().getAnotherUser();
            IUser iUser2 = UserCenter.getInstance().userFactory(userType2);
            user2Manager(iUser2, true);
        }

    }

    /**
     * 第一个帐号的显示逻辑
     *
     * @param user
     */
    private void user1Manager(IUser user) {
        mUserPhoto.setVisibility(View.VISIBLE);
        if (user != null) {
            mUserName.setVisibility(View.VISIBLE);
            mUserName.setText(user.getName());
            loadImage(user, mUserPhoto);
        } else {
            mUserPhoto.setImageResource(R.drawable.ic_no_user);
            mUserName.setVisibility(View.GONE);
        }
    }

    /**
     * 第二个帐号的显示逻辑
     *
     * @param user
     * @param user1Exist
     */
    private void user2Manager(IUser user, boolean user1Exist) {
        if (user != null) {
            mUserPhotoTwo.setVisibility(View.VISIBLE);
            loadImage(user, mUserPhotoTwo);
        } else if (user1Exist) {
            mUserPhotoTwo.setVisibility(View.VISIBLE);
            mUserPhotoTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_user));
        } else {
            mUserPhotoTwo.setVisibility(View.GONE);
        }
    }

    private void loadImage(IUser iUser, RoundedImageView view) {
        if (iUser.getType().equals(UserCenter.USER_TYPE_QQ)) {
            if (new File(FilePathUtils.getQQImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), view);
            } else {
                ImageLoaderManager.displayImage(iUser.getNetImagePath(), view);
            }
        } else {
            if (new File(FilePathUtils.getSinaImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getSinaImagePath(), view);
            } else {
                ImageLoaderManager.displayImage(iUser.getNetImagePath(), view);
            }
        }
    }

    @Override
    public void onUserAccounts() {
    }

    @Override
    public void onCloudInformation() {

    }

    @Override
    public void onCreateInit(Bundle savedInstanceState) {
        this.setNavigationListener(this);
        this.setOnDrawerListener(this);
        //First item of the position selected from the list
        this.setDefaultStartPositionNavigation(0);
    }

    @Override
    public List<Category> setCategoryAdapter() {
        mListData = CategoryDBModel.getInstance().findAll();
        return mListData;
    }


    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {
        CategoryDBModel.getInstance().setCategoryMenuPosition(mListData.get(position));
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
        CategoryDBModel.getInstance().setCategoryMenuPosition(category);
        mListData = CategoryDBModel.getInstance().findAll();
        getCategoryAdapter().resetGroup(mListData);
        getCategoryAdapter().notifyDataSetChanged();
        setCurrentPosition(mListData.size() - 1);
        setCheckedItemNavigation(mListData.size() - 1, true);
        mCategoryLabel = category.getLabel();
        mFragment.changePhotos4Category(mCategoryLabel);
    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {
    }

    @Override
    public void onClickUserPhotoNavigation(View v, final int which) {
        switch (which) {
            case USER_ONE:
                if (UserCenter.getInstance().existUserNumber() > 0) {
                    startActivityForResult(new Intent(HomeActivity.this, BindUserActivity.class), REQUEST_NOTHING);
                } else {
                    startActivityForResult(new Intent(HomeActivity.this, LoginActivity.class), REQUEST_NOTHING);
                }
                break;
            case USER_TWO:
                changeUserPhoto2to1();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_DATA) {
            updateUserInfo();
        }

    }

    private void updateUserInfo() {
        onUserInformation();
        openDrawer();
    }

    @Override
    public void onClickCloudSync(View v) {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.cloud_sync_rotation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mCloudSyncImage.startAnimation(operatingAnim);
    }

    /**
     * 第二个头像余第一个头像切换
     */
    private void changeUserPhoto2to1() {
        if (mUserPhotoTwo.getVisibility() != View.VISIBLE) {
            return;
        }
        startOutAnimation(mUserPhoto);
        startOutAnimation(mUserPhotoTwo);
        Drawable drawable1 = mUserPhotoTwo.getDrawable();
        mUserPhotoTwo.setImageDrawable(mUserPhoto.getDrawable());
        mUserPhoto.setImageDrawable(drawable1);
        startInAnimation(mUserPhoto);
        startInAnimation(mUserPhotoTwo);
    }

    /**
     * 淡入动画
     *
     * @param view 哪个view做动画
     */
    private void startInAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(1000);
        animation.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        );
        animation.start();
    }

    /**
     * 淡出动画
     *
     * @param view 哪个view做动画
     */
    private void startOutAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(1000);
        animation.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        );
        animation.start();
    }

    @Override
    public void onOpen() {
        try {
            mFragment.isMenuSelectModeAndChangeIt();
        } catch (Exception e) {
            //有时候mFragment会为空
        }
    }

    @Override
    public void onClose() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mFragment.ifRevealOpenAndCloseIt()) {//就在那个函数里面关闭了
                    } else if (mFragment.isMenuSelectModeAndChangeIt()) {//就在那个函数里面换了模式了
                    } else if (mFragment.isLayoutRevealOpen()) {//不做其他操作
                    } else {
                        if (System.currentTimeMillis() - mLastBackTime > 2000) {
                            Toast.makeText(HomeActivity.this, "再点击一次退出!", Toast.LENGTH_SHORT).show();
                            mLastBackTime = System.currentTimeMillis();
                        } else {
                            return super.onKeyDown(keyCode, event);
                        }
                    }
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        } catch (Exception e) {
            //有时候mFragment会为空
        }
        return super.onKeyDown(keyCode, event);
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

            //有被删除的，这个时候更新一下menu上的list 一般来源于EditCategoryActivity
            //同时Rename也用的这个
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_CATEGORY_DELETE, false)) {
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
            }

            //移动，移动之后肯定会更新数目 一般来源于AlbumFragment的menu
            if (null != intent.getStringExtra(Const.TARGET_BROADCAST_CATEGORY_MOVE)) {
                //更新移动过去的category数目
                String targetCategoryLabel = intent.getStringExtra(Const.TARGET_BROADCAST_CATEGORY_MOVE);
                int targetNumber = PhotoNoteDBModel.getInstance().findByCategoryLabel(targetCategoryLabel, ComparatorFactory.FACTORY_NOT_SORT).size();
                Category targetCategory = CategoryDBModel.getInstance().findByCategoryLabel(targetCategoryLabel);
                targetCategory.setPhotosNumber(targetNumber);
                CategoryDBModel.getInstance().update(targetCategory);
                //更新原来的category数目
                int number = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, ComparatorFactory.FACTORY_NOT_SORT).size();
                Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
                if (category.getPhotosNumber() == number) {
                    return;
                } else {
                    category.setPhotosNumber(number);
                    CategoryDBModel.getInstance().update(category);
                    getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
                }
            }

            //更新顺序 一般来源于EditCategoryActivity
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_CATEGORY_SORT, false)) {
                mListData = CategoryDBModel.getInstance().refresh();
                getCategoryAdapter().resetGroup(mListData);
            }

            //更新数目
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_CATEGORY_NUMBER, false)) {
                //更新数目
                int number = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, -1).size();
                Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
                if (category.getPhotosNumber() == number) {
                    return;
                } else {
                    category.setPhotosNumber(number);
                    CategoryDBModel.getInstance().update(category);
                    getCategoryAdapter().resetGroup(CategoryDBModel.getInstance().findAll());
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

}
