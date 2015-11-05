package com.yydcdut.note.camera.model;

import com.yydcdut.note.camera.param.Size;

/**
 * Created by yuyidong on 15-4-13.
 */
public interface ICameraRequest extends ICameraParams {


    public void setFlash(int flashState);

    public int getFlash();

    public void setPreviewSize(int width, int height);

    public void setPictureSize(int width, int height);

    public Size getPictureSize();

    public void setZoom(int value);

    public int getZoom();

    public void setExposureCompensation(int value);

    public void setWhiteBalance(int value);

    public int getWhiteBalance();

    public void setDisplayOrientation(int degree);
}
