package com.yydcdut.note.model.camera;

import com.yydcdut.note.camera.param.Size;

/**
 * Created by yuyidong on 16/2/6.
 */
public interface ICameraRequest extends ICameraParams {
    void setPreviewSize(int width, int height);

    void setPictureSize(int width, int height);

    Size getPictureSize();

    Size getPreviewSize();
}
