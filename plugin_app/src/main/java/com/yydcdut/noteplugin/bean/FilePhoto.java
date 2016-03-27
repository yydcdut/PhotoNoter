package com.yydcdut.noteplugin.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yydcdut.noteplugin.model.PhotoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yuyidong on 16/3/27.
 */
public class FilePhoto implements TreeFile {
    private final int mLevel;
    private final TreeFile mParentTreeFile;
    private List<TreeFile> mChildrenFileList;
    private final String mFileName;
    private String[] mCoverPhotoArray;

    public FilePhoto(@NonNull int level, @NonNull String fileName, @Nullable TreeFile parentTreeFile) {
        mLevel = level;
        mFileName = fileName;
        mParentTreeFile = parentTreeFile;
    }

    @NonNull
    @Override
    public String getFileName() {
        return mFileName;
    }

    @Nullable
    @Override
    public TreeFile getParent() {
        return mParentTreeFile;
    }

    @Override
    public void addChild(@NonNull TreeFile treeFile) {
        if (mChildrenFileList == null) {
            mChildrenFileList = new ArrayList<>();
        }
        mChildrenFileList.add(treeFile);
        Collections.sort(mChildrenFileList, PhotoModel.getComparator());
    }

    @Nullable
    @Override
    public List<TreeFile> getChildren() {
        return mChildrenFileList;
    }

    @Override
    public boolean addCoverPhoto(@NonNull String photoName) {
        if (mCoverPhotoArray == null) {
            mCoverPhotoArray = new String[MAX_COVER_NUMBER];
        }
        for (int i = 0; i < MAX_COVER_NUMBER; i++) {
            if (TextUtils.isEmpty(mCoverPhotoArray[i])) {
                mCoverPhotoArray[i] = photoName;
                return true;
            }
        }
        return false;
    }

    @Nullable
    public String[] getCoverPhotoArray() {
        return mCoverPhotoArray;
    }

    @NonNull
    @Override
    public int getLevel() {
        return mLevel;
    }
}
