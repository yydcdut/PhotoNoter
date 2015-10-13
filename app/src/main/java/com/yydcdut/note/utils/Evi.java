package com.yydcdut.note.utils;

import android.content.Context;

import com.yydcdut.note.NoteApplication;

/**
 * Created by yuyidong on 15/7/13.
 */
public class Evi {
    /**
     * 屏幕宽度
     */
    public static int sScreenWidth;
    /**
     * 屏幕高度
     */
    public static int sScreenHeight;

    public static void init() {
        Context context = NoteApplication.getInstance().getApplicationContext();
        sScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        sScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }
}
