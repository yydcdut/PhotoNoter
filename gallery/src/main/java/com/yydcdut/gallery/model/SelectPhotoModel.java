package com.yydcdut.gallery.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by yuyidong on 16/3/23.
 */
public class SelectPhotoModel {
    private Set<String> mSelectPhotoSet;

    private SelectPhotoModel() {
        mSelectPhotoSet = new TreeSet<>(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return 1;
            }
        });
    }

    private static class Holder {
        public static final SelectPhotoModel INSTANCE = new SelectPhotoModel();
    }

    public static SelectPhotoModel getInstance() {
        return Holder.INSTANCE;
    }

    public boolean addPath(String path) {
        return mSelectPhotoSet.add(path);
    }

    public boolean removePath(String path) {
        return mSelectPhotoSet.remove(path);
    }

    public boolean contains(String path) {
        return mSelectPhotoSet.contains(path);
    }

    public int getCount() {
        return mSelectPhotoSet.size();
    }

    public void clear() {
        mSelectPhotoSet.clear();
    }

}
