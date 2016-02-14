package com.yydcdut.note.model.camera.impl2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.util.Arrays;

/**
 * Created by yuyidong on 16/2/3.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2ModelImpl implements ICameraModel {
    private static final String TAG = Camera2ModelImpl.class.getSimpleName();

    private Context mContext;

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;

    private static final int STATE_CAMERA_CLOSE = 0;
    private static final int STATE_CAMERA_OPEN = 1;
    private static final int STATE_CAMERA_PREVIEW = 2;
    private static final int STATE_CAMERA_CAPTURE = 3;
    private int mCameraState = STATE_CAMERA_CLOSE;

    private String mCurrentCameraId;

    private Camera2SettingModel mCamera2SettingModel;

    private ImageReader mJpgImageReader;
    private ImageReader mYuvImageReader;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mSession;

    public Camera2ModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public ICameraSettingModel openCamera(String id, int orientation) {
        if (mCameraState != STATE_CAMERA_CLOSE) {
            return mCamera2SettingModel;
        }
        boolean success = true;
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mCurrentCameraId = id;
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                return null;
                YLog.i("yuyidong", "no permission");
            }
            mCameraManager.openCamera(id, mCameraDeviceStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            success = false;
        }
        if (mCamera2SettingModel == null && success) {
            try {
                ;
                mCamera2SettingModel = new Camera2SettingModel(mCameraManager.getCameraCharacteristics(id),
                        mCameraManager.getCameraIdList().length);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }
        return mCamera2SettingModel;
    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            mCameraState = STATE_CAMERA_OPEN;
            mCameraDevice = camera;
            YLog.i("yuyidong", "CameraDevice.StateCallback onOpened");
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraState = STATE_CAMERA_CLOSE;
            camera.close();
            mCameraDevice = null;
            YLog.i("yuyidong", "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            mCameraState = STATE_CAMERA_CLOSE;
            camera.close();
            mCameraDevice = null;
            YLog.i("yuyidong", "CameraDevice.StateCallback onError error--->" + error);
        }
    };

    @Override
    public ICameraSettingModel reopenCamera(String id, int orientation) {
        return null;
    }

    @Override
    public ICameraFocus startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                                     int previewWidth, int previewHeight) {
        mCamera2SettingModel.setPreviewSurface(previewSurface);
        SurfaceTexture surfaceTexture = previewSurface.getSurfaceTexture();
        SurfaceHolder surfaceHolder = previewSurface.getSurfaceHolder();
        boolean success = true;
        if (surfaceTexture != null) {
            try {
                Surface surface = new Surface(surfaceTexture);
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                mCameraDevice.createCaptureSession(Arrays.asList(surface), mCameraCaptureSessionStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                success = false;
            }
        } else if (surfaceHolder != null) {
            try {
                Surface surface = surfaceHolder.getSurface();
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                mCameraDevice.createCaptureSession(Arrays.asList(surface), mCameraCaptureSessionStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                success = false;
            }
        } else {
            success = false;
        }
        if (success) {
            //// TODO: 16/2/11
            return null;
        } else {
            return null;
        }
    }

    private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            YLog.i("yuyidong", "CameraCaptureSession.StateCallback  onConfigured");
            if (null == mCameraDevice) {
                return;
            }
            mSession = session;
            try {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                CaptureRequest previewRequest = mPreviewRequestBuilder.build();
                mSession.setRepeatingRequest(previewRequest, mCameraCaptureSessionCaptureCallback4Preview, null);
                mCameraState = STATE_CAMERA_PREVIEW;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            YLog.i("yuyidong", "CameraCaptureSession.StateCallback  onConfigureFailed");
        }
    };

    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback4Preview = new CameraCaptureSession.CaptureCallback() {

    };

    @Override
    public long capture(PictureReturnCallback pictureReturnCallback) {
        return 0;
    }

    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback4Capture = new CameraCaptureSession.CaptureCallback() {

    };

    @Override
    public void startStillCapture(StillPictureReturnCallback stillPictureReturnCallback) {

    }

    @Override
    public void stopStillCapture() {

    }

    @Override
    public void restartPreview() {

    }

    @Override
    public void stopPreview() {
        if (mSession != null) {
            try {
                mSession.stopRepeating();
                mCameraState = STATE_CAMERA_OPEN;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeCamera() {
        if (null != mSession) {
            mSession.close();
            mSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        mCameraManager = null;
        mCameraState = STATE_CAMERA_CLOSE;
//        if (null != mImageReader) {
//            mImageReader.close();
//            mImageReader = null;
//        }
    }

    @Override
    public boolean isOpen() {
        return mCameraState == STATE_CAMERA_OPEN;
    }

    @Override
    public boolean isPreview() {
        return mCameraState == STATE_CAMERA_PREVIEW;
    }
}
