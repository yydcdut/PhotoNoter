package com.yydcdut.note.widget;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yydcdut.note.R;

/**
 * Created by yyd on 15-4-27.
 */
public class RevealView extends FrameLayout {
    private RevealColorView mRevealColorView;
    private View mInstance = this;

    public RevealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.reveal_touch, 0, 0);
        boolean ownTouch = a.getBoolean(R.styleable.reveal_touch_own_touch, false);
        a.recycle();
        if (!ownTouch) {
            mInstance.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
        mInstance.setVisibility(GONE);
//        mRevealColorView = new RevealColorView(context);
//        mRevealColorView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mRevealColorView.invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 1) {
            if (getChildAt(0) instanceof RevealColorView) {
                mRevealColorView = (RevealColorView) getChildAt(0);
                mInstance.setVisibility(INVISIBLE);
                reveal(100, 100, 1, 1, 100, new RevealAnimationListener() {
                    @Override
                    public void finish() {
                        hide(100, 100, 1, 1, 100, new RevealAnimationListener() {
                            @Override
                            public void finish() {
                                mInstance.setVisibility(GONE);
                            }
                        });
                    }
                });
            } else {
                throw new NullPointerException("要有RevealColorView");
            }
        } else {
            throw new IllegalArgumentException("只能有RevealColorView");
        }
    }

    public void reveal(final int x, final int y, final int color, final int startRadius, long duration, final RevealAnimationListener listener) {
        mRevealColorView.reveal(x, y, color, startRadius, duration, new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mInstance.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void hide(final int x, final int y, final int color, final int endRadius, final long duration, final RevealAnimationListener listener) {
        mRevealColorView.hide(x, y, color, endRadius, duration, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mInstance.setVisibility(View.GONE);
                if (listener != null) {
                    listener.finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    public interface RevealAnimationListener {
        public void finish();
    }
}
