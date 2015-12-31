package com.yydcdut.note.camera.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/12/31.
 */
public class ColorUtils {

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColor = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();
        return color;
    }

    public static int getDarkPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimaryDark};
        int indexOfAttrColor = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();
        return color;
    }
}
