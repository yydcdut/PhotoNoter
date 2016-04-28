package com.yydcdut.note.model.gallery;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yuyidong on 16/3/23.
 */
public class SelectPhotoModel {
    private List<String> mSelectPhotoList;

    @Singleton
    @Inject
    public SelectPhotoModel() {
        mSelectPhotoList = new ArrayList<>();
    }

    public boolean addPath(String path) {
        if (!mSelectPhotoList.contains(path)) {
            return mSelectPhotoList.add(path);
        } else {
            return false;
        }
    }

    public boolean removePath(String path) {
        return mSelectPhotoList.remove(path);
    }

    public boolean contains(String path) {
        return mSelectPhotoList.contains(path);
    }

    public int getCount() {
        return mSelectPhotoList.size();
    }

    public void clear() {
        mSelectPhotoList.clear();
    }

    public String get(int position) {
        return mSelectPhotoList.get(position);
    }

}
