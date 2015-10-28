package com.yydcdut.note.view.fab2;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuyidong on 15/10/28.
 */
public class FloatingMenuLayout extends CoordinatorLayout implements View.OnClickListener {
    private RotationFloatingActionButton mRotationFloatingActionButton;
    private Map<Integer, int[]> mLocationMap;
    private int[] mMenuLocation;
    private boolean mIsOpened = false;

    public FloatingMenuLayout(Context context) {
        this(context, null);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLocationMap = new HashMap<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof RotationFloatingActionButton) {
                mRotationFloatingActionButton = (RotationFloatingActionButton) view;
                mRotationFloatingActionButton.setOnClickListener(this);
            } else {
                view.setVisibility(INVISIBLE);
            }
        }
    }

    private void getLocation() {
        if (mLocationMap == null) {
            throw new NullPointerException("");
        }
        if (mLocationMap.size() != 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int top = view.getTop();
            int left = view.getLeft();
            int bottom = view.getBottom();
            int right = view.getRight();
            if (view instanceof RotationFloatingActionButton) {
                mRotationFloatingActionButton = (RotationFloatingActionButton) view;
                mRotationFloatingActionButton.setOnClickListener(this);
                mMenuLocation = new int[]{left, top, right, bottom};
            } else if (view instanceof ScrollFloatingActionButton) {
                mLocationMap.put(i, new int[]{left, top, right, bottom});
                view.setVisibility(GONE);
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) view;
                btn.initLocation(mLocationMap.get(i), mMenuLocation);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        getLocation();
    }


    @Override
    public void onClick(View v) {
        if (v == mRotationFloatingActionButton) {
            if (mIsOpened) {
                mRotationFloatingActionButton.closeAnimation();
                mIsOpened = false;
                scrollClose();
            } else {
                mRotationFloatingActionButton.openAnimation();
                mIsOpened = true;
                scrollOpen();
            }
        }
    }

    private void scrollOpen() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) getChildAt(i);
                btn.setVisibility(VISIBLE);
                btn.scroll2CloseSmooth();
            }
        }
    }

    private void scrollClose() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) getChildAt(i);
                btn.setVisibility(VISIBLE);
                btn.scroll2OpenSmooth();
            }
        }
    }


}
