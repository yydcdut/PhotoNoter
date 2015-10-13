package com.yydcdut.note.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.LocalStorageUtils;

/**
 * Created by yuyidong on 15-3-30.
 */
public class FontTextView extends TextView {
    private boolean isUseSystem = true;
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
        }
        isUseSystem = LocalStorageUtils.getInstance().getSettingFontSystem();
        if (!isUseSystem || isFixed) {
            Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            this.setTypeface(typeFace);
        }
    }


}
