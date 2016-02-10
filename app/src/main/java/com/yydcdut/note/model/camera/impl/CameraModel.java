package com.yydcdut.note.model.camera.impl;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.io.IOException;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraModel implements ICameraModel {
    private static final String TAG = CameraModel.class.getSimpleName();

    private Camera mCamera;

    private static final int STATE_CAMERA_CLOSE = 0;
    private static final int STATE_CAMERA_OPEN = 1;
    private static final int STATE_CAMERA_PREVIEW = 2;
    private static final int STATE_CAMERA_CAPTURE = 3;
    private int mCameraState = STATE_CAMERA_CLOSE;

    private String mCurrentCameraId;

    private CameraSettingModel mCameraSettingModel;
    private CameraFocusModel mCameraFocusModel;

    private PicturesPreviewCallback mPicturesPreviewCallback;

    private CameraModel() {
    }

    public static CameraModel getInstance() {
        return CameraInstance.INSTANCE;
    }

    private static class CameraInstance {
        private static final CameraModel INSTANCE = new CameraModel();
    }

    @Override
    public ICameraSettingModel openCamera(String id, int orientation) {
        if (mCameraState == STATE_CAMERA_CLOSE && mCamera == null) {
            int cameraId = 0;
            try {
                cameraId = Integer.parseInt(id);
            } catch (Exception e) {
                //String转int失败,id不是数字
            }
            mCurrentCameraId = String.valueOf(cameraId);
            mCamera = Camera.open(cameraId);
            mCamera.setDisplayOrientation(orientation);
            mCameraState = STATE_CAMERA_OPEN;
        } else {
            printLog("openCamera");
        }
        if (mCameraSettingModel == null) {
            mCameraSettingModel = new CameraSettingModel(mCamera);
        }
        return mCameraSettingModel;
    }

    @Override
    public ICameraSettingModel reopenCamera(String id, int orientation) {
        if (id.equals(mCurrentCameraId)) {
            printLog("reopenCamera   id == mCurrentCameraId");
            return mCameraSettingModel;
        }
        mCurrentCameraId = id;
        if (isPreview()) {
            stopPreview();
        }
        if (isOpen()) {
            closeCamera();
        }
        return openCamera(mCurrentCameraId, orientation);
    }

    @Override
    public ICameraFocus startPreview(AutoFitPreviewView.PreviewSurface previewSurface
            , int previewViewWidth, int previewViewHeight) {
        if (mCameraState == STATE_CAMERA_OPEN && mCamera != null) {
            try {
                if (previewSurface.getSurfaceHolder() != null) {
                    mCamera.setPreviewDisplay(previewSurface.getSurfaceHolder());
                } else {
                    mCamera.setPreviewTexture(previewSurface.getSurfaceTexture());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            mCameraState = STATE_CAMERA_PREVIEW;
        } else {
            printLog("startPreview");
        }
        if (mCameraFocusModel == null) {
            mCameraFocusModel = new CameraFocusModel(mCamera, previewViewWidth, previewViewHeight);
        }
        return mCameraFocusModel;
    }

    @Override
    public long capture(PictureReturnCallback pictureReturnCallback) {
        long time = 0l;
        if (mCameraFocusModel != null && mCameraFocusModel.getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING
                && mCameraState == STATE_CAMERA_PREVIEW && pictureReturnCallback != null) {
            time = System.currentTimeMillis();
            try {
//            mCamera.takePicture(sound ? new SoundCallBack() : null, null, new PictureCallBack(time, mCategoryId, ratio, isMirror));
                mCamera.takePicture(true ? new SoundCallBack() : null, null, new PictureCallBack(time, pictureReturnCallback));
            } catch (Exception e) {
                if (pictureReturnCallback != null) {
                    pictureReturnCallback.onPictureTaken(false, null, 0l);
                }
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
//            try {
////            mCamera.takePicture(sound ? new SoundCallBack() : null, null, new PictureCallBack(time, mCategoryId, ratio, isMirror));
//                mCamera.takePicture(true ? new SoundCallBack() : null, null, new PictureCallBack(time, pictureReturnCallback));
//            } catch (RuntimeException e) {
//                if (pictureReturnCallback != null) {
//                    pictureReturnCallback.onPictureTaken(false, null, 0l);
//                }
//            }
        } else {
            YLog.i(TAG, "capture  focusState--->" + mCameraFocusModel.getFocusState() + "   CameraState--->" + mCameraState);
        }
    }

    @Override
    public void stopStillCapture() {
        mCamera.setPreviewCallback(null);
        mPicturesPreviewCallback = null;
    }

    private class PicturesPreviewCallback implements Camera.PreviewCallback {
        private StillPictureReturnCallback stillPictureReturnCallback;

        public PicturesPreviewCallback(StillPictureReturnCallback stillPictureReturnCallback) {
            this.stillPictureReturnCallback = stillPictureReturnCallback;
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (stillPictureReturnCallback != null) {
                long time = System.currentTimeMillis();
                stillPictureReturnCallback.onStillPictureTaken(ImageFormat.NV21, data, time);
            }
        }
    }

    @Override
    public void restartPreview() {
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
    public boolean isPreview() {
        return mCameraState == STATE_CAMERA_PREVIEW;
    }

    /**
     * 创建jpeg图片回调数据对象
     */
    private class PictureCallBack implements Camera.PictureCallback {
        private long time;
        private PictureReturnCallback pictureReturnCallback;

        public PictureCallBack(long time, PictureReturnCallback pictureReturnCallback) {
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
