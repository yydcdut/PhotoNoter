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
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICaptureModel;
import com.yydcdut.note.model.camera.IPreviewModel;
import com.yydcdut.note.model.compare.SizeComparator;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yuyidong on 16/2/3.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2ModelImpl implements ICameraModel, Camera2SettingModel.OnParameterChangedListener,
        Camera2FocusModel.OnParameterChangedListener {
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
    private PictureCallback mJpegPictureCallback;
    private ImageReader mYuvImageReader;
    private PicturesPreviewCallback mPicturesPreviewCallback;

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
                        mCamera2SettingModel.setOnParameterChangedListener(Camera2ModelImpl.this);
                        Size picSize = null;
                        if (pictureSize == null) {
                            List<Size> list = mCamera2SettingModel.getSupportPictureSizes();
                            Collections.sort(list, new SizeComparator());
                            picSize = list.get(list.size() - 1);
                        } else {
                            picSize = pictureSize;
                        }
                        mJpgImageReader = ImageReader.newInstance(picSize.getWidth(), picSize.getHeight(), ImageFormat.JPEG, 2);
                        mJpegPictureCallback = new PictureCallback();
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

    private Surface getSurface(Size previewSize) {
        SurfaceTexture surfaceTexture = mPreviewSurface.getSurfaceTexture();
        SurfaceHolder surfaceHolder = mPreviewSurface.getSurfaceHolder();
        Surface surface = null;
        if (surfaceTexture != null) {
            if (previewSize != null) {
                surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            }
            surface = new Surface(surfaceTexture);
        } else {
            if (previewSize != null) {
                surfaceHolder.setFixedSize(previewSize.getWidth(), previewSize.getHeight());
            }
            surface = surfaceHolder.getSurface();
        }
        return surface;
    }

    private Size getYuvSize(Size previewSize) {
        List<Size> sizeList = mCamera2SettingModel.getSupportYUV420888Sizes();
        Size returnSize = null;
        for (Size size : sizeList) {
            if (size.equals(previewSize)) {
                returnSize = previewSize;
                break;
            }
        }
        if (returnSize == null) {
            for (Size size : sizeList) {
                if (size.getWidth() == previewSize.getWidth()) {
                    returnSize = previewSize;
                    break;
                }
                if (size.getHeight() == previewSize.getHeight()) {
                    returnSize = previewSize;
                    break;
                }
            }
        }
        if (returnSize == null) {
            returnSize = sizeList.get(sizeList.size() / 2);
        }
        return returnSize;
    }

    @Override
    public void onChanged(CaptureRequest.Builder builder) {
        if (mCameraDevice != null && mSession != null && builder != null) {
            try {
                mSession.setRepeatingRequest(builder.build(), mCameraCaptureSessionCaptureCallback4Preview, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public class Camera2PreviewModel implements IPreviewModel {

        @Override
        public void startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                                 OnCameraPreviewCallback callback, Size previewSize) {
            mPreviewSurface = previewSurface;
            boolean success = true;
            Surface surface = getSurface(previewSize);
            try {
                Size yuvSize = getYuvSize(previewSize);
                mYuvImageReader = ImageReader.newInstance(yuvSize.getWidth(), yuvSize.getHeight(), ImageFormat.YUV_420_888, 10);
                mPicturesPreviewCallback = new PicturesPreviewCallback();
                mYuvImageReader.setOnImageAvailableListener(mPicturesPreviewCallback, null);
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                mCamera2SettingModel.setCaptureRequestBuilder(mPreviewRequestBuilder);
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mJpgImageReader.getSurface(), mYuvImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {

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

                                mCamera2FocusModel = new Camera2FocusModel(mCamera2SettingModel.isFocusSupported(),
                                        mPreviewRequestBuilder, mCamera2SettingModel.getActiveArraySize());
                                mCamera2FocusModel.setOnParameterChangedListener(Camera2ModelImpl.this);
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
//            if (mCamera2FocusModel != null && mCamera2FocusModel.getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING
//                    && mCameraState == STATE_CAMERA_PREVIEW) {
//                if (mPicturesPreviewCallback == null) {
//                    mPicturesPreviewCallback = new PicturesPreviewCallback();
//                    mPicturesPreviewCallback.stillPictureReturnCallback = stillPictureReturnCallback;
//                } else {
//                    if (mPicturesPreviewCallback.stillPictureReturnCallback != stillPictureReturnCallback) {
//                        mPicturesPreviewCallback.stillPictureReturnCallback = stillPictureReturnCallback;
//                    }
//                }
//                CaptureRequest.Builder stillCaptureBuilder;
//                try {
//                    stillCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//                    stillCaptureBuilder.addTarget(mYuvImageReader.getSurface());
////                    Surface surface = getSurface(null);
////                    stillCaptureBuilder.addTarget(surface);
//                    mSession.stopRepeating();
//                    mSession.setRepeatingRequest(stillCaptureBuilder.build(), null, null);
//                } catch (CameraAccessException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                YLog.i(TAG, "capture  focusState--->" + mCamera2FocusModel.getFocusState() + "   CameraState--->" + mCameraState);
//            }
        }

        @Override
        public void stopStillCapture() {
//            try {
//                mSession.stopRepeating();
//                mSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCameraCaptureSessionCaptureCallback4Preview, null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
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

    class PictureCallback implements ImageReader.OnImageAvailableListener {
        private long time;
        private ICaptureModel.PictureReturnCallback pictureReturnCallback;

        @Override
        public void onImageAvailable(ImageReader reader) {
            if (pictureReturnCallback != null) {
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                pictureReturnCallback.onPictureTaken(true, bytes, time);
                image.close();
            }
        }
    }

    class PicturesPreviewCallback implements ImageReader.OnImageAvailableListener {
        private ICaptureModel.StillPictureReturnCallback stillPictureReturnCallback;

        @Override
        public void onImageAvailable(ImageReader reader) {
            if (stillPictureReturnCallback != null) {
                Image image = reader.acquireNextImage();
                stillPictureReturnCallback.onStillPictureTaken(ImageFormat.NV21, convertYUV420ToN21(image),
                        System.currentTimeMillis(), reader.getWidth(), reader.getHeight());
                image.close();
            }
        }
    }

    private byte[] convertYUV420ToN21(Image imgYUV420) {
        byte[] rez = new byte[0];

        ByteBuffer buffer0 = imgYUV420.getPlanes()[0].getBuffer();
        ByteBuffer buffer2 = imgYUV420.getPlanes()[2].getBuffer();
        int buffer0_size = buffer0.remaining();
        int buffer2_size = buffer2.remaining();
        rez = new byte[buffer0_size + buffer2_size];

        buffer0.get(rez, 0, buffer0_size);
        buffer2.get(rez, buffer0_size, buffer2_size);

        return rez;
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

    @Override
    public int getCameraNumber(Context context) {
        int number = 0;
        try {
            number = ((CameraManager) context.getSystemService(Context.CAMERA_SERVICE)).getCameraIdList().length;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return number;
    }
}
