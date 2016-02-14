package com.yydcdut.note.model.camera.impl2;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.view.SurfaceHolder;

import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/2/10.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2SettingModel implements ICameraSettingModel {
    private int mCameraNumber;
    private CameraCharacteristics mCameraCharacteristics;


    private AutoFitPreviewView.PreviewSurface mPreviewSurface;
    private int mPreviewWidth = -1;
    private int mPreviewHeight = -1;

    public Camera2SettingModel(CameraCharacteristics cameraCharacteristics, int cameraNumber) {
        mCameraCharacteristics = cameraCharacteristics;
        mCameraNumber = cameraNumber;
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

    public void setPreviewSurface(AutoFitPreviewView.PreviewSurface previewSurface) {
        mPreviewSurface = previewSurface;
        if (mPreviewWidth != -1 && mPreviewHeight != -1) {
            SurfaceTexture surfaceTexture = mPreviewSurface.getSurfaceTexture();
            SurfaceHolder surfaceHolder = mPreviewSurface.getSurfaceHolder();
            if (surfaceTexture != null) {
                surfaceTexture.setDefaultBufferSize(mPreviewWidth, mPreviewHeight);
            } else if (surfaceHolder != null) {
                surfaceHolder.setFixedSize(mPreviewWidth, mPreviewHeight);
            }
        }
    }

    @Override
    public void setPreviewSize(int width, int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;
        if (mPreviewSurface == null) {
            return;
        }
        SurfaceTexture surfaceTexture = mPreviewSurface.getSurfaceTexture();
        SurfaceHolder surfaceHolder = mPreviewSurface.getSurfaceHolder();
        if (surfaceTexture == null) {
            surfaceTexture.setDefaultBufferSize(mPreviewWidth, mPreviewHeight);
        } else if (surfaceHolder == null) {
            surfaceHolder.setFixedSize(mPreviewWidth, mPreviewHeight);
        }
    }

    @Override
    public void setPictureSize(int width, int height) {

    }

    @Override
    public Size getPictureSize() {
        return null;
    }

    @Override
    public Size getPreviewSize() {
        return new Size(mPreviewWidth, mPreviewHeight);
    }
}
