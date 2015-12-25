package com.yydcdut.note.bean;

import android.graphics.Color;

import com.yydcdut.note.utils.FilePathUtils;

import java.util.Random;

/**
 * Created by yyd on 15-3-29.
 */
public class PhotoNote implements IObject {
    public static final int NO_ID = -1;
    /**
     * ID
     */
    private int id = NO_ID;
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
    private int categoryId;
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
                     long editedNoteTime, int categoryId) {
        this.id = NO_ID;
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryId = categoryId;
    }

    public PhotoNote(int id, String photoName, long createdPhotoTime, long editedPhotoTime,
                     String title, String content, long createdNoteTime,
                     long editedNoteTime, int categoryId) {
        this.id = id;
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryId = categoryId;
    }

    public String getSmallPhotoPathWithFile() {
        return "file://" + FilePathUtils.getSmallPath() + photoName;
    }

    public String getBigPhotoPathWithFile() {
        return "file://" + FilePathUtils.getPath() + photoName;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPaletteColor() {
        return mPaletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        if (paletteColor != Color.WHITE) {
            mPaletteColor = paletteColor;
        }
    }

    @Override
    public String toString() {
        return "PhotoNote{" +
                "id=" + id +
                ", photoName='" + photoName + '\'' +
                ", createdPhotoTime=" + createdPhotoTime +
                ", editedPhotoTime=" + editedPhotoTime +
                ", isSelected=" + isSelected +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdNoteTime=" + createdNoteTime +
                ", editedNoteTime=" + editedNoteTime +
                ", categoryId=" + categoryId +
                ", tag=" + tag +
                ", mPaletteColor=" + mPaletteColor +
                '}';
    }
}