package com.yydcdut.note.camera.model.camera;

import android.hardware.Camera;

import com.yydcdut.note.camera.model.ICameraParams;
import com.yydcdut.note.camera.model.ICameraSetting;
import com.yydcdut.note.camera.param.ParamsConvert;
import com.yydcdut.note.camera.param.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyd on 15-4-10.
 */
public class SettingModel implements ICameraSetting {

    private Camera mCamera;

    public SettingModel(Camera camera) {
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
    public List<Integer> getSupportedFlash() {
        List<String> stringList = getParameters().getSupportedFlashModes();
        if (stringList == null) {
            return new ArrayList<>(0);
        }
        List<Integer> cameraList = new ArrayList<>(stringList.size());
        for (String s : stringList) {
            int flash = ParamsConvert.convertFlash(s);
            if (flash == -1) {
                break;
            } else {
                cameraList.add(flash);
            }
        }
        return cameraList;
    }

    @Override
    public boolean isWhiteBalanceSupported() {
        List<String> list = getParameters().getSupportedWhiteBalance();
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Integer> getSupportedWhiteBalance() {
        List<String> stringList = getParameters().getSupportedWhiteBalance();
        if (stringList == null) {
            return new ArrayList<>(0);
        }
        List<Integer> cameraList = new ArrayList<>(stringList.size());
        for (String s : stringList) {
            int wb = ParamsConvert.convertWhiteBalance(s);
            if (wb == -1) {
                break;
            } else {
                cameraList.add(wb);
            }
        }
        return cameraList;
    }

    @Override
    public List<Size> getPreviewSizes() {
        List<Camera.Size> sizeList = getParameters().getSupportedPreviewSizes();
        List<Size> list = new ArrayList<>(sizeList.size());
        for (Camera.Size size : sizeList) {
            list.add(Size.translate(size.width, size.height));
        }
        return list;
    }

    @Override
    public List<Size> getPictureSizes() {
        List<Camera.Size> sizeList = getParameters().getSupportedPictureSizes();
        List<Size> list = new ArrayList<>(sizeList.size());
        for (Camera.Size size : sizeList) {
            list.add(Size.translate(size.width, size.height));
        }
        return list;
    }

    @Override
    public boolean isZoomSupported() {
        return getParameters().isZoomSupported();
    }

    @Override
    public int getMaxExposureCompensation() {
        Camera.Parameters parameters = getParameters();
        return parameters.getMaxExposureCompensation();
    }

    @Override
    public int getMinExposureCompensation() {
        Camera.Parameters parameters = getParameters();
        return parameters.getMinExposureCompensation();
    }

    @Override
    public void setFlash(int flashState) {
        Camera.Parameters parameters = getParameters();
        switch (flashState) {
            case FLASH_OFF:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case FLASH_AUTO:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case FLASH_ON:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                break;
            case FLASH_TORCH:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            default:
                break;
        }
        applyParameter(parameters);
    }

    @Override
    public int getFlash() {
        Camera.Parameters parameters = getParameters();
        switch (parameters.getFlashMode()) {
            case Camera.Parameters.FLASH_MODE_OFF:
                return FLASH_OFF;
            case Camera.Parameters.FLASH_MODE_AUTO:
                return FLASH_AUTO;
            case Camera.Parameters.FLASH_MODE_ON:
                return FLASH_ON;
            case Camera.Parameters.FLASH_MODE_TORCH:
                return FLASH_TORCH;
        }
        return 0;
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
    public void setZoom(int value) {
        Camera.Parameters parameters = getParameters();
        parameters.setZoom(value);
        applyParameter(parameters);
    }

    @Override
    public int getZoom() {
        Camera.Parameters parameters = getParameters();
        return parameters.getZoom();
    }

    @Override
    public int getMaxZoom() {
        Camera.Parameters parameters = getParameters();
        return parameters.getMaxZoom();
    }

    @Override
    public void setExposureCompensation(int value) {
        Camera.Parameters parameters = getParameters();
        parameters.setExposureCompensation(value);
        applyParameter(parameters);
    }

    @Override
    public void setWhiteBalance(int value) {
        Camera.Parameters parameters = getParameters();
        switch (value) {
            case ICameraParams.WHITE_BALANCE_AUTO:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                break;
            case ICameraParams.WHITE_BALANCE_WARM_FLUORESCENT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT);
                break;
            case ICameraParams.WHITE_BALANCE_TWILIGHT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_TWILIGHT);
                break;
            case ICameraParams.WHITE_BALANCE_SHADE:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_SHADE);
                break;
            case ICameraParams.WHITE_BALANCE_INCANDESCENT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
                break;
            case ICameraParams.WHITE_BALANCE_CLOUDY_DAYLIGHT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
                break;
            case ICameraParams.WHITE_BALANCE_DAYLIGHT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
                break;
            case ICameraParams.WHITE_BALANCE_FLUORESCENT:
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
                break;
        }
        applyParameter(parameters);
    }

    @Override
    public int getWhiteBalance() {
        Camera.Parameters parameters = getParameters();
        switch (parameters.getWhiteBalance()) {
            case Camera.Parameters.WHITE_BALANCE_AUTO:
                return ICameraParams.WHITE_BALANCE_AUTO;
            case Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT:
                return ICameraParams.WHITE_BALANCE_WARM_FLUORESCENT;
            case Camera.Parameters.WHITE_BALANCE_TWILIGHT:
                return ICameraParams.WHITE_BALANCE_TWILIGHT;
            case Camera.Parameters.WHITE_BALANCE_SHADE:
                return ICameraParams.WHITE_BALANCE_SHADE;
            case Camera.Parameters.WHITE_BALANCE_INCANDESCENT:
                return ICameraParams.WHITE_BALANCE_INCANDESCENT;
            case Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT:
                return ICameraParams.WHITE_BALANCE_CLOUDY_DAYLIGHT;
            case Camera.Parameters.WHITE_BALANCE_DAYLIGHT:
                return ICameraParams.WHITE_BALANCE_DAYLIGHT;
            case Camera.Parameters.WHITE_BALANCE_FLUORESCENT:
                return ICameraParams.WHITE_BALANCE_FLUORESCENT;
        }
        return 0;
    }


    @Override
    public int calculateZoom(int firstZoomValue, float firstCurrentSpan, float currectCurrentSpan) {
        if (!isZoomSupported()) {
            return -1;
        }
        List<Integer> zoomRatios = getParameters().getZoomRatios();
        int scaleInt = (int) ((currectCurrentSpan / firstCurrentSpan) * zoomRatios.get(firstZoomValue));
        int size = zoomRatios.size();
        for (int index = 0; index < size; index++) {
            int value = zoomRatios.get(index);
            if (scaleInt <= value) {
                return index;
            }
        }
        return size - 1;
    }

    @Override
    public void saveParameters() {

    }

    @Override
    public void setDisplayOrientation(int degree) {
        mCamera.setDisplayOrientation(degree);
    }

}
