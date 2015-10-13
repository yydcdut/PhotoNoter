package com.yydcdut.sdlv;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/9/25.
 */
class ItemCustomLayout extends FrameLayout {
    private ImageView mBGImage;

    public ItemCustomLayout(Context context) {
        this(context, null);
    }

    public ItemCustomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBGImage = new ImageView(context);
        mBGImage.setBackgroundColor(Color.TRANSPARENT);
        addView(mBGImage, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addCustomView(View customView) {
        addView(customView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public View getCustomView() {
        return getChildAt(1);
    }

    public View getRealView() {
        return this;
    }

    public ImageView getBackGroundImage() {
        return mBGImage;
    }

}
