package com.yydcdut.noteplugin.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.PhotoDetailPagerAdapter;
import com.yydcdut.noteplugin.bean.MediaPhoto;
import com.yydcdut.noteplugin.model.PhotoModel;
import com.yydcdut.noteplugin.model.SelectPhotoModel;
import com.yydcdut.noteplugin.utils.AppCompat;
import com.yydcdut.noteplugin.view.FixViewPager;
import com.yydcdut.noteplugin.view.PhotoCheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yuyidong on 16/3/21.
 */
public class PhotoDetailActivity extends BaseActivity implements PhotoDetailPagerAdapter.OnPhotoClickListener,
        PhotoCheckBox.OnPhotoCheckedChangeListener {
    /* 当前的widget是否在显示 */
    private boolean isWidgetShowed = true;
    /* 当前动画是否在进行 */
    private boolean isAnimationDoing = false;
    /* 是不是浏览选中照片的模式 */
    private boolean isPreviewSelected = false;

    @Bind(R.id.vp_detail)
    FixViewPager mViewPager;

    @Bind(R.id.view_status_cover)
    View mStatusCoverView;

    @Bind(R.id.layout_bar)
    View mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.layout_detail_bottom)
    View mBottomLayout;

    @Bind(R.id.cb_detail)
    PhotoCheckBox mPhotoCheckBox;

    private MenuItem mFinishMenuItem;

    private List<String> mAdapterPathList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        if (AppCompat.AFTER_LOLLIPOP) {
            AppCompat.setFullWindow(getWindow());
        }
        ButterKnife.bind(this);
        isPreviewSelected = getIntent().getBooleanExtra(INTENT_PREVIEW_SELECTED, false);
        initToolBarUI();
        initViewPager();
        mPhotoCheckBox.setOnPhotoCheckedChangeListener(this);
    }

    private void initToolBarUI() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
        if (AppCompat.AFTER_LOLLIPOP) {
            int size = getStatusBarSize();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAppBarLayout.getLayoutParams();
            layoutParams.setMargins(0, size, 0, 0);
        } else {
            mStatusCoverView.setVisibility(View.GONE);
        }
    }

    private void initViewPager() {
        PhotoDetailPagerAdapter photoDetailPagerAdapter;
        if (isPreviewSelected) {
            mAdapterPathList = new ArrayList<>(SelectPhotoModel.getInstance().getCount());
            for (int i = 0; i < SelectPhotoModel.getInstance().getCount(); i++) {
                mAdapterPathList.add(SelectPhotoModel.getInstance().get(i));
            }
            photoDetailPagerAdapter = new PhotoDetailPagerAdapter(mAdapterPathList);
            mViewPager.setAdapter(photoDetailPagerAdapter);
            mPhotoCheckBox.setCheckedWithoutCallback(true);
        } else {
            int initPage = getIntent().getIntExtra(INTENT_PAGE, 0);
            String folderName = getIntent().getStringExtra(INTENT_FOLDER);
            List<MediaPhoto> mediaPhotoList = PhotoModel.getInstance().findByMedia(this).get(folderName).getMediaPhotoList();
            mAdapterPathList = new ArrayList<>(mediaPhotoList.size());
            for (MediaPhoto mediaPhoto : mediaPhotoList) {
                mAdapterPathList.add(mediaPhoto.getPath());
            }
            photoDetailPagerAdapter = new PhotoDetailPagerAdapter(mAdapterPathList);
            mViewPager.setAdapter(photoDetailPagerAdapter);
            mViewPager.setCurrentItem(initPage);
            for (int i = 0; i < SelectPhotoModel.getInstance().getCount(); i++) {
                String selectedPath = SelectPhotoModel.getInstance().get(i);
                if (selectedPath.equals(mAdapterPathList.get(initPage))) {
                    mPhotoCheckBox.setCheckedWithoutCallback(true);
                    break;
                }
            }
        }
        photoDetailPagerAdapter.setOnPhotoClickListener(this);
    }

    @OnPageChange(value = R.id.vp_detail, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onViewPageSelected(int position) {
        String path = mAdapterPathList.get(position);
        if (SelectPhotoModel.getInstance().contains(path)) {
            mPhotoCheckBox.setCheckedWithoutCallback(true);
        } else {
            mPhotoCheckBox.setCheckedWithoutCallback(false);
        }
        mToolbar.setTitle((mViewPager.getCurrentItem() + 1) + "/" + mViewPager.getAdapter().getCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_photo, menu);
        mFinishMenuItem = menu.findItem(R.id.action_finish);
        updateFinishMenuNumber(SelectPhotoModel.getInstance().getCount());
        mToolbar.setTitle((mViewPager.getCurrentItem() + 1) + "/" + mViewPager.getAdapter().getCount());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(BaseActivity.CODE_RESULT_CHANGED);
                finish();
                break;
            case R.id.action_finish:
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        setResult(BaseActivity.CODE_RESULT_CHANGED);
        finish();
    }

    public void updateFinishMenuNumber(int number) {
        if (number == 0) {
            mFinishMenuItem.setTitle(getResources().getString(R.string.action_finish));
        } else {
            mFinishMenuItem.setTitle(getResources().getString(R.string.action_finish) + "(" + number + ")");
        }
    }

    @Override
    public void onPhotoClick(View view) {
        if (isAnimationDoing) {
            return;
        }
        if (isWidgetShowed) {
            hideWidget();
        } else {
            showWidget();
        }
    }

    private void hideWidget() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(1000);
        animation.playTogether(
                ObjectAnimator.ofFloat(mAppBarLayout, "Y", AppCompat.AFTER_LOLLIPOP ? getStatusBarSize() : 0,
                        -getActionBarSize() - (AppCompat.AFTER_LOLLIPOP ? getStatusBarSize() : 0)),
                ObjectAnimator.ofFloat(mBottomLayout, "Y", mBottomLayout.getTop(), mBottomLayout.getTop() + getActionBarSize()),
                ObjectAnimator.ofFloat(mStatusCoverView, "Y", 0f, -getActionBarSize())
        );
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationDoing = true;
                if (AppCompat.AFTER_LOLLIPOP) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationDoing = false;
                isWidgetShowed = false;
            }
        });
        animation.start();
    }

    private void showWidget() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(1000);
        animation.playTogether(
                ObjectAnimator.ofFloat(mAppBarLayout, "Y", -getActionBarSize() - (AppCompat.AFTER_LOLLIPOP ? getStatusBarSize() : 0),
                        AppCompat.AFTER_LOLLIPOP ? getStatusBarSize() : 0),
                ObjectAnimator.ofFloat(mBottomLayout, "Y", mBottomLayout.getTop() + getActionBarSize(), mBottomLayout.getTop()),
                ObjectAnimator.ofFloat(mStatusCoverView, "Y", -getActionBarSize(), 0f)
        );
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationDoing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationDoing = false;
                isWidgetShowed = true;
                if (AppCompat.AFTER_LOLLIPOP) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                }
            }
        });
        animation.start();
    }

    @OnClick(R.id.txt_detail)
    public void onChooseTextClick(View view) {
        mPhotoCheckBox.setChecked(!mPhotoCheckBox.isChecked());
    }

    @Override
    public void onPhotoCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String path = mAdapterPathList.get(mViewPager.getCurrentItem());
        if (isChecked && !SelectPhotoModel.getInstance().contains(path)) {
            SelectPhotoModel.getInstance().addPath(path);
        } else if (!isChecked) {
            SelectPhotoModel.getInstance().removePath(path);
        }
        updateFinishMenuNumber(SelectPhotoModel.getInstance().getCount());
    }
}
