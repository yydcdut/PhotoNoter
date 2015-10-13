package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemBackGroundLayout extends ViewGroup {
    /* 背景的颜色 */
    private ImageView mBGImage;
    /* 下一个View的距离 */
    private int mMarginLeft = 0;
    private int mMarginRight = 0;
    /* 添加的子View */
    private List<View> mBtnViews;


    public ItemBackGroundLayout(Context context) {
        this(context, null);
    }

    public ItemBackGroundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemBackGroundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBGImage = new ImageView(context);
        mBGImage.setBackgroundColor(Color.TRANSPARENT);
        addView(mBGImage, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mBtnViews = new ArrayList<>();
    }

    public View addMenuItem(MenuItem menuItem) {
        int count = getChildCount();
        if (!TextUtils.isEmpty(menuItem.text)) {
            TextView textView = new TextView(getContext());
            textView.setBackgroundDrawable(menuItem.background);
            textView.setText(menuItem.text);
            textView.setTextSize(menuItem.textSize);
            textView.setTextColor(menuItem.textColor);
            textView.setGravity(Gravity.CENTER);
            textView.setTag(menuItem);
            addView(textView, count, new LayoutParams(LayoutParams.MATCH_PARENT, menuItem.width));
            requestLayout();
            mBtnViews.add(textView);
            return textView;
        } else if (menuItem.icon != null) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundDrawable(menuItem.background);
            imageView.setImageDrawable(menuItem.icon);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setTag(menuItem);
            addView(imageView, count, new LayoutParams(LayoutParams.MATCH_PARENT, menuItem.width));
            requestLayout();
            mBtnViews.add(imageView);
            return imageView;
        } else {
            throw new IllegalArgumentException("必须得有一个!");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int total = getChildCount();
        for (int i = 0; i < total; i++) {
            View view = getChildAt(i);
            if (view == mBGImage) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
            } else {
                MenuItem menuItem = (MenuItem) view.getTag();
                measureChild(view, MeasureSpec.makeMeasureSpec(menuItem.width, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int total = getChildCount();
        mMarginLeft = 0;
        mMarginRight = getMeasuredWidth();
        for (int i = 0; i < total; i++) {
            View view = getChildAt(i);
            if (view == mBGImage) {
                view.layout(l, t, r, b);
            } else {
                MenuItem menuItem = (MenuItem) view.getTag();
                if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
                    view.layout(mMarginLeft, t, menuItem.width + mMarginLeft, b);
                    mMarginLeft += menuItem.width;
                } else {
                    view.layout(mMarginRight - menuItem.width, t, mMarginRight, b);
                    mMarginRight -= menuItem.width;
                }
            }
        }
    }

    public ImageView getBackGroundImage() {
        return mBGImage;
    }

    public List<View> getBtnViews() {
        return mBtnViews;
    }
}
