package com.yydcdut.note.utils;

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
                R.style.noteBlue,
                R.style.noteBlue2,
                R.style.noteIndigo,
                R.style.noteBlue3,
                R.style.noteCyan,
                R.style.noteTeal,
                R.style.noteGreen,
                R.style.noteGreen2,
                R.style.noteLime,
                R.style.noteYellow,
                R.style.noteYellow2,
                R.style.noteOrange,
                R.style.noteOrange1,
                R.style.noteRed,
                R.style.noteRed2,
                R.style.notePurple,
                R.style.noteBrown,
                R.style.noteGray,
                R.style.noteBlueGray,
                R.style.noteBlack
        ), Arrays.asList(
                R.color.blue_colorPrimaryDark,
                R.color.blue2_colorPrimaryDark,
                R.color.indigo_colorPrimaryDark,
                R.color.blue3_colorPrimaryDark,
                R.color.cyan_colorPrimaryDark,
                R.color.teal_colorPrimaryDark,
                R.color.green_colorPrimaryDark,
                R.color.green2_colorPrimaryDark,
                R.color.lime_colorPrimaryDark,
                R.color.yellow_colorPrimaryDark,
                R.color.yellow2_colorPrimaryDark,
                R.color.orange_colorPrimaryDark,
                R.color.orange1_colorPrimaryDark,
                R.color.red_colorPrimaryDark,
                R.color.red2_colorPrimaryDark,
                R.color.purple_colorPrimaryDark,
                R.color.brown_colorPrimaryDark,
                R.color.gray_colorPrimaryDark,
                R.color.blue_gray_colorPrimaryDark,
                R.color.black_colorPrimaryDark
        ));
    }

    private static ArrayList<ThemeHelper> create(List<Integer> styleList, List<Integer> statusColorList) {
        if (statusColorList.size() != statusColorList.size()) {
            throw new IllegalArgumentException("两个必须得相同");
        }
        ArrayList<ThemeHelper> themeHelperList = new ArrayList<>(20);
        for (int i = 0; i < styleList.size(); i++) {
            themeHelperList.add(new ThemeHelper(styleList.get(i), statusColorList.get(i)));
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

    public ThemeHelper(int style, int statusColor) {
        this.style = style;
        this.statusColor = statusColor;
    }

    public int getStyle() {
        return style;
    }

    public int getStatusColor() {
        return statusColor;
    }
}
