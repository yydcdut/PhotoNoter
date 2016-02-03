package com.yydcdut.note.widget.fab2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/10/28.
 */
public class ScrollFloatingActionButton extends FloatingActionButton {
    /* 动画时间 */
    private static final int ANIMATION_DURATION = 300;
    /* Scroller 为什么是2个呢，是因为不同的Interpolator */
    private Scroller mCloseScroller;
    private Scroller mOpenScroller;
    /* 一开始的时候自己的坐标 */
    private int[] mSelf;
    /* 已开始的时候Menu的坐标 */
    private int[] mMenu;
    /* fab旁边的label文字 */
    private String mTitle;
    /* fab旁边的label */
    private TextView mTextView;

    public ScrollFloatingActionButton(Context context) {
        this(context, null);
    }

    public ScrollFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CustomFloationActionButton, 0, 0);
        mTitle = attr.getString(R.styleable.CustomFloationActionButton_fab_text);
        attr.recycle();
        mCloseScroller = new Scroller(context, new DecelerateInterpolator(3f));
        mOpenScroller = new Scroller(context, new OvershootInterpolator());
    }

    /**
     * 初始化位置
     *
     * @param self
     * @param menu
     */
    protected void initLocation(int[] self, int[] menu) {
        mSelf = self;
        mMenu = menu;
        if (getTag() != null) {
            mTextView = (TextView) getTag();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    protected void scroll2CloseSmooth() {
        int deltaX = mSelf[0] - mMenu[0];
        int deltaY = mSelf[1] - mMenu[1];
        mCloseScroller.startScroll(0, 0, deltaX, deltaY, ANIMATION_DURATION);
        postInvalidate();
    }

    protected void scroll2OpenSmooth() {
        int deltaX = mSelf[0] - mMenu[0];
        int deltaY = mSelf[1] - mMenu[1];
        setVisibility(VISIBLE);
        if (mTextView != null) {
            mTextView.setVisibility(VISIBLE);
        }
        mOpenScroller.startScroll(deltaX, deltaY, -deltaX, -deltaY, ANIMATION_DURATION);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        if (mCloseScroller.computeScrollOffset()) {
            float totalX = Math.abs(mCloseScroller.getFinalX() - mCloseScroller.getStartX());
            float nowX = Math.abs(mCloseScroller.getCurrX() - mCloseScroller.getStartX());
            float totalY = Math.abs(mCloseScroller.getFinalY() - mCloseScroller.getStartY());
            float nowY = Math.abs(mCloseScroller.getCurrY() - mCloseScroller.getStartY());
            left = (int) (mSelf[0] - (nowX * (mSelf[0] - mMenu[0]) / totalX));
            top = (int) (mSelf[1] - (nowY * (mSelf[1] - mMenu[1]) / totalY));
            right = (int) (mSelf[2] - (nowX * (mSelf[2] - mMenu[2]) / totalX));
            bottom = (int) (mSelf[3] - (nowY * (mSelf[3] - mMenu[3]) / totalY));
            if (totalX == 0) {
                left = mMenu[0];
                right = mMenu[2];
            }
            if (totalY == 0) {
                top = mMenu[1];
                bottom = mMenu[3];
            }
            layout(left, top, right, bottom);
            if (mTextView != null) {
                int width = mTextView.getWidth() != 0 ? mTextView.getWidth() :
                        mTextView.getMeasuredWidth() == 0 ? 100 : mTextView.getMeasuredWidth();
                int height = mTextView.getHeight() != 0 ? mTextView.getHeight() :
                        mTextView.getMeasuredHeight() == 0 ? 100 : mTextView.getMeasuredHeight();
                mTextView.layout(left - 20 - width, top + (bottom - top - height) / 2, left - 20, bottom - (bottom - top - height) / 2);
                if (mTextView != null) {
                    Rect rect = (Rect) mTextView.getTag();
                    rect.set(left - 20 - width, top + (bottom - top - height) / 2, left - 20, bottom - (bottom - top - height) / 2);
                }
                if (totalX != 0) {
                    mTextView.setAlpha(1 - (nowX / totalX));
                } else if (totalY != 0) {
                    mTextView.setAlpha(1 - (nowY / totalY));
                }
            }
            postInvalidate();
            if (mCloseScroller.isFinished()) {
                this.setVisibility(INVISIBLE);
                if (mTextView != null) {
                    mTextView.setVisibility(INVISIBLE);
                }
            }
        } else if (mOpenScroller.computeScrollOffset()) {
            float totalX = Math.abs(mOpenScroller.getFinalX() - mOpenScroller.getStartX());
            float nowX = Math.abs(mOpenScroller.getCurrX() - mOpenScroller.getStartX());
            float totalY = Math.abs(mOpenScroller.getFinalY() - mOpenScroller.getStartY());
            float nowY = Math.abs(mOpenScroller.getCurrY() - mOpenScroller.getStartY());
            left = (int) (mMenu[0] + (nowX * (mSelf[0] - mMenu[0]) / totalX));
            top = (int) (mMenu[1] + (nowY * (mSelf[1] - mMenu[1]) / totalY));
            right = (int) (mMenu[2] + (nowX * (mSelf[2] - mMenu[2]) / totalX));
            bottom = (int) (mMenu[3] + (nowY * (mSelf[3] - mMenu[3]) / totalY));
            if (totalX == 0) {
                left = mSelf[0];
                right = mSelf[2];
            }
            if (totalY == 0) {
                top = mSelf[1];
                bottom = mSelf[3];
            }
            layout(left, top, right, bottom);
            if (mTextView != null) {
                int width = mTextView.getWidth() != 0 ? mTextView.getWidth() :
                        mTextView.getMeasuredWidth() == 0 ? 100 : mTextView.getMeasuredWidth();
                int height = mTextView.getHeight() != 0 ? mTextView.getHeight() :
                        mTextView.getMeasuredHeight() == 0 ? 100 : mTextView.getMeasuredHeight();
                mTextView.layout(left - 20 - width, top + (bottom - top - height) / 2, left - 20, bottom - (bottom - top - height) / 2);
                if (mTextView != null) {
                    Rect rect = (Rect) mTextView.getTag();
                    rect.set(left - 20 - width, top + (bottom - top - height) / 2, left - 20, bottom - (bottom - top - height) / 2);
                }
                if (totalX != 0) {
                    mTextView.setAlpha((nowX / totalX));
                } else if (totalY != 0) {
                    mTextView.setAlpha((nowY / totalY));
                }
            }
            postInvalidate();
        }
        super.computeScroll();
    }
}
