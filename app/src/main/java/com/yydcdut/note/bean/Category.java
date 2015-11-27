package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/7/8.
 */
public class Category implements IObject {

    private final int id;
    private String label;
    private int photosNumber;
    private boolean isCheck;
    private int sort;

    public Category(int id, String label, int photosNumber, int sort, boolean isCheck) {
        this.id = id;
        this.label = label;
        this.photosNumber = photosNumber;
        this.isCheck = isCheck;
        this.sort = sort;
    }

    public int getId() {
        return id;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", photosNumber=" + photosNumber +
                ", isCheck=" + isCheck +
                ", sort=" + sort +
                '}';
    }
}
