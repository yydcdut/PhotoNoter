package com.yydcdut.note.model.camera;

import com.yydcdut.note.utils.camera.param.Size;

import java.util.List;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraCharacteristic extends ICameraParams {
    int getNumberOfCameras();

    boolean isFlashSupported();

    List<Size> getSupportPreviewSizes();

    List<Size> getSupportPictureSizes();

    boolean isZoomSupported();

    boolean isFocusSupported();

    int getMaxZoom();

    int getMaxExposureCompensation();

    int getMinExposureCompensation();
}
