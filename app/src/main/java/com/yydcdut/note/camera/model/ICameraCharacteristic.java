package com.yydcdut.note.camera.model;


import com.yydcdut.note.camera.param.Size;

import java.util.List;

/**
 * Created by yuyidong on 15-4-9.
 */
public interface ICameraCharacteristic extends ICameraParams {
    public int getNumberOfCameras();

    //--------------------- Flash --------------------
    public boolean isFlashSupported();

    public List<Integer> getSupportedFlash();
    //--------------------- Flash --------------------

    public boolean isWhiteBalanceSupported();

    public List<Integer> getSupportedWhiteBalance();

    public List<Size> getPreviewSizes();

    public List<Size> getPictureSizes();

    public boolean isZoomSupported();

    public int getMaxZoom();

    public int getMaxExposureCompensation();

    public int getMinExposureCompensation();

}
