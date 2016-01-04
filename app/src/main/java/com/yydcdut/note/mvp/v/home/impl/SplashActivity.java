package com.yydcdut.note.mvp.v.home.impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.component.DaggerActivityComponent;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.mvp.p.home.impl.SplashPresenterImpl;
import com.yydcdut.note.mvp.v.BaseActivity;
import com.yydcdut.note.mvp.v.home.ISplashView;
import com.yydcdut.note.utils.AppCompat;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/7/16.
 */
public class SplashActivity extends BaseActivity implements ISplashView {
    @Inject
    SplashPresenterImpl mSplashPresenter;
    private AnimatorSet mAnimatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build();
        mActivityComponent.inject(this);
        mSplashPresenter.attachView(this);
        mSplashPresenter.isWannaCloseSplash();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        AppCompat.setFullWindow(getWindow());
        return R.layout.activity_splash;
    }

    @Override
    public void initInjector() {
        mIPresenter = mSplashPresenter;
    }

    @Override
    public void initUiAndListener() {
        View logoView = findViewById(R.id.layout_splash);
        View backgroundView = findViewById(R.id.img_splash_bg);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(2000);
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(logoView, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(logoView, "translationY", 300, 0),
                ObjectAnimator.ofFloat(backgroundView, "scaleX", 1.3f, 1.05f),
                ObjectAnimator.ofFloat(backgroundView, "scaleY", 1.3f, 1.05f)
        );
        mAnimatorSet.start();
        mSplashPresenter.doingSplash();
    }

    @Override
    public void jump2Introduce() {
        Intent intent = new Intent(SplashActivity.this, IntroduceActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    public void jump2Album() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    public boolean isAnimationRunning() {
        return mAnimatorSet.isRunning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSplashPresenter.onActivityPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSplashPresenter.onActivityStart();
    }
}
