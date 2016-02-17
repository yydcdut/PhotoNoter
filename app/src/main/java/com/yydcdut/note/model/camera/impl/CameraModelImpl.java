package com.yydcdut.note.model.camera.impl;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;

import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICaptureModel;
import com.yydcdut.note.model.camera.IPreviewModel;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraModelImpl implements ICameraModel {
    private static final String TAG = CameraModelImpl.class.getSimpleName();

    private Camera mCamera;

    private static final int STATE_CAMERA_CLOSE = 0;
    private static final int STATE_CAMERA_OPEN = 1;
    private static final int STATE_CAMERA_PREVIEW = 2;
    private static final int STATE_CAMERA_CAPTURE = 3;
    private int mCameraState = STATE_CAMERA_CLOSE;

    private CameraSettingModel mCameraSettingModel;
    private CameraFocusModel mCameraFocusModel;

    private PreviewModel mPreviewModel;
    private CaptureModel mCaptureModel;

    private PicturesPreviewCallback mPicturesPreviewCallback;

    @Singleton
    @Inject
    public CameraModelImpl() {
    }

    @Override
    public void openCamera(String id, OnCameraOpenedCallback callback, int orientation, Size pictureSize) {
        if (mCameraState != STATE_CAMERA_CLOSE) {
            return;
        }
        if (callback == null) {
            throw new IllegalArgumentException("");
        }
        if (mCameraState == STATE_CAMERA_CLOSE && mCamera == null) {
            int cameraId = 0;
            try {
                cameraId = Integer.parseInt(id);
            } catch (Exception e) {
                //String转int失败,id不是数字
            }
            mCamera = Camera.open(cameraId);
            mCamera.setDisplayOrientation(orientation);
            mCameraState = STATE_CAMERA_OPEN;
            mPreviewModel = new PreviewModel();
        } else {
            printLog("openCamera");
        }
        mCameraSettingModel = new CameraSettingModel(mCamera);
        if (pictureSize == null) {
            List<Size> list = mCameraSettingModel.getSupportPictureSizes();
            Collections.sort(list, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return -(rhs.getWidth() * rhs.getHeight() - lhs.getWidth() * lhs.getHeight());
                }
            });
            pictureSize = list.get(list.size() - 1);
        }
        mCameraSettingModel.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        callback.onOpen(mPreviewModel, mCameraSettingModel);
    }


    public class PreviewModel implements IPreviewModel {

        @Override
        public void startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                                 OnCameraPreviewCallback callback, Size previewSize) {
            if (callback == null) {
                throw new IllegalArgumentException("");
            }
            if (mCameraState == STATE_CAMERA_OPEN && mCamera != null) {
                try {
                    mCameraSettingModel.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
                    if (previewSurface.getSurfaceHolder() != null) {
                        mCamera.setPreviewDisplay(previewSurface.getSurfaceHolder());
                    } else {
                        mCamera.setPreviewTexture(previewSurface.getSurfaceTexture());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onPreviewError();
                    return;
                }
                mCamera.startPreview();
                mCaptureModel = new CaptureModel();
                mCameraState = STATE_CAMERA_PREVIEW;
            } else {
                printLog("startPreview");
            }
            mCameraFocusModel = new CameraFocusModel(mCamera, previewSize.getWidth(), previewSize.getHeight());
            callback.onPreview(mCaptureModel, mCameraFocusModel);
        }

        @Override
        public void continuePreview() {
            mCamera.startPreview();
            mCameraState = STATE_CAMERA_PREVIEW;
        }

        @Override
        public void stopPreview() {
            if (mCameraState == STATE_CAMERA_PREVIEW && mCamera != null) {
                mCamera.stopPreview();
                mCameraState = STATE_CAMERA_OPEN;
                mCameraFocusModel = null;
            } else {
                printLog("stopPreview");
            }
        }

        @Override
        public boolean isPreview() {
            return mCameraState == STATE_CAMERA_PREVIEW;
        }

    }

    public class CaptureModel implements ICaptureModel {

        @Override
        public long capture(PictureReturnCallback pictureReturnCallback) {
            long time = 0l;
            if (mCameraFocusModel != null && mCameraFocusModel.getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING
                    && mCameraState == STATE_CAMERA_PREVIEW && pictureReturnCallback != null) {
                time = System.currentTimeMillis();
                try {
//            mCamera.takePicture(sound ? new SoundCallBack() : null, null, new PictureCallBack(time, mCategoryId, ratio, isMirror));
                    mCamera.takePicture(null, null, new PictureCallBack(time, pictureReturnCallback));
                    mCameraState = STATE_CAMERA_CAPTURE;
                } catch (Exception e) {
                    pictureReturnCallback.onPictureTaken(false, null, 0l);
                }
            } else {
                YLog.i(TAG, "capture  focusState--->" + mCameraFocusModel.getFocusState() + "   CameraState--->" + mCameraState);
            }
            return time;
        }

        @Override
        public void startStillCapture(StillPictureReturnCallback stillPictureReturnCallback) {
            if (mCameraFocusModel != null && mCameraFocusModel.getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING
                    && mCameraState == STATE_CAMERA_PREVIEW) {
                if (mPicturesPreviewCallback == null) {
                    mPicturesPreviewCallback = new PicturesPreviewCallback(stillPictureReturnCallback);
                } else {
                    if (mPicturesPreviewCallback.stillPictureReturnCallback != stillPictureReturnCallback) {
                        mPicturesPreviewCallback = null;
                        mPicturesPreviewCallback = new PicturesPreviewCallback(stillPictureReturnCallback);
                    }
                }
                mCamera.setPreviewCallback(mPicturesPreviewCallback);
            } else {
                YLog.i(TAG, "capture  focusState--->" + mCameraFocusModel.getFocusState() + "   CameraState--->" + mCameraState);
            }
        }

        @Override
        public void stopStillCapture() {
            mCamera.setPreviewCallback(null);
            mPicturesPreviewCallback = null;
        }
    }

    private class PicturesPreviewCallback implements Camera.PreviewCallback {
        private ICaptureModel.StillPictureReturnCallback stillPictureReturnCallback;

        public PicturesPreviewCallback(ICaptureModel.StillPictureReturnCallback stillPictureReturnCallback) {
            this.stillPictureReturnCallback = stillPictureReturnCallback;
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (stillPictureReturnCallback != null) {
                stillPictureReturnCallback.onStillPictureTaken(ImageFormat.NV21, data, System.currentTimeMillis(),
                        mCameraSettingModel.getPreviewSize().getWidth(), mCameraSettingModel.getPreviewSize().getHeight());
            }
        }
    }

    @Override
    public void closeCamera() {
        if (mCameraState == STATE_CAMERA_OPEN && mCamera != null) {
            mCamera.release();
            mCamera = null;
            mCameraSettingModel = null;
            mCameraState = STATE_CAMERA_CLOSE;
        } else {
            printLog("closeCamera");
        }
    }

    @Override
    public boolean isOpen() {
        return mCameraState == STATE_CAMERA_OPEN;
    }

    @Override
    public int getCameraNumber(Context context) {
        return Camera.getNumberOfCameras();
    }

    /**
     * 创建jpeg图片回调数据对象
     */
    private class PictureCallBack implements Camera.PictureCallback {
        private long time;
        private ICaptureModel.PictureReturnCallback pictureReturnCallback;

        public PictureCallBack(long time, ICaptureModel.PictureReturnCallback pictureReturnCallback) {
            this.time = time;
            this.pictureReturnCallback = pictureReturnCallback;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (pictureReturnCallback != null) {
                pictureReturnCallback.onPictureTaken(true, data, time);
            }
//            //这里经常崩溃，做个延时处理
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    reStartPreview();
//                }
//            }, 300);
        }
    }

    private class SoundCallBack implements Camera.ShutterCallback {

        @Override
        public void onShutter() {
//            try {
//                AudioManager meng = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//                int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//                if (volume != 0) {
//                    if (mShootSound == null) {
//                        mShootSound = MediaPlayer.create(mContext, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
//                    }
//                    if (mShootSound != null) {
//                        mShootSound.start();
//                    }
//                }
//            } catch (Exception e) {
//                e.getStackTrace();
//            }
        }
    }

    private void printLog(String method) {
        StringBuilder sb = new StringBuilder(method + "  ");
        switch (mCameraState) {
            case STATE_CAMERA_OPEN:
                sb.append("STATE_CAMERA_OPEN");
                break;
            case STATE_CAMERA_PREVIEW:
                sb.append("STATE_CAMERA_PREVIEW");
                break;
            case STATE_CAMERA_CLOSE:
                sb.append("STATE_CAMERA_CLOSE");
                break;
            case STATE_CAMERA_CAPTURE:
                sb.append("STATE_CAMERA_CAPTURE");
                break;
        }
        YLog.i(TAG, "State--->" + sb.toString() + "  Camera == null --->" + (mCamera == null));
    }
}
