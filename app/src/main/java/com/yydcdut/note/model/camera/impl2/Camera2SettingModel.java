package com.yydcdut.note.model.camera.impl2;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;

import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.utils.camera.param.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/2/10.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2SettingModel implements ICameraSettingModel {
    private int mCameraNumber;
    private CameraCharacteristics mCameraCharacteristics;

    private CaptureRequest.Builder mBuilder;

    public Camera2SettingModel(CameraCharacteristics cameraCharacteristics, int cameraNumber) {
        mCameraCharacteristics = cameraCharacteristics;
        mCameraNumber = cameraNumber;
    }

    public void setCaptureRequestBuilder(CaptureRequest.Builder builder) {
        mBuilder = builder;
    }

    @Override
    public int getNumberOfCameras() {
        return mCameraNumber;
    }

    @Override
    public boolean isFlashSupported() {
        return mCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    @Override
    public List<Size> getSupportPreviewSizes() {
        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return null;
        }
        android.util.Size[] supports = map.getOutputSizes(SurfaceTexture.class);
        List<Size> sizeList = new ArrayList<>(supports.length);
        for (android.util.Size size : supports) {
            sizeList.add(Size.translate(size.getWidth(), size.getHeight()));

        }
        return sizeList;
    }

    @Override
    public List<Size> getSupportPictureSizes() {
        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return null;
        }
        android.util.Size[] supports = map.getOutputSizes(ImageFormat.JPEG);
        List<Size> sizeList = new ArrayList<>(supports.length);
        for (android.util.Size size : supports) {
            sizeList.add(Size.translate(size.getWidth(), size.getHeight()));

        }
        return sizeList;
    }

    public List<Size> getSupportYUV420888Sizes() {
        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) {
            return null;
        }
        android.util.Size[] supports = map.getOutputSizes(ImageFormat.YUV_420_888);
        List<Size> sizeList = new ArrayList<>(supports.length);
        for (android.util.Size size : supports) {
            sizeList.add(Size.translate(size.getWidth(), size.getHeight()));

        }
        return sizeList;
    }

    @Override
    public void setDisplayOrientation(int degree) {

    }

    @Override
    public void setFlash(int flashState) {
        switch (flashState) {
            case FLASH_OFF:
                mBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                break;
            case FLASH_AUTO:
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                break;
            case FLASH_ON:
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                break;
            default:
                break;
        }
        if (mOnParameterChangedListener != null) {
            mOnParameterChangedListener.onChanged(mBuilder);
        }
    }

    @Override
    public int getFlash() {
        switch (mBuilder.get(CaptureRequest.CONTROL_AE_MODE)) {
            case CaptureRequest.CONTROL_AE_MODE_OFF:
                return FLASH_OFF;
            case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                return FLASH_AUTO;
            case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                return FLASH_ON;

        }
        return FLASH_OFF;
    }

    private OnParameterChangedListener mOnParameterChangedListener;

    public void setOnParameterChangedListener(OnParameterChangedListener onParameterChangedListener) {
        mOnParameterChangedListener = onParameterChangedListener;
    }

    public interface OnParameterChangedListener {
        void onChanged(CaptureRequest.Builder builder);
    }
}
