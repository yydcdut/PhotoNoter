package com.yydcdut.note.camera.model;

/**
 * Created by yyd on 15-4-13.
 */
public interface ICameraSetting extends ICameraCharacteristic, ICameraRequest {
    /**
     * 计算zoom
     *
     * @param firstZoomValue
     * @param firstCurrentSpan
     * @param currentCurrentSpan ScaleGesture
     * @return
     */
    public int calculateZoom(int firstZoomValue, float firstCurrentSpan, float currentCurrentSpan);

    /**
     * 保存参数到sharedPreference
     */
    public void saveParameters();

}
