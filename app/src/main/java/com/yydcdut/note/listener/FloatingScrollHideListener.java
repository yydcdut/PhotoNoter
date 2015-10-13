package com.yydcdut.note.listener;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;

import com.yydcdut.note.view.fab.FloatingActionsMenu;


/**
 * Created by yuyidong on 15-3-25.
 */
public class FloatingScrollHideListener implements AbsListView.OnScrollListener {
    private static final int SATISFIED_HIDE_TIMES = 3;
    private static final int DIRECTION_CHANGE_THRESHOLD = 1;
    private int mPrevPosition;
    private int mPrevTop;
    private boolean mUpdated;
    private boolean mHide;
    private FloatingActionsMenu mFloatingActionsMenu;
    private int mTime = 0;

    public FloatingScrollHideListener(FloatingActionsMenu mFloatingActionsMenu) {
        this.mFloatingActionsMenu = mFloatingActionsMenu;
        reset();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //如果是打开状态，就不操作
        if (mFloatingActionsMenu.isExpanded()) {
            return;
        }
        //得到第一个childView
        final View topChild = view.getChildAt(0);
        //得到第一个childView的Top
        int firstViewTop = 0;
        if (topChild != null) {
            firstViewTop = topChild.getTop();
        }
        //是不是往下
        boolean goingDown;
        //scroll是否满足需求的滑动
        boolean changed = true;
        //记录的mPrevPosition与现在第一个显示的position位置是否相同
        if (mPrevPosition == firstVisibleItem) {//相同的话
            //他们top之间的差值
            final int topDelta = mPrevTop - firstViewTop;
            //firstViewTop < mPrevTop 为true，往上滑，否则往下
            goingDown = firstViewTop < mPrevTop;
            //判断这个差值是否满足需求
            changed = Math.abs(topDelta) > DIRECTION_CHANGE_THRESHOLD;
        } else {
            //firstVisibleItem > mPrevPosition为true的时候代表往下滑
            goingDown = firstVisibleItem > mPrevPosition;
        }
        if (changed && mUpdated) {
            onDirectionChanged(goingDown);
        }
        mPrevPosition = firstVisibleItem;
        mPrevTop = firstViewTop;
        mUpdated = true;
    }

    public boolean isHide() {
        return mHide;
    }

    public void show() {
        onDirectionChanged(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        switch (scrollState) {
//            case SCROLL_STATE_IDLE:
//                onDirectionChanged(false);
//                break;
//            case SCROLL_STATE_TOUCH_SCROLL:
//                onDirectionChanged(true);
//                break;
//            case SCROLL_STATE_FLING:
////                onDirectionChanged(true);
//                break;
//        }
        //No-op
    }

    public void reset() {
        mPrevPosition = 0;
        mPrevTop = 0;
        mUpdated = false;
        mTime = 0;
    }

    private void onDirectionChanged(boolean goingDown) {
        leHide(goingDown, true);
    }

    private void leHide(final boolean hide, final boolean animated) {
        mTime++;
        if (mTime <= SATISFIED_HIDE_TIMES) {
            return;
        }
        leHide(hide, animated, false);
    }


    private void leHide(final boolean hide, final boolean animated, final boolean deferred) {
        if (mHide != hide || deferred) {
            mHide = hide;
            final int height = mFloatingActionsMenu.getHeight();
            if (height == 0 && !deferred) {
                // Dang it, haven't been drawn before, defer! defer!
                final ViewTreeObserver vto = mFloatingActionsMenu.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            // Sometimes is not the same we used to know
                            final ViewTreeObserver currentVto = mFloatingActionsMenu.getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            leHide(hide, animated, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int marginBottom = 0;
            final ViewGroup.LayoutParams layoutParams = mFloatingActionsMenu.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
            }
            final int translationY = mHide ? height + marginBottom : 0;
            if (animated) {
                mFloatingActionsMenu.animate()
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(1000)
                        .translationY(translationY);
            } else {
                mFloatingActionsMenu.setTranslationY(translationY);
            }
        }
    }
}
