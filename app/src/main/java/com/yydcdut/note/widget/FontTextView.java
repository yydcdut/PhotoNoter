package com.yydcdut.note.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15-3-30.
 */
public class FontTextView extends TextView {
    private boolean isFixed = false;

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.font_textview, defStyleAttr, 0);
            isFixed = a.getBoolean(R.styleable.font_textview_fixed, false);
            a.recycle();
        }
    }

    public void setFontSystem(boolean useSystem) {
        if (!useSystem || isFixed) {
            Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            this.setTypeface(typeFace);
        }
    }
}
