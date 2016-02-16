package com.yydcdut.note.presenters.camera.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.model.camera.ICaptureModel;
import com.yydcdut.note.model.camera.IPreviewModel;
import com.yydcdut.note.model.camera.impl.CameraModelImpl;
import com.yydcdut.note.model.camera.impl2.Camera2ModelImpl;
import com.yydcdut.note.model.compare.SizeComparator;
import com.yydcdut.note.presenters.camera.IAdjustCameraPresenter;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.camera.IAdjustCameraView;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by yuyidong on 16/2/16.
 */
public class AdjustCameraPresenterImpl implements IAdjustCameraPresenter {
    private IAdjustCameraView mAdjustCameraView;
    /* CameraId */
    private String mCameraId = Const.CAMERA_BACK;

    private int mCurrentOrientationDegree = 0;
    private int mDeltaOrientationDegree = 90;

    private LocalStorageUtils mLocalStorageUtils;

    private ICameraModel mCameraModel;
    private ICameraSettingModel mCameraSettingModel;
    private IPreviewModel mPreviewModel;

    private AutoFitPreviewView.PreviewSurface mPreviewSurface;

    @Inject
    public AdjustCameraPresenterImpl(@ContextLife("Activity") Context context, LocalStorageUtils localStorageUtils,
                                     CameraModelImpl cameraModelImpl) {
        mLocalStorageUtils = localStorageUtils;
        mCurrentOrientationDegree = getCameraRotation();
        //todo 是不是使用Camera2
        if (AppCompat.AFTER_LOLLIPOP) {
            mCameraModel = new Camera2ModelImpl(context);
        } else {
            mCameraModel = cameraModelImpl;
        }
    }

    private int getCameraRotation() {
        switch (mCameraId) {
            case Const.CAMERA_BACK:
                return mLocalStorageUtils.getCameraBackRotation();
            default:
            case Const.CAMERA_FRONT:
                return mLocalStorageUtils.getCameraFrontRotation();
        }
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mAdjustCameraView = (IAdjustCameraView) iView;
        mCameraModel.openCamera(mCameraId, new ICameraModel.OnCameraOpenedCallback() {

            @Override
            public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                mPreviewModel = previewModel;
                mCameraSettingModel = cameraSettingModel;
                if (!isPictureSizeSaved()) {

                }
                savePictureSizes(mCameraId);
                Size previewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                mAdjustCameraView.setSize(previewSize.getHeight(), previewSize.getWidth());
            }

            @Override
            public void onError() {
            }
        }, getCameraRotation(), null);
    }

    /**
     * 获得最佳预览尺寸
     *
     * @param previewList
     * @return
     */
    private Size getSuitablePreviewSize(List<Size> previewList) {
        Size previewSize = null;
        Collections.sort(previewList, new SizeComparator());
        float screenScale = Utils.sScreenHeight / (float) Utils.sScreenWidth;
        for (Size preSize : previewList) {
            if (preSize.getWidth() * preSize.getHeight() > 1200000) {
                continue;
            }
            float preScale = preSize.getWidth() / (float) preSize.getHeight();
            //full ratio 如果全屏也是4：3的话，就先这样吧
            if (Math.abs(preScale - screenScale) < 0.03) {
                previewSize = preSize;
            }
        }
        if (previewSize == null) {
            previewSize = previewList.get(0);
        }
        return previewSize;
    }

    /**
     * 保存照片尺寸到SharedPreference
     *
     * @param currentCameraId
     */
    private Size savePictureSizes(String currentCameraId) {
        Size size = null;
        try {
            List<Size> list = mCameraSettingModel.getSupportPictureSizes();
            Collections.sort(list, new SizeComparator());
            size = list.get(list.size() - 1);
            mLocalStorageUtils.setPictureSizes(currentCameraId, list);
            mLocalStorageUtils.setPictureSize(currentCameraId, size);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            //mCameraSettingModel有可能为空
            e.printStackTrace();
        }
        return size;
    }

    private boolean isPictureSizeSaved() {
        boolean bool = true;
        try {
            bool = mLocalStorageUtils.getPictureSize(mCameraId) != null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bool;
    }

    @Override
    public void detachView() {
        closeCamera();
    }

    private void closeCamera() {
        if (mPreviewModel != null && mPreviewModel.isPreview()) {
            mPreviewModel.stopPreview();
        }
        if (mCameraModel.isOpen()) {
            mCameraModel.closeCamera();
        }
    }

    @Override
    public void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height) {
        mPreviewSurface = surface;
        if (!mCameraModel.isOpen()) {
            mCameraModel.openCamera(mCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            if (!isPictureSizeSaved()) {
                                savePictureSizes(mCameraId);
                            }
                            Size previewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(mPreviewSurface, mPreviewCallback, previewSize);
                        }

                        @Override
                        public void onError() {

                        }
                    }, getCameraRotation(), null);

        } else if (mCameraModel.isOpen() && mPreviewModel != null) {
            Size previewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
            mPreviewModel.startPreview(mPreviewSurface, mPreviewCallback, previewSize);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        closeCamera();
    }

    @Override
    public void switchCamera() {
        if (mCameraSettingModel != null && mCameraSettingModel.getNumberOfCameras() == 2) {
            if (Const.CAMERA_BACK.equals(mCameraId)) {
                mCameraId = Const.CAMERA_FRONT;
            } else {
                mCameraId = Const.CAMERA_BACK;
            }
            closeCamera();
            mCameraModel.openCamera(mCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            Size previewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(mPreviewSurface, mPreviewCallback, previewSize);
                        }

                        @Override
                        public void onError() {
                        }
                    }, getCameraRotation(), null);
        }
    }

    @Override
    public void clickBack() {

    }

    @Override
    public void clickRotation() {

    }

    private IPreviewModel.OnCameraPreviewCallback mPreviewCallback = new IPreviewModel.OnCameraPreviewCallback() {

        @Override
        public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {

        }

        @Override
        public void onPreviewError() {

        }
    };
}
