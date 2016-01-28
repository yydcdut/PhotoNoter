package com.yydcdut.note.camera.controller;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.model.AbsCameraModel;
import com.yydcdut.note.camera.model.ICameraParams;
import com.yydcdut.note.camera.model.camera.CameraModel;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.camera.view.AnimationTextView;
import com.yydcdut.note.camera.view.AutoFitSurfaceView;
import com.yydcdut.note.camera.view.CameraGridView;
import com.yydcdut.note.camera.view.FocusView;
import com.yydcdut.note.camera.view.GestureView;
import com.yydcdut.note.camera.view.IsoView;
import com.yydcdut.note.camera.view.MenuLayout;
import com.yydcdut.note.camera.view.callback.OnLayoutItemClickListener;
import com.yydcdut.note.model.compare.SizeComparator;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuyidong on 15-4-8.
 */
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        OnLayoutItemClickListener, AnimationTextView.OnAnimationTextViewListener,
        IsoView.OnValueChangedListener, IsoView.OnIsoViewOnTouchedListener,
        FocusView.OnTriggerFocusListener, FocusView.OnFocusStateChangedListener,
        GestureView.OnZoomScaleListener, GestureView.OnFocusListener {
    /* Size */
    private Size mFullSize;
    private Size m43Size;
    /* Model */
    private AbsCameraModel mCameraModel;
    /* CameraId */
    private String mCameraId = Const.CAMERA_BACK;
    /* Other View */
    private AutoFitSurfaceView mAutoFitSurfaceView;
    private int mViewWidth;
    private int mViewHeight;
    /* View */
    private FocusView mFocusImage;
    private AnimationTextView mWindowTextView;
    private GestureView mGestureView;
    private IsoView mIsoView;
    private CameraGridView mCameraGridView;
    /* Screen */
    private int mScreenWidth = -1;
    private int mScreenHeight = -1;
    /* Category */
    private int mCategoryId;
    /* Camera Zoom */
    private float mZoomCurrentSpan = 0;
    private int mFirstZoomValue = 0;
    private int mLastZoomValue = 0;
    /* MenuLayout */
    private MenuLayout mMenuLayout;
    /* Timer */
    private int mTimerNumber;
    /* Sensor */
    private SensorManager mSensorManager;
    /* save setting */
    private boolean mIsSaving = false;
    private int mFlashState = ICameraParams.FLASH_OFF;
    private int mTimerState = Const.LAYOUT_PERSONAL_TIMER_0;
    private int mSizeState = Const.LAYOUT_PERSONAL_RATIO_4_3;
    private int mExposureCompensation = 0;
    private boolean mLocationOpen = false;
    private int mWBState = 0;
    private boolean mGridOpen = false;
    private boolean mTorchOpen = false;
    private boolean mSoundOpen = false;
    private int mCameraRotation = 0;

    private LocalStorageUtils mLocalStorageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalStorageUtils = LocalStorageUtils.getInstance(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        mCategoryId = bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES);
        AppCompat.setFullWindow(getWindow());
        try {
            initCameraNumberAndPictureSize();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_camera);
        initData();
        initUIAndListener();
        initSensor();
    }

    private void initData() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        mIsSaving = mLocalStorageUtils.getCameraSaveSetting();
        if (mIsSaving) {
            mCameraId = mLocalStorageUtils.getCameraSaveCameraId();
            mFlashState = mLocalStorageUtils.getCameraSaveFlash();
            mTimerState = mLocalStorageUtils.getCameraSaveTimer();
            mSoundOpen = mLocalStorageUtils.getCameraSoundOpen();
            mSizeState = mLocalStorageUtils.getCameraPreviewRatioDefault();
            mExposureCompensation = mLocalStorageUtils.getCameraExposureCompensation();
            mLocationOpen = mLocalStorageUtils.getCameraLocation();
            mWBState = mLocalStorageUtils.getCameraWhiteBalance();
            mGridOpen = mLocalStorageUtils.getCameraGridOpen();
        }
        getCameraRotation();
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void destroySensor() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void getCameraRotation() {
        switch (mCameraId) {
            case Const.CAMERA_BACK:
                mCameraRotation = mLocalStorageUtils.getCameraBackRotation();
                break;
            case Const.CAMERA_FRONT:
                mCameraRotation = mLocalStorageUtils.getCameraFrontRotation();
                break;
        }
    }

    private void initUIAndListener() {
        mAutoFitSurfaceView = (AutoFitSurfaceView) findViewById(R.id.sv_camera);

        mCameraGridView = (CameraGridView) findViewById(R.id.grid_camera);

        mGestureView = (GestureView) findViewById(R.id.view_gesture);
        mGestureView.setOnZoomScaleListener(this);
        mGestureView.setOnFocusListener(this);

        mFocusImage = (FocusView) findViewById(R.id.img_focus);
        mFocusImage.setVisibility(View.INVISIBLE);
        mFocusImage.setOnTriggerFocusListener(this);
        mFocusImage.setOnFocusStateChangedListener(this);
        mWindowTextView = (AnimationTextView) findViewById(R.id.txt_timer);
        mWindowTextView.setVisibility(View.INVISIBLE);
        mWindowTextView.setOnAnimationTextViewListener(this);

        mMenuLayout = (MenuLayout) findViewById(R.id.layout_menu);
        mMenuLayout.setOnLayoutItemClickListener(this);

        mIsoView = (IsoView) findViewById(R.id.pb_iso);
        mIsoView.setOnValueChangedListener(this);
        mIsoView.setOnIsoViewOnTouchedListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCameraModel == null) {
            mCameraModel = new CameraModel(getApplicationContext(), holder, mCategoryId);
            mCameraModel.onCreate(CameraActivity.this);
        }
        mCameraModel.openCamera(mCameraId, mCameraRotation);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Size previewSize = getSuitablePreviewSize(mCameraModel.getSettingModel().getPreviewSizes());
        setPreviewSize(previewSize);
        Size size = null;
        try {
            size = mLocalStorageUtils.getPictureSize(mCameraId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCameraModel.getSettingModel().setPictureSize(size.getWidth(), size.getHeight());
        mCameraModel.startPreview();
        initUiParams();
    }

    private void initUiParams() {
        initFocusImage();
        setCameraIds2UI();
        setFlash2UI();
        setTorch2UI();
        setWB2UI();
        setTimer2UI();
        setSound2UI();
        setSize2UI();
        setExposureCompensation2UI();
        setZoomText("0");
        setLocation2UI();
        setGrid2UI();
    }

    private void setZoomText(String zoom) {
        mMenuLayout.setZoomText(zoom);
    }

    private void setLocation2UI() {
        int[] icons = new int[]{R.drawable.ic_camera_location_off, R.drawable.ic_cameralocation_on};
        int[] array = new int[]{ICameraParams.LOCATION_OFF, ICameraParams.LOCATION_ON};
        mMenuLayout.setSupportedLocation(icons, array, mLocationOpen ? 1 : 0);
    }

    private void setExposureCompensation2UI() {
        if (mIsSaving) {
            int max = mCameraModel.getSettingModel().getMaxExposureCompensation();
            int min = mCameraModel.getSettingModel().getMinExposureCompensation();
            float percent = 1 - ((float) (mExposureCompensation + Math.abs(min))) / (max + Math.abs(min));
            mIsoView.setValue((int) (mIsoView.getValueMax() * percent));
            mCameraModel.getSettingModel().setExposureCompensation(mExposureCompensation);
        } else {
            mIsoView.setValue(mIsoView.getValueMax() / 2);
        }

    }

    private void setCameraIds2UI() {
        int number = mCameraModel.getSettingModel().getNumberOfCameras();
        int[] icons, array;
        if (number == 1) {
            icons = new int[]{R.drawable.ic_camera_rear};
            array = new int[]{Const.CAMERA_ID_REAR};
        } else if (number == 2) {
            icons = new int[]{R.drawable.ic_camera_rear, R.drawable.ic_camera_front};
            array = new int[]{Const.CAMERA_ID_REAR, Const.CAMERA_ID_FRONT};
        } else {
            throw new IllegalArgumentException("不支持没摄像头或者两个以上摄像头");
        }
        int current = 0;
        if (mCameraId.equals(Const.CAMERA_FRONT)) {
            current = 1;
        }
        mMenuLayout.setSupportedCameraIds(icons, array, current);
    }

    private void setWB2UI() {
        List<Integer> list = mCameraModel.getSettingModel().getSupportedWhiteBalance();
        int[] icons = new int[list.size()];
        int[] array = new int[list.size()];
        int current = 0;
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
            if (current == list.get(i)) {
                current = i;
            }
            switch (list.get(i)) {
                case ICameraParams.WHITE_BALANCE_AUTO:
                    icons[i] = R.drawable.ic_camera_wb_auto;
                    break;
                case ICameraParams.WHITE_BALANCE_CLOUDY_DAYLIGHT:
                    icons[i] = R.drawable.ic_camera_wb_cloudy_daylight;
                    break;
                case ICameraParams.WHITE_BALANCE_DAYLIGHT:
                    icons[i] = R.drawable.ic_camera_wb_daylight;
                    break;
                case ICameraParams.WHITE_BALANCE_FLUORESCENT:
                    icons[i] = R.drawable.ic_camera_wb_fluorescent;
                    break;
                case ICameraParams.WHITE_BALANCE_INCANDESCENT:
                    icons[i] = R.drawable.ic_camera_wb_incandescent;
                    break;
                case ICameraParams.WHITE_BALANCE_SHADE:
                    icons[i] = R.drawable.ic_camera_wb_shade;
                    break;
                case ICameraParams.WHITE_BALANCE_TWILIGHT:
                    icons[i] = R.drawable.ic_camera_wb_twilight;
                    break;
                case ICameraParams.WHITE_BALANCE_WARM_FLUORESCENT:
                    icons[i] = R.drawable.ic_camera_wb_warm_fluorescent;
                    break;
            }
            mMenuLayout.setSupportedWB(icons, array, current);
        }
    }

    private void setGrid2UI() {
        int current = mGridOpen ? 1 : 0;
        int[] icons = new int[]{R.drawable.ic_camera_grid, R.drawable.ic_camera_grid_sel};
        int[] array = new int[]{Const.CAMERA_PARAMS_GRID_OFF, Const.CAMERA_PARAMS_GRID_ON};
        mMenuLayout.setSupportedGrid(icons, array, current);
        if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_1_1) {
            mCameraGridView.setMargin(true);
        } else {
            mCameraGridView.setMargin(false);
        }
        if (mGridOpen) {
            mCameraGridView.open();
        } else {
            mCameraGridView.close();
        }
    }

    private void setFlash2UI() {
        if (!mCameraModel.getSettingModel().isFlashSupported()) {
            int[] icons = new int[]{R.drawable.ic_flash_off};
            int[] array = new int[]{ICameraParams.NOTHING};
            mMenuLayout.setSupportedFlash(icons, array, 0);
            return;
        }
        List<Integer> list = mCameraModel.getSettingModel().getSupportedFlash();
        int[] icons = new int[list.size()];
        int[] array = new int[list.size()];
        int current = 0;
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
            if (mFlashState == list.get(i)) {
                current = i;
            }
            switch (list.get(i)) {
                case ICameraParams.FLASH_OFF:
                    icons[i] = R.drawable.ic_flash_off;
                    break;
                case ICameraParams.FLASH_AUTO:
                    icons[i] = R.drawable.ic_flash_auto;
                    break;
                case ICameraParams.FLASH_ON:
                    icons[i] = R.drawable.ic_flash_on;
                    break;
            }
        }
        mMenuLayout.setSupportedFlash(icons, array, current);
    }

    private void setTorch2UI() {
        if (!mCameraModel.getSettingModel().isFlashSupported()) {
            int[] icons = new int[]{R.drawable.ic_flash_video_off};
            int[] array = new int[]{ICameraParams.NOTHING};
            mMenuLayout.setSupportedTorch(icons, array, 0);
            return;
        }
        List<Integer> list = mCameraModel.getSettingModel().getSupportedFlash();
        int[] icons = null;
        int[] array = null;
        for (int flash : list) {
            if (flash == ICameraParams.FLASH_TORCH) {
                icons = new int[]{R.drawable.ic_flash_video_off, R.drawable.ic_flash_video_on};
                array = new int[]{ICameraParams.FLASH_TORCH_OFF, ICameraParams.FLASH_TORCH};
            }
        }
        if (icons == null && array == null) {
            icons = new int[]{R.drawable.ic_flash_video_off};
            array = new int[]{ICameraParams.NOTHING};
        }
        int current = mTorchOpen ? 1 : 0;
        mMenuLayout.setSupportedTorch(icons, array, current);
    }

    private void setTimer2UI() {
        int current = 0;
        switch (mTimerState) {
            case Const.LAYOUT_PERSONAL_TIMER_0:
                current = 0;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_3:
                current = 1;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_5:
                current = 2;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_10:
                current = 3;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_15:
                current = 4;
                break;
        }
        mMenuLayout.setTimerUI(current);
    }

    private void setSound2UI() {
        int current = mSoundOpen ? 1 : 0;
        int[] icons = new int[]{R.drawable.ic_volume_off_black_24dp, R.drawable.ic_volume_up_black_24dp};
        int[] array = new int[]{Const.CAMERA_PARAMS_SOUND_OFF, Const.CAMERA_PARAMS_SOUND_ON};
        mMenuLayout.setSupportedSound(icons, array, current);
    }

    private void setSize2UI() {
        if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_1_1) {
            mMenuLayout.setRatio11();
        } else if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_4_3) {
            mMenuLayout.setRatio43();
        } else if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
            mMenuLayout.setRatioFull();
        }
        mMenuLayout.setSizeUI(mSizeState);
    }


    /**
     * 设置预览尺寸
     *
     * @param previewSize
     */
    private void setPreviewSize(Size previewSize) {
        mAutoFitSurfaceView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
        mCameraGridView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
        mViewWidth = mAutoFitSurfaceView.getWidth();
        mViewHeight = mAutoFitSurfaceView.getHeight();
        mCameraModel.setTouchArea(previewSize.getHeight(), previewSize.getWidth());
        mCameraModel.getSettingModel().setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraModel != null) {
//            mCameraModel.openCamera(mCameraId, mCameraRotation);
//            mCameraModel.startPreview();
        } else {
            mAutoFitSurfaceView.getHolder().addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraModel != null) {
            mCameraModel.stopPreview();
            mCameraModel.closeCamera();
        }
        if (mIsSaving) {
            mLocalStorageUtils.setCameraSaveTimer(mTimerState);
            mLocalStorageUtils.setCameraSaveCameraId(mCameraId);
            mLocalStorageUtils.setCameraSaveFlash(mFlashState);
            mLocalStorageUtils.setCameraSoundOpen(mSoundOpen);
            mLocalStorageUtils.setCameraPreviewRatio(mSizeState);
            mLocalStorageUtils.setCameraExposureCompensation(mExposureCompensation);
            mLocalStorageUtils.setCameraLocation(mLocationOpen);
            mLocalStorageUtils.setCameraWhiteBalance(mWBState);
            mLocalStorageUtils.setCameraGridOpen(mGridOpen);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraModel != null) {
            mCameraModel.closeCamera();
            mCameraModel.onDestroy(CameraActivity.this);
            destroySensor();
        }
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
        float screenScale = mScreenHeight / (float) mScreenWidth;
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
            if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_1_1) {
                previewSize = m43Size;
                mMenuLayout.setRatio11();
            } else if (mSizeState == Const.LAYOUT_PERSONAL_RATIO_FULL) {
                mMenuLayout.setRatio43();
                previewSize = mFullSize;
            } else {
                mMenuLayout.setRatio43();
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


    //-------------------------  parameters  -------------------------
    private void reOpenCamera() {
        getCameraRotation();
        mCameraModel.reopenCamera(mCameraId, mCameraRotation);
        Size previewSize = getSuitablePreviewSize(mCameraModel.getSettingModel().getPreviewSizes());
        setPreviewSize(previewSize);
        Size size = null;
        try {
            size = mLocalStorageUtils.getPictureSize(mCameraId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCameraModel.getSettingModel().setPictureSize(size.getWidth(), size.getHeight());
        mCameraModel.startPreview();
        initUiParams();
    }
    //-------------------------  parameters  -------------------------

    //-------------------------  Iso  -------------------------
    @Override
    public void onValueChanged(View view, int value) {
        int max = mCameraModel.getSettingModel().getMaxExposureCompensation();
        int min = mCameraModel.getSettingModel().getMinExposureCompensation();
        int finalValue = -(value * (max - min) / ((IsoView) view).getValueMax() - Math.abs(min));
        if (finalValue <= max && finalValue >= min) {
            mCameraModel.getSettingModel().setExposureCompensation(finalValue);
        }
        mExposureCompensation = finalValue;
    }

    @Override
    public void onTouched() {
        mFocusImage.delayDisappear();
    }
    //-------------------------  Iso  -------------------------


    //-------------------------  Focus  -------------------------

    @Override
    public void getMotionEvent(MotionEvent event) {
        mFocusImage.setMotionEvent(event);
    }

    /**
     * 这样做是为了获得mFocusImage的高度和宽度
     * 如果不支持的话就不显示
     */
    private void initFocusImage() {
        if (mCameraId.equals(Const.CAMERA_BACK)) {
            mFocusImage.initFocus(mViewWidth, mViewHeight);
            mIsoView.setVisibility(View.GONE);
        } else {
            mFocusImage.setNotSupport();
        }
    }

    /**
     * 设置mFocusImage为聚焦状态
     *
     * @param x
     * @param y
     */
    private boolean focusFocusing(float x, float y) {
        return mFocusImage.startFocusing(x, y);
    }

    @Override
    public void onTriggerFocus(float x, float y) {
        if (focusFocusing(x, y)) {
            mCameraModel.getFocusModel().triggerFocus((int) x, (int) y);
        }
    }

    public boolean onFocusTrigger(float x, float y) {
        if (focusFocusing(x, y)) {
            mCameraModel.getFocusModel().triggerFocus((int) x, (int) y);
        }
        return true;
    }


    @Override
    public void onBeginFocusing(float x, float y) {
        mIsoView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIsoView.getLayoutParams();
        float isoHeight = getResources().getDimension(R.dimen.iso_height);
        float leftMargin;
        if (x <= Utils.sScreenWidth / 2) {
            leftMargin = x + getResources().getDimension(R.dimen.focus_length_max) * 2 / 3;
        } else {
            leftMargin = x - getResources().getDimension(R.dimen.focus_length_max);
        }
        layoutParams.setMargins((int) (leftMargin), (int) (y - isoHeight / 2), 0, 0);
    }

    @Override
    public void onBeginMoving() {
        mIsoView.setVisibility(View.GONE);
    }

    @Override
    public void onFocusDisappeared() {
        mIsoView.setVisibility(View.GONE);
    }
    //-------------------------  Focus  -------------------------

    @Override
    public void onClick(View v, int item) {
        switch (item) {
            case Const.LAYOUT_PERSONAL_RATIO_4_3:
                mSizeState = item;
                setPreviewSize(m43Size);
                mMenuLayout.setRatio43();
                mCameraGridView.setMargin(false);
                break;
            case Const.LAYOUT_PERSONAL_RATIO_FULL:
                mSizeState = item;
                setPreviewSize(mFullSize);
                mMenuLayout.setRatioFull();
                mCameraGridView.setMargin(false);
                break;
            case Const.LAYOUT_PERSONAL_RATIO_1_1:
                mSizeState = item;
                mMenuLayout.setRatio11();
                mCameraGridView.setMargin(true);
                break;
            case Const.LAYOUT_MAIN_CAPTURE:
                //如果有倒计时并且没有在倒计时，就进行倒计时
                if (mTimerNumber != 0 && !mWindowTextView.isCountDown()) {
                    mMenuLayout.setCaptureImage(getResources().getDrawable(R.drawable.btn_cancel_capture));
                    mWindowTextView.start(mTimerNumber);
                } else if (mWindowTextView.isCountDown()) {//如果在进行倒计时，则取消
                    mMenuLayout.setCaptureImage(getResources().getDrawable(R.drawable.btn_camera_capture));
                    mWindowTextView.interrupt();
                } else if (mTimerNumber == 0) {//如果没有倒计时，直接拍照
                    mCameraModel.capture(mSoundOpen, mSizeState, mLocalStorageUtils.getCameraMirrorOpen());
                }
                break;
            case Const.LAYOUT_PERSONAL_TIMER_0:
                mTimerState = item;
                mTimerNumber = 0;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_3:
                mTimerState = item;
                mTimerNumber = 3;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_5:
                mTimerState = item;
                mTimerNumber = 5;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_10:
                mTimerState = item;
                mTimerNumber = 10;
                break;
            case Const.LAYOUT_PERSONAL_TIMER_15:
                mTimerState = item;
                mTimerNumber = 15;
                break;
            case Const.CAMERA_ID_FRONT:
                mCameraId = Const.CAMERA_FRONT;
                reOpenCamera();
                initFocusImage();
                break;
            case Const.CAMERA_ID_REAR:
                mCameraId = Const.CAMERA_BACK;
                reOpenCamera();
                initFocusImage();
                break;
            case ICameraParams.FLASH_OFF:
            case ICameraParams.FLASH_AUTO:
            case ICameraParams.FLASH_ON:
                if (mTorchOpen) {
                    mTorchOpen = false;
                    mMenuLayout.resetFlashAndTorch(-1, R.drawable.ic_flash_video_off);
                }
                mCameraModel.getSettingModel().setFlash(item);
                mFlashState = item;
                break;
            case ICameraParams.FLASH_TORCH:
                mMenuLayout.resetFlashAndTorch(R.drawable.ic_flash_off, -1);
                mFlashState = ICameraParams.FLASH_OFF;
                mCameraModel.getSettingModel().setFlash(item);
                mTorchOpen = true;
                break;
            case ICameraParams.FLASH_TORCH_OFF:
                mMenuLayout.resetFlashAndTorch(R.drawable.ic_flash_off, -1);
                mFlashState = ICameraParams.FLASH_OFF;
                mCameraModel.getSettingModel().setFlash(ICameraParams.FLASH_OFF);
                mTorchOpen = false;
                break;
            case ICameraParams.LOCATION_ON:
                mLocationOpen = true;
                break;
            case ICameraParams.LOCATION_OFF:
                mLocationOpen = false;
                break;
            case ICameraParams.WHITE_BALANCE_AUTO:
            case ICameraParams.WHITE_BALANCE_CLOUDY_DAYLIGHT:
            case ICameraParams.WHITE_BALANCE_DAYLIGHT:
            case ICameraParams.WHITE_BALANCE_FLUORESCENT:
            case ICameraParams.WHITE_BALANCE_INCANDESCENT:
            case ICameraParams.WHITE_BALANCE_SHADE:
            case ICameraParams.WHITE_BALANCE_TWILIGHT:
            case ICameraParams.WHITE_BALANCE_WARM_FLUORESCENT:
                mCameraModel.getSettingModel().setWhiteBalance(item);
                mWBState = item;
                break;
            case Const.CAMERA_PARAMS_GRID_OFF:
                mCameraGridView.close();
                mGridOpen = false;
                break;
            case Const.CAMERA_PARAMS_GRID_ON:
                mCameraGridView.open();
                mGridOpen = true;
                break;
            case Const.CAMERA_PARAMS_SOUND_OFF:
                mSoundOpen = true;
                break;
            case Const.CAMERA_PARAMS_SOUND_ON:
                mSoundOpen = false;
                break;
        }
    }

    //-------------------------  AnimationTextView  -------------------------
    @Override
    public void onTextCancel() {
        mMenuLayout.setCaptureImage(getResources().getDrawable(R.drawable.btn_camera_capture));
    }

    @Override
    public void onTextDisappear() {
        mMenuLayout.setCaptureImage(getResources().getDrawable(R.drawable.btn_camera_capture));
        mCameraModel.capture(mSoundOpen, mSizeState, mLocalStorageUtils.getCameraMirrorOpen());
    }

    //-------------------------  Zoom  -------------------------
    public boolean onZoomChange(float num) {
        int zoomValue = mCameraModel.getSettingModel().calculateZoom(mFirstZoomValue, mZoomCurrentSpan, num);
        if (zoomValue != -1 && mLastZoomValue != zoomValue) {
            mCameraModel.getSettingModel().setZoom(zoomValue);
            mLastZoomValue = zoomValue;
        }
        setZoomText((mCameraModel.getSettingModel().getZoom() * 100 / mCameraModel.getSettingModel().getMaxZoom()) + "");
        return true;
    }

    public boolean onZoomBegin(float currentSpan) {
        boolean isZoomSupported = mCameraModel.getSettingModel().isZoomSupported();
        if (isZoomSupported) {
            mZoomCurrentSpan = currentSpan;
            mFirstZoomValue = mCameraModel.getSettingModel().getZoom();
            mLastZoomValue = mFirstZoomValue;
        }
        return isZoomSupported;
    }
    //-------------------------  Zoom  -------------------------

    //-------------------------  Sensor  -------------------------
    private int mBeforeDegree = -1;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float ax = values[0];
            float ay = values[1];

            double g = Math.sqrt(ax * ax + ay * ay);
            double cos = ay / g;
            if (cos > 1) {
                cos = 1;
            } else if (cos < -1) {
                cos = -1;
            }
            double rad = Math.acos(cos);
            if (ax < 0) {
                rad = 2 * Math.PI - rad;
            }

            int uiRot = getWindowManager().getDefaultDisplay().getRotation();
            double uiRad = Math.PI / 2 * uiRot;
            rad -= uiRad;

            int degrees = (int) (180 * rad / Math.PI);
            int inputDegree = 0;
            if (degrees < 45 && degrees >= 315) {
                inputDegree = 0;
            } else if (degrees < 135 && degrees >= 45) {
                inputDegree = 90;
            } else if (degrees < 225 && degrees >= 135) {
                inputDegree = 180;
            } else if (degrees < 315 && degrees >= 225) {
                inputDegree = 270;
            }

            if (mBeforeDegree == -1) {
                mBeforeDegree = inputDegree;
            } else if (inputDegree == mBeforeDegree) {
                return;
            }
            mBeforeDegree = inputDegree;
            mMenuLayout.dispatchRotationEvent(inputDegree);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    //-------------------------  Sensor  -------------------------
    @Override
    public void onBackPressed() {
        if (mMenuLayout.isOtherLayoutShowing()) {
            mMenuLayout.closeOtherLayout();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 初始化相机的拍照尺寸、相机个数
     */
    private void initCameraNumberAndPictureSize() throws JSONException {
        List<Size> initSizeList = mLocalStorageUtils.getPictureSizes(Const.CAMERA_BACK);
        if (initSizeList != null) {
            return;
        }
        //暂时用Camera的方法
        int total = Camera.getNumberOfCameras();
        mLocalStorageUtils.setCameraNumber(total);
        int[] cameraIds;
        if (total == 0) {
            cameraIds = new int[0];
        } else if (total == 1) {
            cameraIds = new int[]{0};
        } else {
            cameraIds = new int[]{0, 1};
        }
        for (int i = 0; i < cameraIds.length; i++) {
            try {
                List<Size> sizeList = getPictureSizeJsonArray(cameraIds[i]);
                Collections.sort(sizeList, new Comparator<Size>() {
                    @Override
                    public int compare(Size lhs, Size rhs) {
                        return -(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                    }
                });
                mLocalStorageUtils.setPictureSizes(String.valueOf(cameraIds[i]), sizeList);
                Size suitableSize = sizeList.get(0);
                mLocalStorageUtils.setPictureSize(String.valueOf(cameraIds[i]), suitableSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开这个activity的时候去判断
     * 将List的数据存为JsonArray
     *
     * @param cameraId
     * @return
     * @throws JSONException
     */
    private List<Size> getPictureSizeJsonArray(int cameraId) throws JSONException {
        Camera camera = Camera.open(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> cameraSizeList = parameters.getSupportedPictureSizes();
        camera.release();
        List<Size> sizeList = new ArrayList<>();
        for (Camera.Size size : cameraSizeList) {
            sizeList.add(Size.parseSize(size));
        }
        return sizeList;
    }


}