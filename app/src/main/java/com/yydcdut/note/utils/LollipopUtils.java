package com.yydcdut.note.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by yuyidong on 15/10/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LollipopUtils {

    /**
     * 5.0之后的SDK
     */
    public static final boolean AFTER_LOLLIPOP = afterLollipop();

    private static boolean afterLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void setElevation(View view, float elevation) {
        if (AFTER_LOLLIPOP) {
            view.setElevation(elevation);
        }
    }

    public static void setFullWindow(Window window) {
        if (LollipopUtils.AFTER_LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
