package com.yydcdut.note.views.gallery.impl;

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
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.PhotoDetailPagerAdapter;
import com.yydcdut.note.presenters.gallery.IPhotoDetailPresenter;
import com.yydcdut.note.presenters.gallery.impl.PhotoDetailPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.gallery.IPhotoDetailView;
import com.yydcdut.note.widget.FixViewPager;
import com.yydcdut.note.widget.PhotoCheckBox;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yuyidong on 16/4/4.
 */
public class PhotoDetailActivity extends BaseActivity implements IPhotoDetailView,
        PhotoDetailPagerAdapter.OnPhotoClickListener, PhotoCheckBox.OnPhotoCheckedChangeListener {
    @Inject
    PhotoDetailPresenterImpl mPhotoDetailPresenter;

    @Bind(R.id.vp_detail)
    FixViewPager mViewPager;

    @Bind(R.id.view_status_cover)
    View mStatusCoverView;

    @Bind(R.id.appbar)
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
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        if (AppCompat.AFTER_LOLLIPOP) {
            AppCompat.setFullWindow(getWindow());
        }
        return R.layout.activity_photo_detail;
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
        mPhotoDetailPresenter.bindData(getIntent().getBooleanExtra(INTENT_PREVIEW_SELECTED, false),
                getIntent().getIntExtra(INTENT_PAGE, 0), getIntent().getStringExtra(INTENT_FOLDER));
        mPhotoDetailPresenter.attachView(this);
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
        mPhotoDetailPresenter.initViewPager();
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

    @Override
    public void setAdapter(List<String> adapterPathList, int initPage) {
        mAdapterPathList = adapterPathList;
        PhotoDetailPagerAdapter photoDetailPagerAdapter = new PhotoDetailPagerAdapter(mAdapterPathList);
        mViewPager.setAdapter(photoDetailPagerAdapter);
        mViewPager.setCurrentItem(initPage);
        photoDetailPagerAdapter.setOnPhotoClickListener(this);
    }

    @Override
    public void initAdapterData(boolean isPreviewSelected, List<String> selectedPathList) {
        if (isPreviewSelected) {
            mPhotoCheckBox.setCheckedWithoutCallback(true);
        } else {
            for (int i = 0; i < selectedPathList.size(); i++) {
                String selectedPath = selectedPathList.get(i);
                if (selectedPath.equals(mAdapterPathList.get(mViewPager.getCurrentItem()))) {
                    mPhotoCheckBox.setCheckedWithoutCallback(true);
                    break;
                }
            }
        }
    }

    @Override
    public void setCheckBoxSelectedWithoutCallback(boolean selected) {
        mPhotoCheckBox.setCheckedWithoutCallback(selected);
    }

    @OnPageChange(value = R.id.vp_detail, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onViewPageSelected(int position) {
        mPhotoDetailPresenter.onPagerChanged(position);
    }

    @Override
    public void setToolbarTitle(String content) {
        mToolbar.setTitle((mViewPager.getCurrentItem() + 1) + "/" + mViewPager.getAdapter().getCount());
    }

    @Override
    public void setMenuTitle(String content) {
        mFinishMenuItem.setTitle(content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_photo, menu);
        mFinishMenuItem = menu.findItem(R.id.action_finish);
        mPhotoDetailPresenter.initMenu();
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

    @Override
    public void onPhotoCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPhotoDetailPresenter.onChecked(isChecked);
    }

    @Override
    public void onPhotoClick(View view) {
        mPhotoDetailPresenter.click2doAnimation();
    }

    @OnClick(R.id.txt_detail)
    public void onChooseTextClick(View view) {
        mPhotoCheckBox.setChecked(!mPhotoCheckBox.isChecked());
    }

    @Override
    public void hideWidget(final IPhotoDetailPresenter.OnAnimationAdapter onAnimationAdapter) {
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
                if (onAnimationAdapter != null) {
                    onAnimationAdapter.onAnimationStarted(IPhotoDetailPresenter.STATE_HIDE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onAnimationAdapter != null) {
                    onAnimationAdapter.onAnimationEnded(IPhotoDetailPresenter.STATE_HIDE);
                }
            }
        });
        animation.start();
    }


    @Override
    public void showWidget(final IPhotoDetailPresenter.OnAnimationAdapter onAnimationAdapter) {
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
                if (onAnimationAdapter != null) {
                    onAnimationAdapter.onAnimationStarted(IPhotoDetailPresenter.STATE_SHOW);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onAnimationAdapter != null) {
                    onAnimationAdapter.onAnimationEnded(IPhotoDetailPresenter.STATE_SHOW);
                }
            }
        });
        animation.start();
    }

    @Override
    public void showStatusBarTime() {
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    public void hideStatusBarTime() {
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

}

