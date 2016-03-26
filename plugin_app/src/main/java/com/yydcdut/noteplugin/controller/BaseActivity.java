package com.yydcdut.noteplugin.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.yydcdut.noteplugin.R;

import java.lang.reflect.Field;

/**
 * Created by yuyidong on 16/3/20.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    public static final int CODE_RESULT_CHANGED = 1;
    public static final int CODE_RESULT_NOT_CHANGED = -1;

    public static final String INTENT_PAGE = "page";
    public static final String INTENT_FOLDER = "folder";

    public static final String INTENT_PREVIEW_SELECTED = "preview_selected";

    /**
     * 得到statusBar高度
     *
     * @return
     */
    public int getStatusBarSize() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sBar = 38;//默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sBar = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sBar;
    }

    /**
     * 得到actionbar大小
     *
     * @return
     */
    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColor = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();
        return color;
    }
}
