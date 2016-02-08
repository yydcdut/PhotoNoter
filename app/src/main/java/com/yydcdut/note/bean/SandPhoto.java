package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandPhoto implements IObject {
    public static final int ID_NULL = -1;

    private long id;
    private final long time;
    private final String cameraId;
    private final int categoryId;
    private final boolean isMirror;
    private final int ratio;
    private final String fileName;
    private final int size;
    private int imageFormat;

    private final SandExif mSandExif;

    public SandPhoto(long id, long time, String cameraId, int categoryId,
                     boolean isMirror, int ratio, String fileName, int size, int imageFormat,
                     SandExif sandExif) {
        this.id = id;
        this.time = time;
        this.cameraId = cameraId;
        this.categoryId = categoryId;
        this.isMirror = isMirror;
        this.ratio = ratio;
        this.fileName = fileName;
        this.size = size;
        this.imageFormat = imageFormat;
        this.mSandExif = sandExif;
    }

    public long getTime() {
        return time;
    }

    public String getCameraId() {
        return cameraId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMirror() {
        return isMirror;
    }

    public int getRatio() {
        return ratio;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    public int getImageFormat() {
        return imageFormat;
    }

    public SandExif getSandExif() {
        return mSandExif;
    }

    @Override
    public String toString() {
        return "SandPhoto{" +
                "id=" + id +
                ", time=" + time +
                ", cameraId='" + cameraId + '\'' +
                ", categoryId=" + categoryId +
                ", isMirror=" + isMirror +
                ", ratio=" + ratio +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", imageFormat=" + imageFormat +
                ", mSandExif=" + mSandExif +
                '}';
    }
}
