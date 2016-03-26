package com.yydcdut.noteplugin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/3/23.
 */
public class SelectPhotoModel {
    private List<String> mSelectPhotoList;

    private SelectPhotoModel() {
        mSelectPhotoList = new ArrayList<>();
    }

    private static class Holder {
        public static final SelectPhotoModel INSTANCE = new SelectPhotoModel();
    }

    public static SelectPhotoModel getInstance() {
        return Holder.INSTANCE;
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
