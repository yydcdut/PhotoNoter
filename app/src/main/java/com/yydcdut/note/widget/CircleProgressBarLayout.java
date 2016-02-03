package com.yydcdut.note.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/7/28.
 */
public class CircleProgressBarLayout extends FrameLayout implements View.OnTouchListener {
    private CircleProgressBar mCircleProgressBar;

    public CircleProgressBarLayout(Context context) {
        this(context, null);
    }

    public CircleProgressBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_circleprogreebar, this, true);
        hide();
        setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircleProgressBar = (CircleProgressBar) findViewById(R.id.pb_loading);
        mCircleProgressBar.setColorSchemeColors(getThemeColor());
    }

    public CircleProgressBar getCircleProgressBar() {
        return mCircleProgressBar;
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // do nothing
        return true;
    }

    public int getThemeColor() {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColor = 0;
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();
        return color;
    }
}
