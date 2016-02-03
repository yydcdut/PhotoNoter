package com.yydcdut.note.views.setting.impl;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.setting.impl.FeedbackPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.setting.IFeedbackView;
import com.yydcdut.note.widget.CircleProgressBarLayout;
import com.yydcdut.note.widget.RevealView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 15/11/3.
 */
public class FeedbackActivity extends BaseActivity implements IFeedbackView {
    @Bind(R.id.et_feedback_content)
    EditText mContentText;
    @Bind(R.id.et_feedback_email)
    EditText mEmailText;
    @Bind(R.id.layout_progress)
    CircleProgressBarLayout mProgressLayout;
    @Bind(R.id.reveal_feedback)
    RevealView mRevealView;
    @Bind(R.id.img_feedback_ok)
    View mOkView;
    @Bind(R.id.fab_send)
    FloatingActionButton mFab;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    FeedbackPresenterImpl mFeedbackPresenter;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mFeedbackPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mFeedbackPresenter.bindData(getIntent().getIntExtra(
                mFeedbackPresenter.TYPE, mFeedbackPresenter.TYPE_FEEDBACK));
        initToolBarUI();
        mFeedbackPresenter.attachView(this);
        initProgressBar();
    }

    private void initToolBarUI() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        AppCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initProgressBar() {
        mProgressLayout.getCircleProgressBar().setCircleBackgroundEnabled(false);
        mProgressLayout.getCircleProgressBar().setColorSchemeColors(Color.WHITE);
    }

    @Override
    public String getEmail() {
        return mEmailText.getText().toString();
    }

    @Override
    public String getContent() {
        return mContentText.getText().toString();
    }

    @Override
    public void showFeedbackTitle() {
        mToolbar.setTitle(getResources().getString(R.string.feedback));
    }

    @Override
    public void showContactTitle() {
        mToolbar.setTitle(getResources().getString(R.string.about_contact));

    }

    @Override
    public void showLoading() {
        mProgressLayout.show();
    }

    @Override
    public void hideLoadingAndFinish() {
        mProgressLayout.hide();
        mOkView.setVisibility(View.VISIBLE);
        mOkView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale_small_2_big));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 600);
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.cl_feedback), message, Snackbar.LENGTH_SHORT).show();
    }

    private void startSendingAnimation(final RevealView.RevealAnimationListener listener) {
        final int width = mFab.getLeft();
        final int height = mFab.getTop();
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(Const.DURATION / 2);
        valueAnimator.setObjectValues(new PointF(0, 0));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            @Override
            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
                PointF point = new PointF();
                point.x = (width) * (1 - fraction / 2);
                point.y = (height) - 0.85f * (height) * (fraction / 2) * (fraction / 2);
                return point;
            }
        });
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                mFab.setX(point.x);
                mFab.setY(point.y);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRevealView.reveal((int) mFab.getX() + mFab.getWidth() / 2, (int) mFab.getY() + mFab.getHeight() / 2,
                        getThemeColor(), Const.RADIUS, Const.DURATION, listener);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.fab_send)
    public void clickSendFeedback(View v) {
        boolean wannaStartAnimation = mFeedbackPresenter.checkFeendback();
        if (wannaStartAnimation) {
            startSendingAnimation(new RevealView.RevealAnimationListener() {
                @Override
                public void finish() {
                    mFeedbackPresenter.sendFeedback(mEmailText.getText().toString(), mContentText.getText().toString());
                }
            });
        }
    }

}
