package com.yydcdut.note.view.fab2;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

import java.util.Arrays;

/**
 * Created by yuyidong on 15/10/28.
 */
public class ScrollFloatingActionButton extends FloatingActionButton {
    private Scroller mCloseScroller;
    private Scroller mOpenScroller;

    public ScrollFloatingActionButton(Context context) {
        this(context, null);
    }

    public ScrollFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCloseScroller = new Scroller(context, new DecelerateInterpolator(3f));
        mOpenScroller = new Scroller(context, new OvershootInterpolator());
    }

    private int[] mSelf;
    private int[] mMenu;

    public void initLocation(int[] self, int[] menu) {
        mSelf = self;
        mMenu = menu;
        Log.i("yuyidong", Arrays.toString(mSelf) + "   self");
        Log.i("yuyidong", Arrays.toString(mMenu) + "   mMenu");
    }

    public void scroll2CloseSmooth() {
        int deltaX = mSelf[0] - mMenu[0];
        int deltaY = mSelf[1] - mMenu[1];
        mCloseScroller.startScroll(0, 0, deltaX, deltaY, 300);
        postInvalidate();
    }

    public void scroll2OpenSmooth() {
        int deltaX = mSelf[0] - mMenu[0];
        int deltaY = mSelf[1] - mMenu[1];
        mOpenScroller.startScroll(deltaX, deltaY, -deltaX, -deltaY, 300);
        postInvalidate();
        setVisibility(VISIBLE);
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
            postInvalidate();
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
            postInvalidate();
        }
        super.computeScroll();
    }


}
