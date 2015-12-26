package com.yydcdut.note.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by yuyidong on 15/10/14.
 */
public class AppCompat {

    public static int getColor(int id, Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return activity.getResources().getColor(id);
        } else {
            return activity.getResources().getColor(id, activity.getTheme());
        }
    }

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 5.0之后的SDK
     */
    public static final boolean AFTER_LOLLIPOP = afterLollipop();

    private static boolean afterLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setElevation(View view, float elevation) {
        if (AFTER_LOLLIPOP) {
            view.setElevation(elevation);
        }
    }

    public static void setFullWindow(Window window) {
        if (AppCompat.AFTER_LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void setStatuColor(Window window) {
//        int index = LocalStorageUtils.getInstance().getThemeColor();
//        if (AFTER_LOLLIPOP) {
//            window.setStatusBarColor(ThemeHelper.THEME.get(index).getStatusColor());
//            window.setNavigationBarColor(ThemeHelper.THEME.get(index).getStatusColor());
//        }
    }

}
