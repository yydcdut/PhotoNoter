package com.yydcdut.note.camera.model.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.model.AbsCameraModel;
import com.yydcdut.note.camera.model.ICameraFocus;
import com.yydcdut.note.camera.model.ICameraSetting;
import com.yydcdut.note.utils.YLog;

import java.io.IOException;

/**
 * Created by yuyidong on 15-4-8.
 */
public class CameraModel extends AbsCameraModel {
    private static final String TAG = CameraModel.class.getSimpleName();

    private static final int STATE_CAMERA_IDLE = 0;
    private static final int STATE_CAMERA_OPEN = 1;
    private static final int STATE_CAMERA_PREVIEW_NORMAL = 2;
    private static final int STATE_CAMERA_NO_PREVIEW = 3;
    private int mCameraState = STATE_CAMERA_IDLE;

    private Camera mCamera = null;
    private MediaPlayer mShootSound;
    private SurfaceHolder mSurfaceHolder;
    private String mCurrentCameraId;
    private Context mContext;
    private SettingModel mSettingModel;
    private FocusModel mFocusModel;
    private int mCategoryId;


    private int mViewWidth;
    private int mViewHeight;

    public CameraModel(Context context, SurfaceHolder surfaceHolder, int categoryId) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;
        mCategoryId = categoryId;
    }


    @Override
    public void openCamera(String id, int orientation) {
        if (mCamera != null) {
            YLog.e(TAG, "相机已经开启");
            return;
        }
        if (mCameraState == STATE_CAMERA_IDLE) {
            try {
                int cameraId = 0;
                try {
                    cameraId = Integer.parseInt(id);
                } catch (Exception e) {
                    //String转int失败,id不是数字
                }
                mCurrentCameraId = String.valueOf(cameraId);
                mCamera = Camera.open(cameraId);
                setCameraDisplayOrientation(orientation);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCameraState = STATE_CAMERA_OPEN;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置相机预览方向
     */
    private void setCameraDisplayOrientation(int orientation) {
        mCamera.setDisplayOrientation(orientation);
    }

    @Override
    public void reopenCamera(String id, int orientation) {
        if (mCamera == null) {
            //当相机开启失败失败，那么其他操作都不作处理
            YLog.i(TAG, "reopenCamera,当相机开启失败失败，那么其他操作都不作处理");
            return;
        }
        if (mCurrentCameraId.equals(id) || mCamera == null) {
            //相同，就不再进行操作,,,没有开启相机，应该调用openCamera
            return;
        }
        if (mCameraState == STATE_CAMERA_PREVIEW_NORMAL) {
            mCamera.stopPreview();
        }
        YLog.i(TAG, "reopenCamera,mCamera.release();");
        mCamera.release();
        mCamera = null;
        destroySettingModel();
        destroyFocusModel();
        mCameraState = STATE_CAMERA_IDLE;
        openCamera(id, orientation);
    }

    @Override
    public void startPreview() {
        if (mCamera == null) {
            //当相机开启失败失败，那么其他操作都不作处理
            YLog.i(TAG, "startPreview,当相机开启失败失败，那么其他操作都不作处理");
            return;
        }
        if (mCameraState == STATE_CAMERA_PREVIEW_NORMAL) {
            //已经开启预览了，再调用也没得啥子用
            YLog.i(TAG, "已经开启预览了，再调用也没得啥子用");
            return;
        }
        if (mCameraState != STATE_CAMERA_OPEN) {
            //开启了相机才能开启预览
            throw new RuntimeException("开启了相机才能开启预览");
        }
        mCamera.startPreview();
        mCameraState = STATE_CAMERA_PREVIEW_NORMAL;
    }

    @Override
    public void reStartPreview() {
        mCamera.startPreview();
        mCameraState = STATE_CAMERA_PREVIEW_NORMAL;
    }

    @Override
    public void stopPreview() {
        if (mCamera == null) {
            //当相机开启失败失败，那么其他操作都不作处理
            YLog.i(TAG, "stopPreview,当相机开启失败失败，那么其他操作都不作处理");
            return;
        }
        if (mCameraState == STATE_CAMERA_PREVIEW_NORMAL) {
            mCamera.stopPreview();
        }
        mCameraState = STATE_CAMERA_NO_PREVIEW;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void closeCamera() {
        if (mCamera == null) {
            //当相机开启失败失败，那么其他操作都不作处理
            YLog.i(TAG, "closeCamera,当相机开启失败失败，那么其他操作都不作处理");
            return;
        }
        if (mCameraState == STATE_CAMERA_PREVIEW_NORMAL) {
            mCamera.stopPreview();
        }
        mCamera.release();
        mCamera = null;
        destroySettingModel();
        destroyFocusModel();
        mCameraState = STATE_CAMERA_IDLE;
    }

    @Override
    public long capture(boolean sound, int ratio, boolean isMirror) {
        long time = 0l;
        if (getFocusModel().getFocusState() != ICameraFocus.FOCUS_STATE_FOCUSING && mCameraState == STATE_CAMERA_PREVIEW_NORMAL) {
            time = System.currentTimeMillis();
            try {
                mCamera.takePicture(sound ? new SoundCallBack() : null, null, new PictureCallBack(time, mCategoryId, ratio, isMirror));
            } catch (RuntimeException e) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_fail), Toast.LENGTH_SHORT).show();
            }
        } else {
            YLog.i(TAG, "capture  focusState--->" + getFocusModel().getFocusState() + "   CameraState--->" + mCameraState);
        }
        return time;
    }

    /**
     * 创建jpeg图片回调数据对象
     */
    private class PictureCallBack implements Camera.PictureCallback {
        private long time;
        private int categoryId;
        private int ratio;
        private boolean isMirror;

        public PictureCallBack(long time, int categoryId, int ratio, boolean isMirror) {
            this.time = time;
            this.categoryId = categoryId;
            this.ratio = ratio;
            this.isMirror = isMirror;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            addData2Service(data, mCurrentCameraId, time, categoryId, isMirror, ratio);
            //这里经常崩溃，做个延时处理
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reStartPreview();
                }
            }, 300);
        }
    }

    private class SoundCallBack implements Camera.ShutterCallback {

        @Override
        public void onShutter() {
            try {
                AudioManager meng = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                if (volume != 0) {
                    if (mShootSound == null) {
                        mShootSound = MediaPlayer.create(mContext, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                    }
                    if (mShootSound != null) {
                        mShootSound.start();
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public ICameraSetting getSettingModel() {
        if (mCamera == null) {
            YLog.e(TAG, "相机还没有开启，拿不到");
            return null;
        }
//        synchronized (this) {
        if (mSettingModel == null) {
            mSettingModel = new SettingModel(mCamera);
        }
//        }
        return mSettingModel;
    }

    @Override
    public ICameraFocus getFocusModel() {
        if (mViewWidth == 0 || mViewHeight == 0) {
            throw new IllegalArgumentException("必须得传入view的大小，通过setTouchArea");
        }
        if (mCamera == null) {
            YLog.e(TAG, "相机还没有开启，拿不到");
            return null;
        }
//        synchronized (this) {
        if (mFocusModel == null) {
            mFocusModel = new FocusModel(mCamera, mViewWidth, mViewHeight);
        }
//        }
        return mFocusModel;
    }

    @Override
    public void setTouchArea(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
    }

    private void destroySettingModel() {
        mSettingModel = null;
    }

    private void destroyFocusModel() {
        mFocusModel = null;
    }
}
