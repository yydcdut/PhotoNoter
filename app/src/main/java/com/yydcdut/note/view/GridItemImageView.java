package com.yydcdut.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yydcdut.note.utils.Evi;

/**
 * Created by yuyidong on 15/7/13.
 */
public class GridItemImageView extends ImageView {
    public GridItemImageView(Context context) {
        super(context);
    }

    public GridItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = Evi.sScreenWidth / 3 + getPaddingLeft() + getPaddingRight();
        int height = Evi.sScreenWidth / 3 + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }
}
