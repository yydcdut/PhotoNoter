package com.yydcdut.note.bean;

import android.graphics.Color;

import com.yydcdut.note.utils.FilePathUtils;

import java.util.Random;

/**
 * Created by yyd on 15-3-29.
 */
public class PhotoNote implements IObject {
    /**
     * ID
     */
    private int id;
    /**
     * 名字
     */
    private String photoName;
    /**
     * 照片创建时间
     */
    private long createdPhotoTime;
    /**
     * 照片编辑时间
     */
    private long editedPhotoTime;
    /**
     * 照片是否被选中
     */
    private boolean isSelected = false;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建笔记时间
     */
    private long createdNoteTime;
    /**
     * 最后修改笔记时间
     */
    private long editedNoteTime;
    /**
     * Category中的类别
     */
    private String categoryLabel;
    /**
     * 标记
     */
    private int tag;
    /**
     * 颜色
     */
    private int mPaletteColor = Color.argb(255, new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));

    public PhotoNote(String photoName, long createdPhotoTime, long editedPhotoTime,
                     String title, String content, long createdNoteTime,
                     long editedNoteTime, String categoryLabel) {
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryLabel = categoryLabel;
    }

    public PhotoNote(int id, String photoName, long createdPhotoTime, long editedPhotoTime,
                     String title, String content, long createdNoteTime,
                     long editedNoteTime, String categoryLabel) {
        this.id = id;
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryLabel = categoryLabel;
    }

    public String getSmallPhotoPathWithFile() {
        return "file:/" + FilePathUtils.getSmallPath() + photoName;
    }

    public String getBigPhotoPathWithFile() {
        return "file:/" + FilePathUtils.getPath() + photoName;
    }

    public String getSmallPhotoPathWithoutFile() {
        return FilePathUtils.getSmallPath() + photoName;
    }

    public String getBigPhotoPathWithoutFile() {
        return FilePathUtils.getPath() + photoName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public long getCreatedPhotoTime() {
        return createdPhotoTime;
    }

    public void setCreatedPhotoTime(long createdPhotoTime) {
        this.createdPhotoTime = createdPhotoTime;
    }

    public long getEditedPhotoTime() {
        return editedPhotoTime;
    }

    public void setEditedPhotoTime(long editedPhotoTime) {
        this.editedPhotoTime = editedPhotoTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedNoteTime() {
        return createdNoteTime;
    }

    public long getEditedNoteTime() {
        return editedNoteTime;
    }

    public void setEditedNoteTime(long editedNoteTime) {
        this.editedNoteTime = editedNoteTime;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setCreatedNoteTime(long createdNoteTime) {
        this.createdNoteTime = createdNoteTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public int getPaletteColor() {
        return mPaletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        if (paletteColor != Color.WHITE) {
            mPaletteColor = paletteColor;
        }
    }
}