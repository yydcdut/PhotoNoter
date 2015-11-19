package com.yydcdut.note.mvp.v.home.impl;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.IntroducePagerAdapter;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.mvp.p.home.IIntroducePresenter;
import com.yydcdut.note.mvp.p.home.impl.IntroducePresenterImpl;
import com.yydcdut.note.mvp.v.home.IIntroduceView;
import com.yydcdut.note.service.InitService;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by yuyidong on 15/8/9.
 */
public class IntroduceActivity extends BaseActivity implements IIntroduceView, OnPageChangeListener, View.OnClickListener {
    private IIntroducePresenter mIntroducePresenter;

    private ServiceConnection mServiceConnection;

    @InjectViews({R.id.img_introduce_1, R.id.img_introduce_2, R.id.img_introduce_3, R.id.img_introduce_4,
            R.id.img_introduce_5, R.id.img_introduce_6})
    ImageView[] mImageViewArray;

    @InjectView(R.id.btn_introduce_start)
    View mBtnStart;

    @InjectView(R.id.layout_progress)
    CircleProgressBarLayout mCircleProgressBar;


    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        LollipopCompat.setFullWindow(getWindow());
        return R.layout.activity_introduce;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.inject(this);
        mIntroducePresenter = new IntroducePresenterImpl();
        mIntroducePresenter.attachView(this);
        initViewPager();
        mBtnStart.setOnClickListener(this);
        mCircleProgressBar = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_introduce);
        viewPager.setAdapter(new IntroducePagerAdapter(IntroduceActivity.this));
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
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

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        mIntroducePresenter.wannaFinish();
    }

    @Override
    public void bindServiceConnection(ServiceConnection serviceConnect) {
        Intent initIntent = new Intent(this, InitService.class);
        mServiceConnection = serviceConnect;
        bindService(initIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unbindServiceConnection() {
        unbindService(mServiceConnection);
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
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}
