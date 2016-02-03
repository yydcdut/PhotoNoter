package com.yydcdut.note.views.home.impl;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.IntroducePagerAdapter;
import com.yydcdut.note.presenters.home.impl.IntroducePresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.home.IIntroduceView;
import com.yydcdut.note.widget.CircleProgressBarLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yuyidong on 15/8/9.
 */
public class IntroduceActivity extends BaseActivity implements IIntroduceView {
    @Inject
    IntroducePresenterImpl mIntroducePresenter;

    @Bind({R.id.img_introduce_1, R.id.img_introduce_2, R.id.img_introduce_3, R.id.img_introduce_4,
            R.id.img_introduce_5, R.id.img_introduce_6})
    ImageView[] mImageViewArray;
    @Bind(R.id.btn_introduce_start)
    View mBtnStart;
    @Bind(R.id.layout_progress)
    CircleProgressBarLayout mCircleProgressBar;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        AppCompat.setFullWindow(getWindow());
        return R.layout.activity_introduce;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mIntroducePresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mIntroducePresenter.attachView(this);
        initViewPager();
        mCircleProgressBar = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_introduce);
        viewPager.setAdapter(new IntroducePagerAdapter(IntroduceActivity.this));
    }

    @OnPageChange(
            value = R.id.vp_introduce,
            callback = OnPageChange.Callback.PAGE_SELECTED
    )
    public void viewPagerSelected(int position) {
        switch (position) {
            case 0:
                resetDots(mImageViewArray[0]);
                break;
            case 1:
                resetDots(mImageViewArray[1]);
                break;
            case 2:
                resetDots(mImageViewArray[2]);
                break;
            case 3:
                resetDots(mImageViewArray[3]);
                break;
            case 4:
                resetDots(mImageViewArray[4]);
                break;
            case 5:
                resetDots(mImageViewArray[5]);
                showStartImage();
                break;
        }
    }

    private void showStartImage() {
        if (mBtnStart.getVisibility() == View.GONE) {
            AnimatorSet animation = new AnimatorSet();
            animation.setDuration(300);
            animation.playTogether(
                    ObjectAnimator.ofFloat(mBtnStart, "alpha", 0f, 1f)
            );
            animation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mBtnStart.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animation.start();
        }
    }

    private void resetDots(ImageView imageView) {
        for (ImageView imageView1 : mImageViewArray) {
            imageView1.setImageDrawable(getResources().getDrawable(R.drawable.img_introduce_dot));
        }
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.img_introduce_dot_foucs));
    }

    @OnClick(R.id.btn_introduce_start)
    public void click2FinishActivity(View v) {
        mIntroducePresenter.wannaFinish();
    }

    @Override
    public void showProgressBar() {
        mCircleProgressBar.show();
    }

    @Override
    public void hideProgressBar() {
        mCircleProgressBar.hide();
    }

    @Override
    public void jump2Album() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
