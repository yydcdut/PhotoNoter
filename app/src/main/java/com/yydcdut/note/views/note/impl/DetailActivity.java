package com.yydcdut.note.views.note.impl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.DetailPagerAdapter;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.presenters.note.impl.DetailPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.note.IDetailView;
import com.yydcdut.note.widget.FontTextView;
import com.yydcdut.note.widget.RevealView;
import com.yydcdut.note.widget.fab.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yuyidong on 16/1/8.
 */
public class DetailActivity extends BaseActivity implements IDetailView,
        ViewPager.PageTransformer, PopupMenu.OnMenuItemClickListener {
    private static final float MIN_SCALE = 0.75f;

    @Inject
    DetailPresenterImpl mDetailPresenter;

    @Bind(R.id.vp_detail)
    ViewPager mViewPager;
    @Bind(R.id.view_overlay)
    View mOverlayView;
    @Bind(R.id.txt_detail_content_title)
    FontTextView mTitleView;/* Content TextView */
    @Bind(R.id.txt_detail_content)
    FontTextView mContentView;
    @Bind(R.id.txt_detail_create_time)
    TextView mCreateView;
    @Bind(R.id.txt_detail_edit_time)
    TextView mEditView;
    @Bind(R.id.img_menu)
    View mMenuView;
    @Bind(R.id.layout_menu)
    View mMenuLayout;
    @Bind(R.id.card_detail)
    View mCardView;
    @Bind(R.id.txt_label_title)
    View mTitleLabelView;
    @Bind(R.id.txt_label_content)
    TextView mContentLabelView;
    @Bind(R.id.txt_label_date)
    View mDateLabelView;
    @Bind(R.id.layout_detail_time)
    View mDateLayoutView;
    @Bind(R.id.fab_edit)
    FloatingActionButton mFab;
    @Bind(R.id.reveal)
    RevealView mRevealView;

    private DetailPagerAdapter mDetailPagerAdapter;
    private int mTranslateHeight = 0;
    private boolean mIsIgnoreBackPress = false;
    private boolean mIsIgnoreClick = false;
    private Handler mAnimationHandler;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        if (AppCompat.AFTER_LOLLIPOP) {
            AppCompat.setFullWindow(getWindow());
        }
        return R.layout.activity_detail;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mDetailPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        calculateFabPosition();
        Bundle bundle = getIntent().getExtras();
        mDetailPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES), bundle.getInt(Const.PHOTO_POSITION),
                bundle.getInt(Const.COMPARATOR_FACTORY));
        mDetailPresenter.attachView(this);
        initToolBar();
        initListener();
        mAnimationHandler = new Handler();
    }

    private void calculateFabPosition() {
        if (AppCompat.AFTER_LOLLIPOP && AppCompat.hasNavigationBar(this)) {
            int height = AppCompat.getNavigationBarHeight(this);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            layoutParams.bottomMargin = height;
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (AppCompat.AFTER_LOLLIPOP) {
            int size = getStatusBarSize();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.setMargins(0, size, 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppCompat.AFTER_LOLLIPOP && AppCompat.hasNavigationBar(this)) {
            getMenuInflater().inflate(R.menu.menu_detail_toolbar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_note:
                mDetailPresenter.doCardViewAnimation();
                break;
        }
        return true;
    }

    private void initListener() {
        mViewPager.setPageTransformer(true, this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int menuLayoutHeight = mMenuLayout.getHeight();
        int cardViewTop = mCardView.getTop();
        if (AppCompat.AFTER_LOLLIPOP) {
            mTranslateHeight = Utils.sScreenHeight - cardViewTop - menuLayoutHeight;
        } else {
            mTranslateHeight = Utils.sScreenHeight - getStatusBarSize() - cardViewTop - menuLayoutHeight;
        }
    }

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when
            // moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1 - position);
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                    * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    @Override
    public void setFontSystem(boolean useSystem) {
        mTitleView.setFontSystem(useSystem);
        mContentView.setFontSystem(useSystem);
    }

    @Override
    public void setViewPagerAdapter(List<PhotoNote> list, int position, int comparator) {
        mDetailPagerAdapter = new DetailPagerAdapter(list, getFragmentManager(), comparator);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void showNote(String title, String content, String createdTime, String editedTime) {
        mTitleLabelView.setVisibility(View.VISIBLE);
        mTitleView.setVisibility(View.VISIBLE);
        mContentLabelView.setText(getResources().getString(R.string.text_content));
        mDateLabelView.setVisibility(View.VISIBLE);
        mDateLayoutView.setVisibility(View.VISIBLE);
        mTitleView.setText(title);
        mContentView.setText(content);
        mCreateView.setText(createdTime);
        mEditView.setText(editedTime);
    }

    @Override
    public void showExif(String exif) {
        mTitleLabelView.setVisibility(View.GONE);
        mTitleView.setVisibility(View.GONE);
        mDateLabelView.setVisibility(View.GONE);
        mDateLayoutView.setVisibility(View.GONE);
        mContentLabelView.setText(getResources().getString(R.string.detail_exif));
        mContentView.setText(exif);
    }

    @Override
    public void initAnimationView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(mCardView, "translationY", 0, mTranslateHeight)
                );
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mOverlayView.setVisibility(View.GONE);
                        mIsIgnoreClick = true;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mIsIgnoreClick = true;
                    }
                });
                animatorSet.setDuration(400);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                mAnimationHandler.postDelayed(mDownDelayRunnable, 350);
            }
        }, 500);
    }

    @Override
    public void jump2EditTextActivity(int categoryId, int position, int comparator) {
        Intent intent = new Intent(this, EditTextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.CATEGORY_ID_4_PHOTNOTES, categoryId);
        bundle.putInt(Const.PHOTO_POSITION, position);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_NOTHING);
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
    }

    @Override
    public void jump2MapActivity(int categoryId, int position, int comparator) {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.CATEGORY_ID_4_PHOTNOTES, categoryId);
        bundle.putInt(Const.PHOTO_POSITION, position);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_NOTHING);
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
    }

    @OnPageChange(
            value = R.id.vp_detail,
            callback = OnPageChange.Callback.PAGE_SELECTED
    )
    public void viewPagerSelected(int page) {
        mDetailPresenter.showNote(page);
    }

    @OnClick(R.id.img_menu)
    public void onMenuItemClick(View view) {
        mDetailPresenter.showMenuIfNotHidden();
    }

    @OnClick(R.id.layout_menu)
    public void onMenuLayoutClick(View view) {
        mDetailPresenter.doCardViewAnimation();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exif:
                mDetailPresenter.showExif(mViewPager.getCurrentItem());
                break;
            case R.id.menu_info:
                mDetailPresenter.showNote(mViewPager.getCurrentItem());
                break;
        }
        return false;
    }

    @Override
    public void upAnimation() {
        View adapterPositionView = mDetailPagerAdapter.getItemView(mViewPager.getCurrentItem());
        View blurView;
        if (adapterPositionView != null) {
            blurView = adapterPositionView.findViewById(R.id.img_blur);
        } else {
            blurView = mOverlayView;
        }
        final View finalBlurView = blurView;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mCardView, "translationY", mTranslateHeight, 0),
                ObjectAnimator.ofFloat(mViewPager, "scaleX", 1f, 1.1f),
                ObjectAnimator.ofFloat(mViewPager, "scaleY", 1f, 1.1f),
                ObjectAnimator.ofFloat(finalBlurView, "alpha", 0f, 1f)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                finalBlurView.setVisibility(View.VISIBLE);
                mOverlayView.setVisibility(View.VISIBLE);
                mIsIgnoreClick = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsIgnoreClick = false;
            }
        });
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
        mAnimationHandler.postDelayed(mUpDelayRunnable, 500);

    }

    private Runnable mUpDelayRunnable = new Runnable() {
        @Override
        public void run() {
            Animator animator = ObjectAnimator.ofFloat(mFab, "translationY", mTranslateHeight, 0);
            animator.setDuration(350);
            animator.setInterpolator(new OvershootInterpolator());
            animator.start();
        }
    };

    @Override
    public void downAnimation() {
        View adapterPositionView = mDetailPagerAdapter.getItemView(mViewPager.getCurrentItem());
        View blurView;
        if (adapterPositionView != null) {
            blurView = adapterPositionView.findViewById(R.id.img_blur);
        } else {
            blurView = mOverlayView;
        }
        final View finalBlurView = blurView;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mCardView, "translationY", 0, mTranslateHeight),
                ObjectAnimator.ofFloat(mViewPager, "scaleX", 1.1f, 1.0f),
                ObjectAnimator.ofFloat(mViewPager, "scaleY", 1.1f, 1.0f),
                ObjectAnimator.ofFloat(finalBlurView, "alpha", 1f, 0f)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finalBlurView.setVisibility(View.GONE);
                mOverlayView.setVisibility(View.GONE);
                mIsIgnoreClick = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsIgnoreClick = true;
            }
        });
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
        mAnimationHandler.postDelayed(mDownDelayRunnable, 350);
    }

    private Runnable mDownDelayRunnable = new Runnable() {
        @Override
        public void run() {
            Animator animator = ObjectAnimator.ofFloat(mFab, "translationY", 0, mTranslateHeight);
            animator.setDuration(400);
            animator.start();
        }
    };

    @Override
    public void showPopupMenu() {
        PopupMenu popup = new PopupMenu(this, mMenuView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_detail, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public void showFabIcon(@DrawableRes int iconRes) {
        mFab.setIcon(iconRes);
    }

    @OnClick(R.id.view_overlay)
    public void clickOverlayView() {
        if (!mIsIgnoreClick) {
            mDetailPresenter.doCardViewAnimation();
        }
    }

    @OnClick(R.id.fab_edit)
    public void clickFabEdit(View v) {
        showRevealColorViewAndStartActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_DATA) {
            Bundle bundle = data.getExtras();
            mDetailPresenter.updateNote(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES),
                    bundle.getInt(Const.PHOTO_POSITION), bundle.getInt(Const.COMPARATOR_FACTORY));
        }
        closeRevealColorView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开RevealColorView并且开启activity
     */
    private void showRevealColorViewAndStartActivity() {
        if (mTitleLabelView.getVisibility() != View.VISIBLE) {
            showSnackBar(getString(R.string.function_offoline));
            return;
        }
        mIsIgnoreBackPress = true;
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.reveal(p.x, p.y, getThemeColor(), mFab.getHeight() / 2, Const.DURATION, new RevealView.RevealAnimationListener() {

            @Override
            public void finish() {
                if (mTitleLabelView.getVisibility() != View.VISIBLE) {
//                    mDetailPresenter.jump2MapActivity();
                } else {
                    mDetailPresenter.jump2EditTextActivity();
                }
                mIsIgnoreBackPress = false;
            }
        });
    }

    /**
     * 关闭activity之后的动画或者onActivityResult
     */
    public void closeRevealColorView() {
        mIsIgnoreBackPress = true;
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.hide(p.x, p.y, Color.TRANSPARENT, Const.RADIUS, Const.DURATION, new RevealView.RevealAnimationListener() {
            @Override
            public void finish() {
                mIsIgnoreBackPress = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mIsIgnoreBackPress) {
            super.onBackPressed();
        }
    }

    @Override
    public void showSnackBar(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
