package com.yydcdut.note.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by yuyidong on 15/7/8.
 */
public class Category extends DataSupport implements IObject {

    private int id;
    private String label;
    private int photosNumber;
    private int sort;
    private boolean isCheck;

    public Category(int id, String label, int photosNumber, int sort, boolean isCheck) {
        this.id = id;
        this.label = label;
        this.photosNumber = photosNumber;
        this.isCheck = isCheck;
        this.sort = sort;
    }

    public Category(String label, int photosNumber, int sort, boolean isCheck) {
        this.label = label;
        this.photosNumber = photosNumber;
        this.isCheck = isCheck;
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPhotosNumber() {
        return photosNumber;
    }

    public void setPhotosNumber(int photosNumber) {
        this.photosNumber = photosNumber;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
