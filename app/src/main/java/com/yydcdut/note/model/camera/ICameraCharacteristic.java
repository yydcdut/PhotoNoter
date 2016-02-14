package com.yydcdut.note.model.camera;

import com.yydcdut.note.camera.param.Size;

import java.util.List;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraCharacteristic extends ICameraParams {
    public int getNumberOfCameras();

    public boolean isFlashSupported();

    List<Size> getSupportPreviewSizes();

    List<Size> getSupportPictureSizes();
}
