package com.yydcdut.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by yuyidong on 16/4/29.
 */
public class ZoomHeaderImageView extends FrameLayout {
    private ImageView mImageView0;
    private ImageView mImageView1;

    public ZoomHeaderImageView(Context context) {
        this(context, null);
    }

    public ZoomHeaderImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomHeaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView0 = new ImageView(context);
        mImageView1 = new ImageView(context);
        addView(mImageView0, layoutParams);
        addView(mImageView1, layoutParams);
        mImageView1.setVisibility(INVISIBLE);

    }
}
