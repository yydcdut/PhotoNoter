package com.yydcdut.note.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.yydcdut.note.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuyidong on 15/7/13.
 */
public class ThemeHelper {
    public static ArrayList<ThemeHelper> THEME;

    static {
        THEME = create(Arrays.asList(
                R.style.ThemeBlue,
                R.style.ThemeIndigo,
                R.style.ThemeCyan,
                R.style.ThemeTeal,
                R.style.ThemeGreen,
                R.style.ThemeLightGreen,
                R.style.ThemeLime,
                R.style.ThemeYellow,
                R.style.ThemeAmber,
                R.style.ThemeOrange,
                R.style.ThemeDeepOrange,
                R.style.ThemeRed,
                R.style.ThemePurple,
                R.style.ThemeBrown,
                R.style.ThemeGray,
                R.style.ThemeBlueGray
        ), Arrays.asList(
                R.color.blue_colorPrimaryDark,
                R.color.indigo_colorPrimaryDark,
                R.color.cyan_colorPrimaryDark,
                R.color.teal_colorPrimaryDark,
                R.color.green_colorPrimaryDark,
                R.color.light_green_colorPrimaryDark,
                R.color.lime_colorPrimaryDark,
                R.color.yellow_colorPrimaryDark,
                R.color.amber_colorPrimaryDark,
                R.color.orange_colorPrimaryDark,
                R.color.deep_orange_colorPrimaryDark,
                R.color.red_colorPrimaryDark,
                R.color.purple_colorPrimaryDark,
                R.color.brown_colorPrimaryDark,
                R.color.gray_colorPrimaryDark,
                R.color.blue_gray_colorPrimaryDark
        ), Arrays.asList(
                R.color.blue_colorPrimary,
                R.color.indigo_colorPrimary,
                R.color.cyan_colorPrimary,
                R.color.teal_colorPrimary,
                R.color.green_colorPrimary,
                R.color.light_green_colorPrimary,
                R.color.lime_colorPrimary,
                R.color.yellow_colorPrimary,
                R.color.amber_colorPrimary,
                R.color.orange_colorPrimary,
                R.color.deep_orange_colorPrimary,
                R.color.red_colorPrimary,
                R.color.purple_colorPrimary,
                R.color.brown_colorPrimary,
                R.color.gray_colorPrimary,
                R.color.blue_gray_colorPrimary
        ));
    }

    private static ArrayList<ThemeHelper> create(List<Integer> styleList, List<Integer> statusColorList, List<Integer> colorPrimaryList) {
        if (statusColorList.size() != statusColorList.size()) {
            throw new IllegalArgumentException("两个必须得相同");
        }
        ArrayList<ThemeHelper> themeHelperList = new ArrayList<>(20);
        for (int i = 0; i < styleList.size(); i++) {
            themeHelperList.add(new ThemeHelper(styleList.get(i), statusColorList.get(i), colorPrimaryList.get(i)));
        }
        return themeHelperList;
    }


    /**
     * 样式
     */
    private int style;
    /**
     * statusBar的颜色
     */
    private int statusColor;

    private int colorPrimary;

    public ThemeHelper(int style, int statusColor, int colorPrimary) {
        this.style = style;
        this.statusColor = statusColor;
        this.colorPrimary = colorPrimary;

    }

    public int getStyle() {
        return style;
    }

    public int getStatusColor() {
        return statusColor;
    }

    public int getColorPrimary() {
        return colorPrimary;
    }

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
