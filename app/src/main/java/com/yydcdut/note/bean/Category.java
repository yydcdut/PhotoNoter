package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/7/8.
 */
public class Category implements IObject {

    private int id;
    private final String label;
    private String showLabel;
    private int photosNumber;
    private int sort;
    private boolean isCheck;

    public Category(int id, String label, String showLabel, int photosNumber, int sort, boolean isCheck) {
        this.id = id;
        this.label = label;
        this.showLabel = showLabel;
        this.photosNumber = photosNumber;
        this.isCheck = isCheck;
        this.sort = sort;
    }

    public Category(String label, String showLabel, int photosNumber, int sort, boolean isCheck) {
        this.label = label;
        this.showLabel = showLabel;
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

    public String getLabel() {
        return label;
    }

    public String getShowLabel() {
        return showLabel;
    }

    public void setShowLabel(String showLabel) {
        this.showLabel = showLabel;
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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", showLabel='" + showLabel + '\'' +
                ", photosNumber=" + photosNumber +
                ", sort=" + sort +
                ", isCheck=" + isCheck +
                '}';
    }
}
