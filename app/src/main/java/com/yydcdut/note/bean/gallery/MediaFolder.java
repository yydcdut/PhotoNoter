package com.yydcdut.note.bean.gallery;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by yuyidong on 16/3/19.
 */
public class MediaFolder {
    public static final String ALL = "全部";

    private final String mFolderName;
    private final List<MediaPhoto> mMediaPhotoList;

    public MediaFolder(@NonNull String folderName, @NonNull List<MediaPhoto> mediaPhotoList) {
        mFolderName = folderName;
        mMediaPhotoList = mediaPhotoList;
    }

    @NonNull
    public String getFolderName() {
        return mFolderName;
    }


    @NonNull
    public List<MediaPhoto> getMediaPhotoList() {
        return mMediaPhotoList;
    }

    @Override
    public String toString() {
        return "MediaFolder{" +
                "mFolderName='" + mFolderName + '\'' +
                ", mMediaPhotoList=" + mMediaPhotoList +
                '}';
    }
}
