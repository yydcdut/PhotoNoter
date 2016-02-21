package com.yydcdut.note.presenters.camera.impl;

import android.content.Context;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.ICameraParams;
import com.yydcdut.note.model.camera.ICameraSettingModel;
import com.yydcdut.note.model.camera.ICaptureModel;
import com.yydcdut.note.model.camera.IPreviewModel;
import com.yydcdut.note.model.camera.impl.CameraModelImpl;
import com.yydcdut.note.model.camera.impl2.Camera2ModelImpl;
import com.yydcdut.note.model.compare.SizeComparator;
import com.yydcdut.note.presenters.camera.ICameraPresenter;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.camera.CameraStateUtils;
import com.yydcdut.note.utils.camera.param.Size;
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
    /* Size */
    private Size mFullSize;
    private Size m43Size;
    private Size mPictureSize;
    private Size mPreviewSize;
    /* 业务逻辑 */
    private int mCategoryId;
    /* UI */
    private ICameraView mICameraView;
    private AutoFitPreviewView.PreviewSurface mPreviewSurface;
    /* Model */
    private ICameraModel mCameraModel;
    private ICameraSettingModel mCameraSettingModel;
    private IPreviewModel mPreviewModel;
    private ICaptureModel mCaptureModel;
    private ICameraFocus mCameraFocus;
    /* 参数 */
    private Context mContext;
    private LocalStorageUtils mLocalStorageUtils;
    /* 状态 */
    private int mFlashState = 0;
    private int mRatioState = 0;
    private int mTimerState = 0;
    private boolean mGridState = false;
    private int mExposureCompensation = 0;
    /* 坐标 */
    private LocationClient mLocationClient;
    private double mLatitude;
    private double mLongitude;
    /* Camera Zoom */
    private float mZoomCurrentSpan = 0;
    private int mFirstZoomValue = 0;
    private int mLastZoomValue = 0;
    /* CameraId */
    private String mCurrentCameraId;
    /* Message */
    private static final int MSG_DOWN = 1;
    private static final int MSG_UP = 2;
    private static final int MSG_STILL_SIGNAL = 3;
    private static final int MSG_CAPTURE = 4;
    private Handler mHandler;

    @Inject
    public CameraPresenterImpl(@ContextLife("Activity") Context context, LocalStorageUtils localStorageUtils,
                               CameraModelImpl cameraModelImpl) {
        mContext = context;
        mLocalStorageUtils = localStorageUtils;
        if (AppCompat.AFTER_LOLLIPOP && mLocalStorageUtils.getCameraAndroidLollipop()) {
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
            YLog.i("yuyidong", "Camera2ModelImpl");
            mCameraModel = new Camera2ModelImpl(context);
//            mCameraModel = cameraModelImpl;
        } else {
            YLog.i("yuyidong", "CameraModelImpl");
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
                if (mPictureSize == null) {
                    mPictureSize = savePictureSizes(mCurrentCameraId);
                }
                initUIState();
                initLogicState();
                mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                if (mRatioState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
                    mICameraView.doFullRatioAnimation();
                } else if (mRatioState == Const.LAYOUT_PERSONAL_RATIO_4_3) {
                    mICameraView.do43RatioAnimation();
                } else {
                    mICameraView.do11RatioAnimation();
                }
                mICameraView.setSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                onGridClick(-1);
            }

            @Override
            public void onError() {
            }
        }, getCameraRotation(), mPictureSize);
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
                        CameraStateUtils.changeFlashLogicState2UIState(mLocalStorageUtils.getCameraSaveFlash()), flashRes,
                        CameraStateUtils.changeRatioLogicState2UIState(mLocalStorageUtils.getCameraPreviewRatioDefault()),
                        CameraStateUtils.changeTimerLogicState2UIState(mLocalStorageUtils.getCameraSaveTimer()),
                        CameraStateUtils.changeGridLogicState2UIState(mLocalStorageUtils.getCameraGridOpen()),
                        CameraStateUtils.changeCameraIdLogicState2UIState(mLocalStorageUtils.getCameraSaveCameraId()), cameraIdRes);
            } else {
                mICameraView.initState(0, flashRes, 0, 0, 0, 0, cameraIdRes);
            }

            if (Const.CAMERA_BACK.equals(mCurrentCameraId)) {
                mICameraView.initFocus(true);
            } else {
                mICameraView.initFocus(false);
            }
        }
    }

    private void initLogicState() {
        if (mLocalStorageUtils.getCameraSaveSetting()) {
            mFlashState = mLocalStorageUtils.getCameraSaveFlash();
            mRatioState = mLocalStorageUtils.getCameraPreviewRatioDefault();
            mTimerState = mLocalStorageUtils.getCameraSaveTimer();
            mGridState = mLocalStorageUtils.getCameraGridOpen();
        } else {
            mFlashState = ICameraParams.FLASH_OFF;
            mRatioState = Const.LAYOUT_PERSONAL_RATIO_FULL;
            mTimerState = Const.LAYOUT_PERSONAL_TIMER_0;
            mGridState = false;
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
                mFullSize = preSize;
            }
            //4:3 默认进来4：3
            if (preScale < 1.36f && preScale > 1.30f) {
                m43Size = preSize;
            }
            if (mRatioState == Const.LAYOUT_PERSONAL_RATIO_1_1) {
                previewSize = m43Size;
            } else if (mRatioState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
                previewSize = mFullSize;
            } else {
                previewSize = m43Size;
            }
        }
        if (mFullSize == null) {
            mFullSize = previewList.get(previewList.size() / 2);
        }
        if (m43Size == null) {
            m43Size = previewList.get(previewList.size() / 2);
        }
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
        saveState();
        if (mPreviewModel != null && mPreviewModel.isPreview()) {
            mPreviewModel.stopPreview();
        }
        if (mCameraModel.isOpen()) {
            mCameraModel.closeCamera();
        }
    }

    /**
     * 保存参数
     */
    private void saveState() {
        if (mLocalStorageUtils.getCameraSaveSetting()) {
            mLocalStorageUtils.setCameraSaveFlash(mFlashState);
            mLocalStorageUtils.setCameraSaveTimer(mTimerState);
            mLocalStorageUtils.setCameraGridOpen(mGridState);
            mLocalStorageUtils.setCameraPreviewRatio(mRatioState);
            mLocalStorageUtils.setCameraSaveCameraId(mCurrentCameraId);
            mLocalStorageUtils.setCameraExposureCompensation(mExposureCompensation);
        }
    }

    @Override
    public void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height) {
        mPreviewSurface = surface;
        if (!mCameraModel.isOpen()) {
            if (TextUtils.isEmpty(mCurrentCameraId)) {
                mCurrentCameraId = mLocalStorageUtils.getCameraSaveCameraId();
            }
            mPictureSize = getPictureSize();
            mCameraModel.openCamera(mCurrentCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            if (mPictureSize == null) {
                                mPictureSize = savePictureSizes(mCurrentCameraId);
                            }
                            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(surface, new IPreviewModel.OnCameraPreviewCallback() {

                                @Override
                                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                                    mCaptureModel = captureModel;
                                    mCameraFocus = cameraFocus;
                                    initExposure();
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

        } else if (mCameraModel.isOpen() && mPreviewModel != null) {
            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
            mPreviewModel.startPreview(surface, new IPreviewModel.OnCameraPreviewCallback() {

                @Override
                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                    mCaptureModel = captureModel;
                    mCameraFocus = cameraFocus;
                    initExposure();
                }

                @Override
                public void onPreviewError() {

                }
            }, mPreviewSize);
        }
    }

    /**
     * 初始化曝光
     */
    private void initExposure() {
        if (mLocalStorageUtils.getCameraSaveSetting()) {
            mExposureCompensation = mLocalStorageUtils.getCameraExposureCompensation();
            int max = mCameraSettingModel.getMaxExposureCompensation();
            int min = mCameraSettingModel.getMinExposureCompensation();
            if (mExposureCompensation > max) {
                mExposureCompensation = max;
            } else if (mExposureCompensation < min) {
                mExposureCompensation = min;
            }
            float percent = 1 - ((float) (mExposureCompensation + Math.abs(min))) / (max + Math.abs(min));
            mICameraView.setIsoViewValue((int) (mICameraView.getIsoViewMaxValue() * percent));
            mCameraSettingModel.setExposureCompensation(mExposureCompensation);
        } else {
            mICameraView.setIsoViewValue(mICameraView.getIsoViewMaxValue() / 2);
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
    public void onFlashClick(int state) {
        mFlashState = CameraStateUtils.changeFlashUIState2LogicState(state);
        if (mCameraSettingModel != null) {
            mCameraSettingModel.setFlash(mFlashState);
        }
    }

    @Override
    public void onRatioClick(int state) {
        mRatioState = CameraStateUtils.changeRatioUIState2LogicState(state);
        if (mRatioState == Const.LAYOUT_PERSONAL_RATIO_4_3 || mRatioState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
            switch (mRatioState) {
                case Const.LAYOUT_PERSONAL_RATIO_FULL:
                    mPreviewSize = mFullSize;
                    mICameraView.doFullRatioAnimation();
                    break;
                case Const.LAYOUT_PERSONAL_RATIO_4_3:
                default:
                    mPreviewSize = m43Size;
                    mICameraView.do43RatioAnimation();
                    break;
            }
            mPreviewModel.stopPreview();
            mICameraView.setSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            mPreviewModel.startPreview(mPreviewSurface, new IPreviewModel.OnCameraPreviewCallback() {
                @Override
                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                    mCaptureModel = captureModel;
                    mCameraFocus = cameraFocus;
                }

                @Override
                public void onPreviewError() {

                }
            }, mPreviewSize);
        } else {
            if (!mPreviewSize.equals(m43Size)) {
                mPreviewSize = m43Size;
                mPreviewModel.stopPreview();
                mICameraView.setSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            mICameraView.do11RatioAnimation();
        }
        onGridClick(-1);
    }

    @Override
    public void onTimerClick(int state) {
        mTimerState = CameraStateUtils.changeTimerUIState2LogicState(state);
    }

    @Override
    public void onTimerCancel() {

    }

    @Override
    public void onTimerFinish() {
        mHandler.sendEmptyMessage(MSG_CAPTURE);
    }

    @Override
    public void onGridClick(int state) {
        if (state != -1) {
            mGridState = CameraStateUtils.changeGridUIState2LogicState(state);
        }
        if (mGridState) {
            int top = 0;
            int bottom = 0;
            switch (mRatioState) {
                case Const.LAYOUT_PERSONAL_RATIO_FULL:
                    mICameraView.setGridUI(true, top, bottom, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    break;
                case Const.LAYOUT_PERSONAL_RATIO_4_3:
                    top = mICameraView.getTopViewHeight();
                    mICameraView.setGridUI(true, top, bottom, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    break;
                case Const.LAYOUT_PERSONAL_RATIO_1_1:
                    top = mICameraView.getTopViewHeight();
                    mICameraView.setGridUI(true, top, bottom, mPreviewSize.getWidth(), mPreviewSize.getWidth());
                    break;
            }
        } else {
            mICameraView.setGridUI(false, 0, 0, mPreviewSize.getWidth(), mPreviewSize.getHeight());
        }
    }

    @Override
    public void onCameraIdClick(int state) {
        if (mCameraSettingModel != null && mCameraSettingModel.getNumberOfCameras() == 2) {
            if (Const.CAMERA_BACK.equals(mCurrentCameraId)) {
                mCurrentCameraId = Const.CAMERA_FRONT;
            } else {
                mCurrentCameraId = Const.CAMERA_BACK;
            }
            closeCamera();
            final Size pictureSize = getPictureSize();
            mCameraModel.openCamera(mCurrentCameraId,
                    new ICameraModel.OnCameraOpenedCallback() {

                        @Override
                        public void onOpen(IPreviewModel previewModel, ICameraSettingModel cameraSettingModel) {
                            mPreviewModel = previewModel;
                            mCameraSettingModel = cameraSettingModel;
                            if (pictureSize == null) {
                                savePictureSizes(mCurrentCameraId);
                            }
                            initUIState();
                            initLogicState();
                            mPreviewSize = getSuitablePreviewSize(mCameraSettingModel.getSupportPreviewSizes());
                            mPreviewModel.startPreview(mPreviewSurface, new IPreviewModel.OnCameraPreviewCallback() {

                                @Override
                                public void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus) {
                                    mCaptureModel = captureModel;
                                    mCameraFocus = cameraFocus;
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
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DOWN:
                mHandler.sendEmptyMessageDelayed(MSG_STILL_SIGNAL, TIME_LONG_CAPTURE);
                break;
            case MSG_STILL_SIGNAL:
                if (AppCompat.AFTER_LOLLIPOP) {
                    mICameraView.showToast(mContext.getResources().getString(R.string.not_support));
                } else {
                    mIsWannaStillCapture = true;
                    mCaptureModel.startStillCapture(this);
                }
                break;
            case MSG_UP:
                if (mHandler.hasMessages(MSG_STILL_SIGNAL)) {
                    mHandler.removeMessages(MSG_STILL_SIGNAL);
                }
                if (mIsWannaStillCapture) {
                    mIsWannaStillCapture = false;
                    mCaptureModel.stopStillCapture();
                } else {
                    if (mICameraView.isTimerCounting()) {
                        mICameraView.interruptTimer();
                        break;
                    }
                    switch (mTimerState) {
                        case Const.LAYOUT_PERSONAL_TIMER_0:
                            mCaptureModel.capture(this);
                            break;
                        case Const.LAYOUT_PERSONAL_TIMER_3:
                            mICameraView.startTimer(3);
                            break;
                        case Const.LAYOUT_PERSONAL_TIMER_10:
                            mICameraView.startTimer(10);
                            break;
                    }
                }
                break;
            case MSG_CAPTURE:
                mCaptureModel.capture(this);
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
                mLongitude = bdLocation.getLongitude();
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
                                    boolean isMirror, int ratio, int imageFormat, int width, int height) {
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
        String longitude0 = String.valueOf((int) mLongitude) + "/1,";
        String longitude1 = String.valueOf((int) ((mLongitude - (int) mLongitude) * 60) + "/1,");
        String longitude2 = String.valueOf((int) ((((mLongitude - (int) mLongitude) * 60) - ((int) ((mLongitude - (int) mLongitude) * 60))) * 60 * 10000)) + "/10000";
        String longitude = new StringBuilder(longitude0).append(longitude1).append(longitude2).toString();
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
        int imageLength = width;
        int imageWidth = height;
        if (ratio == Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1) {
            imageLength = imageWidth;
        }
        String make = Build.BRAND;
        String model = Build.MODEL;
        try {
            mICameraView.add2Service(fileName, size, cameraId, time, categoryId, isMirror, ratio,
                    orientation, latitude, longitude, whiteBalance, flash, imageLength, imageWidth,
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
                    CameraStateUtils.changeRatioState2SandBoxState(mRatioState), ImageFormat.JPEG,
                    mPictureSize.getWidth(), mPictureSize.getHeight());
            mPreviewModel.continuePreview();
        } else {
            mICameraView.showToast(mContext.getResources().getString(R.string.toast_fail));
        }
    }

    @Override
    public void onStillPictureTaken(int imageFormat, byte[] data, long time, int width, int height) {
        addData2Service(data, mCurrentCameraId, time, mCategoryId, false,
                CameraStateUtils.changeRatioState2SandBoxState(mRatioState), imageFormat, width, height);
    }

    private int getCameraRotation() {
        //// FIXME: 16/2/17 Camera2内部实现实际上没有用到Rotation
        switch (mCurrentCameraId) {
            case Const.CAMERA_BACK:
                return mLocalStorageUtils.getCameraBackRotation();
            case Const.CAMERA_FRONT:
            default:
                return mLocalStorageUtils.getCameraFrontRotation();
        }
    }

    @Override
    public boolean onZoomChange(float num) {
        int zoomValue = mCameraSettingModel.calculateZoom(mFirstZoomValue, mZoomCurrentSpan, num);
        if (zoomValue != -1 && mLastZoomValue != zoomValue) {
            mCameraSettingModel.setZoom(zoomValue);
            mLastZoomValue = zoomValue;
        }
        return true;
    }

    @Override
    public boolean onZoomBegin(float currentSpan) {
        boolean isZoomSupported = mCameraSettingModel.isZoomSupported();
        if (isZoomSupported) {
            mZoomCurrentSpan = currentSpan;
            mFirstZoomValue = mCameraSettingModel.getZoom();
            mLastZoomValue = mFirstZoomValue;
        }
        return isZoomSupported;
    }

    @Override
    public void onFocusTrigger(int viewWidth, int viewHeight, float x, float y) {
        mCameraFocus.triggerFocus(viewWidth, viewHeight, (int) x, (int) y);
    }

    @Override
    public void onValueChanged(int value) {
        int max = mCameraSettingModel.getMaxExposureCompensation();
        int min = mCameraSettingModel.getMinExposureCompensation();
        int finalValue = -(value * (max - min) / mICameraView.getIsoViewMaxValue() - Math.abs(min));
        if (finalValue <= max && finalValue >= min) {
            mCameraSettingModel.setExposureCompensation(finalValue);
            mExposureCompensation = finalValue;
        }
    }

}
