package com.yydcdut.note.controller.setting;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.RevealView;

/**
 * Created by yuyidong on 15/11/3.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener,
        Handler.Callback {
    private EditText mEditText;
    private Handler mHandler;
    private CircleProgressBarLayout mProgressLayout;
    private RevealView mRevealView;
    private FloatingActionButton mFab;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_feedback;
    }


    @Override
    public void initUiAndListener() {
        initToolBarUI();
        initView();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.feedback));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initView() {

        mEditText = (EditText) findViewById(R.id.et_feedback);
        mRevealView = (RevealView) findViewById(R.id.reveal_feedback);
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
        mFab = (FloatingActionButton) findViewById(R.id.fab_send);
        mFab.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_send:
                if (TextUtils.isEmpty(mEditText.getText().toString())) {
                    Snackbar.make(findViewById(R.id.cl_feedback), getResources().getString(R.string.toast_input_error),
                            Snackbar.LENGTH_SHORT).show();
                    break;
                }
                if (mHandler == null) {
                    mHandler = new Handler(this);
                }
                mProgressLayout.show();
                parabolaAnimation(mFab);
                NoteApplication.getInstance().getExecutorPool().submit(new Runnable() {
                    @Override
                    public void run() {
//                        FeedbackModel.getInstance().sendFeedback(System.currentTimeMillis() + "",
//                                mEditText.getText().toString());
                        mHandler.sendEmptyMessage(0);
                    }
                });
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        mProgressLayout.hide();
//        Snackbar.make(findViewById(R.id.cl_feedback), getResources().getString(R.string.toast_success),
//                Snackbar.LENGTH_SHORT).show();
        return false;
    }

    private void parabolaAnimation(final View view) {
        final int wdith = view.getLeft();
        final int height = view.getTop();
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(Const.DURATION / 2);
        valueAnimator.setObjectValues(new PointF(0, 0));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            // fraction = t / duration
            @Override
            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
                PointF point = new PointF();
                float wdithf = (wdith) * (1 - fraction / 2);
                float heightf = (height) - 0.85f * (height) * (fraction / 2) * (fraction / 2);
                point.x = wdithf;
                point.y = heightf;
                return point;
            }
        });

        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                view.setX(point.x);
                view.setY(point.y);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRevealView.reveal((int) mFab.getX() + mFab.getWidth() / 2, (int) mFab.getY() + mFab.getHeight() / 2,
                        getThemeColor(), Const.RADIUS, Const.DURATION, null);
            }
        });
    }
}
