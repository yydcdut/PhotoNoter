package com.yydcdut.note.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;

/**
 * Created by yuyidong on 16/4/29.
 */
public class ZoomHeaderImageView extends FrameLayout implements Handler.Callback {
    private int[] mResArray;
    private int mCurrentIndex = 0;

    private ImageView mImageView0;
    private ImageView mImageView1;

    private Handler mHandler;

    public ZoomHeaderImageView(Context context) {
        this(context, null);
    }

    public ZoomHeaderImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomHeaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mResArray = new int[]{R.drawable.bg_header_0, R.drawable.bg_header_1, R.drawable.bg_header_2,
                R.drawable.bg_header_3, R.drawable.bg_header_4};
        init(context);
        mHandler = new Handler(this);
        doAnimation();
    }

    private void init(Context context) {
        FrameLayout.LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView0 = new ImageView(context);
        mImageView0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView1 = new ImageView(context);
        mImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mImageView0, layoutParams);
        addView(mImageView1, layoutParams);
        mImageView1.setVisibility(INVISIBLE);
        mImageView0.setImageResource(mResArray[mCurrentIndex]);
    }

    private void doAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        if (mCurrentIndex % 2 == 0) {
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(mImageView0, "scaleX", 1f, 1.3f),
                    ObjectAnimator.ofFloat(mImageView0, "scaleY", 1f, 1.3f),
                    ObjectAnimator.ofFloat(mImageView0, "alpha", 1f, 0.0f),
                    ObjectAnimator.ofFloat(mImageView1, "scaleX", 1.3f, 1.0f),
                    ObjectAnimator.ofFloat(mImageView1, "scaleY", 1.3f, 1.0f),
                    ObjectAnimator.ofFloat(mImageView1, "alpha", 0.0f, 1.0f)
            );
        } else {
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(mImageView1, "scaleX", 1f, 1.3f),
                    ObjectAnimator.ofFloat(mImageView1, "scaleY", 1f, 1.3f),
                    ObjectAnimator.ofFloat(mImageView1, "alpha", 1f, 0.0f),
                    ObjectAnimator.ofFloat(mImageView0, "scaleX", 1.3f, 1.0f),
                    ObjectAnimator.ofFloat(mImageView0, "scaleY", 1.3f, 1.0f),
                    ObjectAnimator.ofFloat(mImageView0, "alpha", 0.0f, 1.0f)
            );
        }
        animatorSet.setDuration(600)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mImageView0.setVisibility(VISIBLE);
                        mImageView1.setVisibility(VISIBLE);
                        if (mCurrentIndex % 2 == 0) {
                            mImageView1.setImageResource(mResArray[(mCurrentIndex + 1) % 5]);
                        } else {
                            mImageView0.setImageResource(mResArray[(mCurrentIndex + 1) % 5]);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCurrentIndex++;
                        if (mCurrentIndex % 2 == 0) {
                            mImageView1.setVisibility(INVISIBLE);
                        } else {
                            mImageView0.setVisibility(INVISIBLE);
                        }
                        mHandler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
        animatorSet.start();
    }


    @Override
    public boolean handleMessage(Message msg) {
        doAnimation();
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(0);
    }
}
