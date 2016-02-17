package com.yydcdut.note.model.camera;

/**
 * Created by yuyidong on 16/2/6.
 */
public interface ICameraRequest extends ICameraParams {

    void setDisplayOrientation(int degree);

    void setFlash(int flashState);

    int getFlash();
}
