package com.yydcdut.note.presenters.camera.impl;

import android.content.Context;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yydcdut.note.R;
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
import com.yydcdut.note.presenters.camera.ICameraPresenter;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.CameraStateUtils;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.camera.ICameraView;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraPresenterImpl implements ICameraPresenter, Handler.Callback,
        ICaptureModel.PictureReturnCallback, ICaptureModel.StillPictureReturnCallback {
    private static final int TIME_LONG_CAPTURE = 1000;//1s内没有抬起，那么算作长拍摄
    private boolean mIsWannaStillCapture = false;

    private ICameraView mICameraView;
    private Size mPreviewSize;
    private Size mPictureSize;
    private int mCategoryId;
    private AutoFitPreviewView.PreviewSurface mPreviewSurface;

    private ICameraModel mCameraModel;

    private ICameraSettingModel mCameraSettingModel;

    private Context mContext;
    private LocalStorageUtils mLocalStorageUtils;

//    private int mFlashState = 0;
//    private int mTimerState = 0;
//    private int mGridState =0;
//    private int mCameraId;


    /* 坐标 */
    private LocationClient mLocationClient;
    private double mLatitude;
    private double mLontitude;

    private String mCurrentCameraId;

    private static final int MSG_DOWN = 1;
    private static final int MSG_UP = 2;
    private static final int MSG_STILL_SIGNAL = 3;
    private Handler mHandler;

    private IPreviewModel mPreviewModel;
    private ICaptureModel mCaptureModel;

    @Inject
    public CameraPresenterImpl(@ContextLife("Activity") Context context, LocalStorageUtils localStorageUtils,
                               CameraModelImpl cameraModelImpl) {
        mContext = context;
        mLocalStorageUtils = localStorageUtils;
        if (AppCompat.AFTER_LOLLIPOP) {
            //Dagger2中，在5.0以下机器，找不到Camera2类，会崩掉。。不知道原因
            /*
             *  java.lang.VerifyError: com/yydcdut/note/model/camera/impl2/Camera2ModelImpl
                                                                    at com.yydcdut.note.model.camera.impl2.Camera2ModelImpl_Factory.get(Camera2ModelImpl_Factory.java:19)
                                                                    at com.yydcdut.note.model.camera.impl2.Camera2ModelImpl_Factory.get(Camera2ModelImpl_Factory.java:8)
                                                                    at com.yydcdut.note.presenters.camera.impl.CameraPresenterImpl_Factory.get(CameraPresenterImpl_Factory.java:31)
                                                                    at com.yydcdut.note.presenters.camera.impl.CameraPresenterImpl_Factory.get(CameraPresenterImpl_Factory.java:11)
                                                                    at com.yydcdut.note.views.camera.impl.CameraActivity2_MembersInjector.injectMembers(CameraActivity2_MembersInjector.java:27)
                                                                    at com.yydcdut.note.views.camera.impl.CameraActivity2_MembersInjector.injectMembers(CameraActivity2_MembersInjector.java:9)
                                                                    at com.yydcdut.note.injector.component.DaggerActivityComponent.inject(DaggerActivityComponent.java:315)
                                                                    at com.yydcdut.note.views.camera.impl.CameraActivity2.initInjector(CameraActivity2.java:67)
                                                                    at com.yydcdut.note.views.BaseActivity.onCreate(BaseActivity.java:136)
                                                                    at android.app.Activity.performCreate(Activity.java:5104)
                                                                    at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1080)
                                                                    at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2144)
                                                                    at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2230)
                                                                    at android.app.ActivityThread.access$600(ActivityThread.java:141)
                                                                    at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1234)
                                                                    at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                    at android.os.Looper.loop(Looper.java:137)
                                                                    at android.app.ActivityThread.main(ActivityThread.java:5039)
                                                                    at java.lang.reflect.Method.invokeNative(Native Method)
                                                                    at java.lang.reflect.Method.invoke(Method.java:511)
                                                                    at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:833)
                                                                    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:600)
                                                                    at dalvik.system.NativeStart.main(Native Method)
            */
            mCameraModel = new Camera2ModelImpl(context);
        } else {
            mCameraModel = cameraModelImpl;
        }
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mICameraView = (ICameraView) iView;
        mCurrentCameraId = mLocalStorageUtils.getCameraSaveCameraId();
        mPictureSize = getPictureSize();
        mCameraModel.openCamera(mCurrentCameraId, new ICameraModel.OnCameraOpenedCallback() {

            @Override
            public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                mPreviewModel = previewModel;
                mCameraSettingModel = cameraSettingModel;
                initUIState();
                mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                mICameraView.setSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            @Override
            public void onError() {

            }
        }, getCameraRotation(), mPictureSize);
        if (mPictureSize == null) {
            mPictureSize = savePictureSizes(mCurrentCameraId);
        }
        initLocation();
        mHandler = new Handler(this);
    }

    private void initUIState() {
        if (mCameraSettingModel != null) {
            boolean flashSupported = mCameraSettingModel.isFlashSupported();
            int[] flashRes = flashSupported ?
                    new int[]{
                            R.drawable.ic_flash_off_white_24dp,
                            R.drawable.ic_flash_auto_white_24dp,
                            R.drawable.ic_flash_on_white_24dp} :
                    new int[]{R.drawable.ic_flash_off_white_24dp};
            int cameraNumber = mCameraSettingModel.getNumberOfCameras();
            int[] cameraIdRes = cameraNumber == 1 ?
                    new int[]{R.drawable.ic_camera_rear_white_24dp} :
                    new int[]{
                            R.drawable.ic_camera_rear_white_24dp,
                            R.drawable.ic_camera_front_white_24dp};
            if (mLocalStorageUtils.getCameraSaveSetting()) {
                mICameraView.initState(
                        CameraStateUtils.changeFlahsSaveState2UIState(mLocalStorageUtils.getCameraSaveFlash()), flashRes,
                        CameraStateUtils.changeTimerSaveState2UIState(mLocalStorageUtils.getCameraSaveTimer()),
                        CameraStateUtils.changeGridSaveState2UIState(mLocalStorageUtils.getCameraGridOpen()),
                        CameraStateUtils.changeCameraIdSaveState2UIState(mLocalStorageUtils.getCameraSaveCameraId()), cameraIdRes);
            } else {
                mICameraView.initState(0, flashRes, 0, 0, 0, cameraIdRes);
            }
        }
    }

    @Override
    public void bindData(int categoryId) {
        mCategoryId = categoryId;
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
//                mFullSize = preSize;
                previewSize = preSize;
            }
            //4:3 默认进来4：3
            if (preScale < 1.36f && preScale > 1.30f) {
//                m43Size = preSize;
                previewSize = preSize;
            }
//            if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_1_1) {
//                previewSize = m43Size;
//                mMenuLayout.setRatio11();
//            } else if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
//                mMenuLayout.setRatio43();
//                previewSize = mFullSize;
//            } else {
//                mMenuLayout.setRatio43();
//                previewSize = m43Size;
//            }
        }
//        if (mFullSize == null) {
//            mFullSize = previewList.get(previewList.size() / 2);
//        }
//        if (m43Size == null) {
//            m43Size = previewList.get(previewList.size() / 2);
//        }
        if (previewSize == null) {
            previewSize = previewList.get(0);
        }
        return previewSize;
    }

    private Size getPictureSize() {
        Size size = null;
        try {
            size = mLocalStorageUtils.getPictureSize(mCurrentCameraId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return size;
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

    @Override
    public void detachView() {
        closeCamera();
        mLocationClient.stop();
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
            mCurrentCameraId = mLocalStorageUtils.getCameraSaveCameraId();
            mPictureSize = getPictureSize();
            mCameraModel.openCamera(mCurrentCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(surface, new IPreviewModel.OnCameraPreviewCallback() {

                                @Override
                                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                                    mCaptureModel = captureModel;
                                }

                                @Override
                                public void onPreviewError() {

                                }
                            }, mPreviewSize);
                        }

                        @Override
                        public void onError() {

                        }
                    }, getCameraRotation(), mPictureSize);
            if (mPictureSize == null) {
                mPictureSize = savePictureSizes(mCurrentCameraId);
            }
        } else if (mCameraModel.isOpen() && mPreviewModel != null) {
            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
            mPreviewModel.startPreview(surface, new IPreviewModel.OnCameraPreviewCallback() {

                @Override
                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                    mCaptureModel = captureModel;
                }

                @Override
                public void onPreviewError() {

                }
            }, mPreviewSize);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        closeCamera();
    }

    @Override
    public void onDown() {
        if (mPreviewModel != null && mPreviewModel.isPreview()) {
            mHandler.sendEmptyMessage(MSG_DOWN);
        }
    }

    @Override
    public void onUp() {
        if (mPreviewModel != null && mPreviewModel.isPreview()) {
            mHandler.sendEmptyMessage(MSG_UP);
        }
    }

    @Override
    public void onFlashClick() {

    }

    @Override
    public void onTimerClick() {

    }

    @Override
    public void onGridClick() {

    }

    @Override
    public void onCameraIdClick() {
        if (mCameraSettingModel != null && mCameraSettingModel.getNumberOfCameras() == 2) {
            if (Const.CAMERA_BACK.equals(mCurrentCameraId)) {
                mCurrentCameraId = Const.CAMERA_FRONT;
            } else {
                mCurrentCameraId = Const.CAMERA_BACK;
            }
            closeCamera();
            Size pictureSize = getPictureSize();
            mCameraModel.openCamera(mCurrentCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(mPreviewSurface, new IPreviewModel.OnCameraPreviewCallback() {

                                @Override
                                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                                    mCaptureModel = captureModel;
                                }

                                @Override
                                public void onPreviewError() {

                                }
                            }, mPreviewSize);
                        }

                        @Override
                        public void onError() {
                        }
                    }, getCameraRotation(), pictureSize);
            if (pictureSize == null) {
                savePictureSizes(mCurrentCameraId);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DOWN:
                mHandler.sendEmptyMessageDelayed(MSG_STILL_SIGNAL, TIME_LONG_CAPTURE);
                break;
            case MSG_STILL_SIGNAL:
                mIsWannaStillCapture = true;
                mCaptureModel.startStillCapture(this);
                break;
            case MSG_UP:
                if (mHandler.hasMessages(MSG_STILL_SIGNAL)) {
                    mHandler.removeMessages(MSG_STILL_SIGNAL);
                }
                if (mIsWannaStillCapture) {
                    mIsWannaStillCapture = false;
                    mCaptureModel.stopStillCapture();
                } else {
                    mCaptureModel.capture(this);
                }
                break;
        }
        return false;
    }

    private void initLocation() {
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLatitude = bdLocation.getLatitude();
                mLontitude = bdLocation.getLongitude();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(checkGeoLocation.isChecked());//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private boolean addData2Service(byte[] data, String cameraId, long time, int categoryId,
                                    boolean isMirror, int ratio, int imageFormat) {
        boolean bool = true;
        int size = data.length;
        String fileName = time + ".data";
        File file = new File(FilePathUtils.getSandBoxDir() + fileName);
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    bool = false;
                    e.printStackTrace();
                }
            }
        }

        int orientation = 0;//todo 这个还没做，下个版本做

        String latitude0 = String.valueOf((int) mLatitude) + "/1,";
        String latitude1 = String.valueOf((int) ((mLatitude - (int) mLatitude) * 60) + "/1,");
        String latitude2 = String.valueOf((int) ((((mLatitude - (int) mLatitude) * 60) - ((int) ((mLatitude - (int) mLatitude) * 60))) * 60 * 10000)) + "/10000";
        String latitude = new StringBuilder(latitude0).append(latitude1).append(latitude2).toString();
        String lontitude0 = String.valueOf((int) mLontitude) + "/1,";
        String lontitude1 = String.valueOf((int) ((mLontitude - (int) mLontitude) * 60) + "/1,");
        String lontitude2 = String.valueOf((int) ((((mLontitude - (int) mLontitude) * 60) - ((int) ((mLontitude - (int) mLontitude) * 60))) * 60 * 10000)) + "/10000";
        String lontitude = new StringBuilder(lontitude0).append(lontitude1).append(lontitude2).toString();
        int whiteBalance = 0;
//        if (getSettingModel().getSupportedWhiteBalance().size() > 0) {
//            if (getSettingModel().getWhiteBalance() != ICameraParams.WHITE_BALANCE_AUTO) {
//                whiteBalance = 1;
//            }
//        }
        //todo 这里的flash是指拍照的那个时候闪光灯是否打开了,所以啊。。。这个。。。。
        int flash = 0;
//        if (getSettingModel().getSupportedFlash().size() > 0) {
//            if (getSettingModel().getFlash() != ICameraParams.FLASH_OFF) {
//                flash = 1;
//            }
//        }
        int imageLength;
        int imageWidth;
        switch (imageFormat) {
            case ImageFormat.NV21:
                imageLength = mPreviewSize.getHeight();
                imageWidth = mPreviewSize.getWidth();
                if (ratio == Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1) {
                    imageLength = imageWidth;
                }
                break;
            default:
            case ImageFormat.JPEG:
                imageLength = mPictureSize.getHeight();
                imageWidth = mPictureSize.getWidth();
                if (ratio == Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1) {
                    imageLength = imageWidth;
                }
                break;

        }
        String make = Build.BRAND;
        String model = Build.MODEL;
        try {
            mICameraView.add2Service(fileName, size, cameraId, time, categoryId, isMirror, ratio,
                    orientation, latitude, lontitude, whiteBalance, flash, imageLength, imageWidth,
                    make, model, imageFormat);
        } catch (RemoteException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    @Override
    public void onPictureTaken(boolean success, byte[] data, long time) {
        if (success) {
            addData2Service(data, mCurrentCameraId, time, mCategoryId, false,
                    Const.CAMERA_SANDBOX_PHOTO_RATIO_FULL, ImageFormat.JPEG);
            mPreviewModel.continuePreview();
        } else {
            mICameraView.showToast(mContext.getResources().getString(R.string.toast_fail));
        }
    }

    @Override
    public void onStillPictureTaken(int imageFormat, byte[] data, long time) {
        switch (imageFormat) {
            case ImageFormat.NV21:
                addData2Service(data, mCurrentCameraId, time, mCategoryId, false,
                        Const.CAMERA_SANDBOX_PHOTO_RATIO_FULL, ImageFormat.NV21);
                break;
        }
    }

    private int getCameraRotation() {
        switch (mCurrentCameraId) {
            case Const.CAMERA_BACK:
                return mLocalStorageUtils.getCameraBackRotation();
            case Const.CAMERA_FRONT:
            default:
                return mLocalStorageUtils.getCameraFrontRotation();
        }
    }

}
