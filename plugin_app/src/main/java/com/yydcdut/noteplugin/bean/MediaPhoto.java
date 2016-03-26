package com.yydcdut.noteplugin.bean;

import android.support.annotation.NonNull;

/**
 * Created by yuyidong on 16/3/19.
 */
public class MediaPhoto {
    private final String mPath;
    private final String mThumbPath;

    public MediaPhoto(@NonNull String path, @NonNull String thumbPath) {
        mPath = path;
        mThumbPath = thumbPath;
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    @NonNull
    public String getThumbPath() {
        return mThumbPath;
    }

    @Override
    public String toString() {
        return "MediaPhoto{" +
                "mPath='" + mPath + '\'' +
                ", mThumbPath='" + mThumbPath + '\'' +
                '}';
    }
}
