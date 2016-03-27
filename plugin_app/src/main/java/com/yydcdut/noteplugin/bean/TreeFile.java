package com.yydcdut.noteplugin.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by yuyidong on 16/3/27.
 */
public interface TreeFile {

    int MAX_COVER_NUMBER = 4;

    @NonNull
    String getFileName();

    @Nullable
    TreeFile getParent();

    void addChild(@NonNull TreeFile treeFile);

    @Nullable
    List<TreeFile> getChildren();

    boolean addCoverPhoto(@NonNull String photoName);

    @Nullable
    String[] getCoverPhotoArray();

    @NonNull
    int getLevel();
}
