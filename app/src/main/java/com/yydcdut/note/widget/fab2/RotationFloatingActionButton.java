package com.yydcdut.note.widget.fab2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;

import com.yydcdut.note.R;


/**
 * Created by yuyidong on 15/10/27.
 */
public class RotationFloatingActionButton extends FloatingActionButton {
    /* 动画时间 */
    private static final int ANIMATION_DURATION = 300;
    /* close的时候角度 */
    private static final float COLLAPSED_PLUS_ROTATION = 0f;
    /* open的时候角度 */
    private static final float EXPANDED_PLUS_ROTATION = 90f + 45f + 360f;

    private RotatingDrawable mRotatingDrawable;
    private AnimatorSet mOpenedAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private AnimatorSet mClosedAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);

    public RotationFloatingActionButton(Context context) {
        this(context, null);
    }

    public RotationFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotationFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDrawable(getDrawable());
    }

    private void initDrawable(Drawable drawable) {
        if (drawable == null) {
            drawable = getResources().getDrawable(R.drawable.ic_launcher);
        }
        mRotatingDrawable = new RotatingDrawable(drawable);
        final OvershootInterpolator interpolator = new OvershootInterpolator();

        final ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(mRotatingDrawable, "rotation", EXPANDED_PLUS_ROTATION, COLLAPSED_PLUS_ROTATION);
        final ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(mRotatingDrawable, "rotation", COLLAPSED_PLUS_ROTATION, EXPANDED_PLUS_ROTATION);

        collapseAnimator.setInterpolator(interpolator);
        expandAnimator.setInterpolator(interpolator);

        mOpenedAnimation.play(expandAnimator);
        mClosedAnimation.play(collapseAnimator);

        setImageDrawable(mRotatingDrawable);
    }

    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            throw new NullPointerException("");
        }
        initDrawable(drawable);
    }

    protected void closeAnimation() {
        mClosedAnimation.start();
        mOpenedAnimation.cancel();
    }

    protected void openAnimation() {
        mClosedAnimation.cancel();
        mOpenedAnimation.start();
    }

    /**
     * 旋转的Drawable
     */
    private class RotatingDrawable extends LayerDrawable {
        public RotatingDrawable(Drawable drawable) {
            super(new Drawable[]{drawable});
        }

        private float mRotation;

        @SuppressWarnings("UnusedDeclaration")
        public float getRotation() {
            return mRotation;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
            super.draw(canvas);
            canvas.restore();
        }
    }

}
