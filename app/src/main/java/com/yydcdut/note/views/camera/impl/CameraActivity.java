package com.yydcdut.note.views.camera.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yydcdut.note.ICameraData;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.camera.impl.CameraPresenterImpl;
import com.yydcdut.note.service.CameraService;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.camera.ICameraView;
import com.yydcdut.note.widget.camera.AnimationTextView;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;
import com.yydcdut.note.widget.camera.CameraGridLayout;
import com.yydcdut.note.widget.camera.CameraTopView;
import com.yydcdut.note.widget.camera.FocusView;
import com.yydcdut.note.widget.camera.GestureView;
import com.yydcdut.note.widget.camera.IsoView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraActivity extends BaseActivity implements ICameraView,
        AutoFitPreviewView.SurfaceListener, CameraTopView.OnItemClickListener,
        AnimationTextView.OnAnimationTextViewListener,
        GestureView.OnZoomScaleListener, GestureView.OnFocusListener,
        FocusView.OnTriggerFocusListener, FocusView.OnFocusStateChangedListener,
        IsoView.OnValueChangedListener, IsoView.OnIsoViewOnTouchedListener {
    /* Service */
    private boolean mIsBind = false;
    private ICameraData mCameraService;

    @Inject
    CameraPresenterImpl mCameraPresenter;

    @Bind(R.id.auto_preview)
    AutoFitPreviewView mAutoFitPreviewView;

    @Bind(R.id.fab_capture)
    FloatingActionButton mCaptureBtn;

    @Bind(R.id.view_top)
    CameraTopView mCameraTopView;

    @Bind(R.id.view_ratio_cover)
    View mRatioCoverView;

    @Bind(R.id.grid_camera)
    CameraGridLayout mCameraGridLayout;

    @Bind(R.id.txt_timer)
    AnimationTextView mWindowTextView;

    @Bind(R.id.view_gesture)
    GestureView mGestureView;

    @Bind(R.id.img_focus)
    FocusView mFocusImage;

    @Bind(R.id.pb_iso)
    IsoView mIsoView;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        AppCompat.setFullWindow(getWindow());
        return R.layout.activity_camera2;
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
        mCameraPresenter.attachView(this);
        mIPresenter = mCameraPresenter;
    }

    @Override
    public void initUiAndListener() {
        if (AppCompat.AFTER_LOLLIPOP) {
            mCameraTopView.setItemsMarginTop(getStatusBarSize());
        }
        if (AppCompat.hasNavigationBar(this)) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) mCaptureBtn.getLayoutParams();
            l.bottomMargin = (int) (AppCompat.getNavigationBarHeight(this) + getResources().getDimension(R.dimen.dimen_12dip));
        }
        Bundle bundle = getIntent().getExtras();
        mCameraPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES));
        mAutoFitPreviewView.setSurfaceListener(this);
        mCameraTopView.setOnItemClickListener(this);
        mWindowTextView.setOnAnimationTextViewListener(this);
        mGestureView.setOnZoomScaleListener(this);
        mGestureView.setOnFocusListener(this);
        mFocusImage.setOnFocusStateChangedListener(this);
        mFocusImage.setOnTriggerFocusListener(this);
        mIsoView.setOnValueChangedListener(this);
        mIsoView.setOnIsoViewOnTouchedListener(this);
        mIsoView.setVisibility(View.INVISIBLE);
        bindCameraService();
    }

    @Override
    public void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height) {
        mCameraPresenter.onSurfaceAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceDestroy() {
        mCameraPresenter.onSurfaceDestroy();
    }

    @Override
    public void setSize(int w, int h) {
        mAutoFitPreviewView.setAspectRatio(w, h);
    }

    @Override
    public void add2Service(String fileName, int size, String cameraId, long time, int categoryId,
                            boolean isMirror, int ratio, int orientation, String latitude, String longitude,
                            int whiteBalance, int flash, int imageLength, int imageWidth,
                            String make, String model, int imageFormat) throws RemoteException {
        if (mCameraService != null) {
            mCameraService.add(fileName, size, cameraId, time, categoryId, isMirror, ratio, orientation,
                    latitude, longitude, whiteBalance, flash, imageLength, imageWidth, make, model, imageFormat);
        }
    }

    @Override
    public int getPreviewViewWidth() {
        return mAutoFitPreviewView.getAspectWidth();
    }

    @Override
    public int getPreviewViewHeight() {
        return mAutoFitPreviewView.getAspectHeight();
    }

    @Override
    public int getTopViewHeight() {
        int height = 0;
        if (AppCompat.AFTER_LOLLIPOP) {
            height += getStatusBarSize();
        }
        height += getResources().getDimension(R.dimen.dimen_48dip);
        return height;
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initState(int currentFlash, int[] flashDrawableRes,
                          int currentRatio,
                          int currentTimer,
                          int currentGrid,
                          int currentCameraId, int[] cameraIdDrawableRes) {
        mCameraTopView.initItemState(
                currentFlash, flashDrawableRes,
                currentRatio,
                currentTimer,
                currentGrid,
                currentCameraId, cameraIdDrawableRes);
    }

    @Override
    public void do43RatioAnimation() {
        if (mRatioCoverView.getVisibility() == View.VISIBLE) {
            mRatioCoverView.setVisibility(View.GONE);
        }
        int top = getTopViewHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAutoFitPreviewView.getLayoutParams();
        layoutParams.setMargins(0, top, 0, 0);
        mAutoFitPreviewView.setLayoutParams(layoutParams);
    }

    @Override
    public void do11RatioAnimation() {
        do43RatioAnimation();
        mRatioCoverView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRatioCoverView.getLayoutParams();
        int topMargin = getTopViewHeight();
        int width = mAutoFitPreviewView.getAspectWidth();
        int height = mAutoFitPreviewView.getAspectHeight();
        int delta = 0;
        if (width == 0 || height == 0) {
            width = Utils.sScreenWidth;
            delta = width;
        } else {
            delta = height - width;
        }
        topMargin += width;
        if (delta > 0) {
            lp.height = delta;
        }
        lp.setMargins(0, topMargin, 0, 0);
        mRatioCoverView.setLayoutParams(lp);
        mRatioCoverView.requestLayout();
    }

    @Override
    public void doFullRatioAnimation() {
        if (mRatioCoverView.getVisibility() == View.VISIBLE) {
            mRatioCoverView.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAutoFitPreviewView.getLayoutParams();
        if (layoutParams.topMargin == 0) {
            return;
        }
        layoutParams.setMargins(0, 0, 0, 0);
        mAutoFitPreviewView.setLayoutParams(layoutParams);
    }

    @Override
    public void setGridUI(boolean show, int top, int bottom, int previewWidth, int previewHeight) {
        if (show) {
            mCameraGridLayout.open();
            mCameraGridLayout.setAspectRatio(previewHeight, previewWidth);
            mCameraGridLayout.setMargin(top, bottom);
        } else {
            mCameraGridLayout.close();
        }
    }

    @Override
    public void startTimer(int time) {
        if (!mWindowTextView.isCountDown()) {
            mWindowTextView.start(time);
        }
    }

    @Override
    public boolean isTimerCounting() {
        return mWindowTextView.isCountDown();
    }

    @Override
    public void interruptTimer() {
        if (mWindowTextView.isCountDown()) {
            mWindowTextView.interrupt();
        }
    }

    @Override
    public void initFocus(boolean visible) {
        if (visible) {
            mFocusImage.initFocus(mAutoFitPreviewView.getAspectWidth(), mAutoFitPreviewView.getAspectHeight());
            mIsoView.setVisibility(View.GONE);
        } else {
            mFocusImage.setNotSupport();
        }
    }

    @Override
    public int getIsoViewMaxValue() {
        return mIsoView.getValueMax();
    }

    @Override
    public void setIsoViewValue(int value) {
        mIsoView.setValue(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindCameraService();
    }

    private void bindCameraService() {
        Intent intent = new Intent(this, CameraService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCameraService = ICameraData.Stub.asInterface(service);
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCameraService = null;
            mIsBind = false;
        }
    };

    private void unbindCameraService() {
        if (mIsBind) {
            unbindService(mServiceConnection);
            mIsBind = false;
        }
    }

    @OnTouch(R.id.fab_capture)
    public boolean onFabTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCameraPresenter.onDown();
                break;
            case MotionEvent.ACTION_UP:
                mCameraPresenter.onUp();
                break;
        }
        return true;
    }

    @Override
    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onFlashClick(View view, int state) {
        mCameraPresenter.onFlashClick(state);
    }

    @Override
    public void onRatioClick(View view, int state) {
        mCameraPresenter.onRatioClick(state);
    }

    @Override
    public void onTimerClick(View view, int state) {
        mCameraPresenter.onTimerClick(state);
    }

    @Override
    public void onGridClick(View view, int state) {
        mCameraPresenter.onGridClick(state);
    }

    @Override
    public void onCameraIdClick(View view, int state) {
        mCameraPresenter.onCameraIdClick(state);
    }

    @Override
    public void onTextCancel() {
        mCameraPresenter.onTimerCancel();
    }

    @Override
    public void onTextDisappear() {
        mCameraPresenter.onTimerFinish();
    }

    @Override
    public boolean onZoomChange(float num) {
        return mCameraPresenter.onZoomChange(num);
    }

    @Override
    public boolean onZoomBegin(float currentSpan) {
        return mCameraPresenter.onZoomBegin(currentSpan);
    }

    @Override
    public void getMotionEvent(MotionEvent event) {
        mFocusImage.setMotionEvent(event);
    }

    @Override
    public boolean onFocusTrigger(float x, float y) {
        if (focusFocusing(x, y)) {
            mCameraPresenter.onFocusTrigger(mAutoFitPreviewView.getAspectWidth(),
                    mAutoFitPreviewView.getAspectHeight(), x, y);
        }
        return true;
    }

    @Override
    public void onTriggerFocus(float x, float y) {
        if (focusFocusing(x, y)) {
            mCameraPresenter.onFocusTrigger(mAutoFitPreviewView.getAspectWidth(),
                    mAutoFitPreviewView.getAspectHeight(), x, y);
        }
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
    public void onValueChanged(View view, int value) {
        mCameraPresenter.onValueChanged(value);
    }

    @Override
    public void onTouched() {
        mFocusImage.delayDisappear();
    }
}
