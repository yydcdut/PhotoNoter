package com.yydcdut.note.model.camera.impl2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICaptureModel;
import com.yydcdut.note.model.camera.IPreviewModel;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.nio.ByteBuffer;
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
    private AutoFitPreviewView.PreviewSurface mPreviewSurface;

    private static final int STATE_CAMERA_CLOSE = 0;
    private static final int STATE_CAMERA_OPEN = 1;
    private static final int STATE_CAMERA_PREVIEW = 2;
    private static final int STATE_CAMERA_CAPTURE = 3;
    private int mCameraState = STATE_CAMERA_CLOSE;

    private Camera2SettingModel mCamera2SettingModel;
    private Camera2FocusModel mCamera2FocusModel;
    private Camera2PreviewModel mCamera2PreviewModel;
    private Camera2CaptureModel mCamera2CaptureModel;

    private ImageReader mJpgImageReader;
    private PictureCallBack mJpegPictureCallback;
    private ImageReader mYuvImageReader;

    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mSession;

    public Camera2ModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void openCamera(String id, OnCameraOpenedCallback callback, int orientation, Size pictureSize) {
        if (mCameraState != STATE_CAMERA_CLOSE) {
            return;
        }
        if (callback == null) {
            throw new IllegalArgumentException("");
        }
        if (mCameraManager == null) {
            mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        }
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
            mCameraManager.openCamera(id, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraState = STATE_CAMERA_OPEN;
                    mCameraDevice = camera;
                    mCamera2PreviewModel = new Camera2PreviewModel();
                    try {
                        mCamera2SettingModel = new Camera2SettingModel(mCameraManager.getCameraCharacteristics(id),
                                mCameraManager.getCameraIdList().length);
                        mJpgImageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(), ImageFormat.JPEG, 2);
                        mJpegPictureCallback = new PictureCallBack();
                        mJpgImageReader.setOnImageAvailableListener(mJpegPictureCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    callback.onOpen(mCamera2PreviewModel, mCamera2SettingModel);
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    mCameraState = STATE_CAMERA_CLOSE;
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    mCameraState = STATE_CAMERA_CLOSE;
                    camera.close();
                    mCameraDevice = null;
                    callback.onError();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    public class Camera2PreviewModel implements IPreviewModel {

        @Override
        public void startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                                 OnCameraPreviewCallback callback, Size previewSize) {
            mPreviewSurface = previewSurface;
            SurfaceTexture surfaceTexture = mPreviewSurface.getSurfaceTexture();
            SurfaceHolder surfaceHolder = mPreviewSurface.getSurfaceHolder();
            boolean success = true;
            Surface surface = null;
            if (surfaceTexture != null) {
                surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                surface = new Surface(surfaceTexture);
            } else {
                surfaceHolder.setFixedSize(previewSize.getWidth(), previewSize.getHeight());
                surface = surfaceHolder.getSurface();
            }
            try {
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mJpgImageReader.getSurface()), new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(CameraCaptureSession session) {
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

                        mCamera2FocusModel = new Camera2FocusModel();
                        mCamera2CaptureModel = new Camera2CaptureModel();
                        callback.onPreview(mCamera2CaptureModel, mCamera2FocusModel);
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        callback.onPreviewError();
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                success = false;
            }
            if (!success) {
                callback.onPreviewError();
            }
        }

        @Override
        public void continuePreview() {
            if (mSession != null && mCameraState == STATE_CAMERA_CAPTURE) {
                try {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    CaptureRequest previewRequest = mPreviewRequestBuilder.build();
                    mSession.setRepeatingRequest(previewRequest, mCameraCaptureSessionCaptureCallback4Preview, null);
                    mCameraState = STATE_CAMERA_PREVIEW;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
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
        public boolean isPreview() {
            return mCameraState == STATE_CAMERA_PREVIEW;
        }

        private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback4Preview = new CameraCaptureSession.CaptureCallback() {

            @Override
            public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                super.onCaptureProgressed(session, request, partialResult);
                mCamera2FocusModel.onCaptureProgressed(request, partialResult);
            }

            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                mCamera2FocusModel.onCaptureCompleted(request, result);
            }

        };
    }

    public class Camera2CaptureModel implements ICaptureModel {

        @Override
        public long capture(PictureReturnCallback pictureReturnCallback) {
            long time = 0l;
            if (mCamera2FocusModel != null && mCamera2FocusModel.getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING
                    && mCameraState == STATE_CAMERA_PREVIEW && pictureReturnCallback != null) {
                time = System.currentTimeMillis();
                if (mJpegPictureCallback.pictureReturnCallback == null ||
                        mJpegPictureCallback.pictureReturnCallback != pictureReturnCallback) {
                    mJpegPictureCallback.pictureReturnCallback = pictureReturnCallback;
                }
                mJpegPictureCallback.time = time;
                CaptureRequest.Builder captureBuilder;
                try {
                    captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureBuilder.addTarget(mJpgImageReader.getSurface());
                    captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                // Orientation
//                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                    mSession.stopRepeating();
                    mSession.capture(captureBuilder.build(), mCameraCaptureSessionCaptureCallback4Capture, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            return time;
        }

        @Override
        public void startStillCapture(StillPictureReturnCallback stillPictureReturnCallback) {

        }

        @Override
        public void stopStillCapture() {

        }

        private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback4Capture = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
                mCameraState = STATE_CAMERA_CAPTURE;
            }

            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
            }
        };
    }

    class PictureCallBack implements ImageReader.OnImageAvailableListener {
        private long time;
        private ICaptureModel.PictureReturnCallback pictureReturnCallback;

        @Override
        public void onImageAvailable(ImageReader reader) {
            if (pictureReturnCallback != null) {
                ByteBuffer buffer = reader.acquireNextImage().getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                pictureReturnCallback.onPictureTaken(true, bytes, time);
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
        if (null != mJpgImageReader) {
            mJpgImageReader.close();
            mJpgImageReader = null;
        }
    }

    @Override
    public boolean isOpen() {
        return mCameraState == STATE_CAMERA_OPEN;
    }
}
