package com.yydcdut.note.model.camera.impl;

import android.hardware.Camera;

import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.model.camera.ICameraSettingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraSettingModel implements ICameraSettingModel {
    private Camera mCamera;

    public CameraSettingModel(Camera camera) {
        mCamera = camera;
    }

    private Camera.Parameters getParameters() {
        return mCamera.getParameters();
    }

    private void applyParameter(Camera.Parameters parameters) {
        mCamera.setParameters(parameters);
    }

    @Override
    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public boolean isFlashSupported() {
        List<String> list = getParameters().getSupportedFlashModes();
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Size> getSupportPreviewSizes() {
        List<Camera.Size> sizeList = getParameters().getSupportedPreviewSizes();
        List<Size> list = new ArrayList<>(sizeList.size());
        for (Camera.Size size : sizeList) {
            list.add(Size.translate(size.width, size.height));
        }
        return list;
    }

    @Override
    public List<Size> getSupportPictureSizes() {
        List<Camera.Size> sizeList = getParameters().getSupportedPictureSizes();
        List<Size> list = new ArrayList<>(sizeList.size());
        for (Camera.Size size : sizeList) {
            list.add(Size.translate(size.width, size.height));
        }
        return list;
    }

    @Override
    public void setPreviewSize(int width, int height) {
        Camera.Parameters parameters = getParameters();
        parameters.setPreviewSize(width, height);
        applyParameter(parameters);
    }

    @Override
    public void setPictureSize(int width, int height) {
        Camera.Parameters parameters = getParameters();
        parameters.setPictureSize(width, height);
        applyParameter(parameters);
    }

    @Override
    public Size getPictureSize() {
        Camera.Parameters parameters = getParameters();
        Camera.Size size = parameters.getPictureSize();
        return new Size(size.width, size.height);
    }

    @Override
    public Size getPreviewSize() {
        Camera.Parameters parameters = getParameters();
        Camera.Size size = parameters.getPreviewSize();
        return new Size(size.width, size.height);
    }
}
