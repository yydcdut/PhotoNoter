package com.yydcdut.note.camera.model;

/**
 * Created by yuyidong on 15-4-13.
 */
public interface ICameraRequest extends ICameraParams {


    public void setFlash(int flashState);

    public void setPreviewSize(int width, int height);

    public void setPictureSize(int width, int height);

    public void setZoom(int value);

    public int getZoom();

    public void setExposureCompensation(int value);

    public void setWhiteBalance(int value);

    public void setDisplayOrientation(int degree);
}
