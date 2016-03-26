package com.yydcdut.noteplugin.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by yuyidong on 16/3/20.
 */
public class GalleryApp {
    private final String mAppName;
    private final String mPackageName;
    private final Drawable mLogoDrawable;

    public GalleryApp(Drawable logoDrawable, String packageName, String appName) {
        mLogoDrawable = logoDrawable;
        mPackageName = packageName;
        mAppName = appName;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public Drawable getLogoDrawable() {
        return mLogoDrawable;
    }
}
