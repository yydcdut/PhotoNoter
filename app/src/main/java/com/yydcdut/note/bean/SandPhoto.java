package com.yydcdut.note.bean;

import java.util.Arrays;

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandPhoto implements IObject {
    public static final int ID_NULL = -1;

    private long id;
    private final byte[] data;
    private final long time;
    private final String cameraId;
    private final String category;
    private final boolean isMirror;
    private final int ratio;

    private final SandExif mSandExif;

    public SandPhoto(long id, byte[] data, long time, String cameraId, String category, boolean isMirror, int ratio, SandExif sandExif) {
        this.id = id;
        this.data = data;
        this.time = time;
        this.cameraId = cameraId;
        this.category = category;
        this.isMirror = isMirror;
        this.ratio = ratio;
        this.mSandExif = sandExif;
    }

    public byte[] getData() {
        return data;
    }

    public long getTime() {
        return time;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getCategory() {
        return category;
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

    public SandExif getSandExif() {
        return mSandExif;
    }

    @Override
    public String toString() {
        return "SandPhoto{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                ", time=" + time +
                ", cameraId='" + cameraId + '\'' +
                ", category='" + category + '\'' +
                ", isMirror=" + isMirror +
                ", ratio=" + ratio +
                ", mSandExif=" + mSandExif +
                '}';
    }
}
