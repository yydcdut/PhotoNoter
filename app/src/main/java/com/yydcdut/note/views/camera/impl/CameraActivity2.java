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
import android.widget.Toast;

import com.yydcdut.note.ICameraData;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.camera.impl.CameraPresenterImpl;
import com.yydcdut.note.service.CameraService;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.camera.ICameraView;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;
import com.yydcdut.note.widget.camera.CameraTopView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by yuyidong on 16/2/3.
 */
public class CameraActivity2 extends BaseActivity implements ICameraView,
        AutoFitPreviewView.SurfaceListener, CameraTopView.OnItemClickListener {
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
        Bundle bundle = getIntent().getExtras();
        mCameraPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES));
        mAutoFitPreviewView.setSurfaceListener(this);
        mCameraTopView.setOnItemClickListener(this);
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
                            boolean isMirror, int ratio, int orientation, String latitude, String lontitude,
                            int whiteBalance, int flash, int imageLength, int imageWidth,
                            String make, String model, int imageFormat) throws RemoteException {
        if (mCameraService != null) {
            mCameraService.add(fileName, size, cameraId, time, categoryId, isMirror, ratio, orientation,
                    latitude, lontitude, whiteBalance, flash, imageLength, imageWidth, make, model, imageFormat);
        }
    }

    @Override
    public int getPreviewViewWidth() {
        return mAutoFitPreviewView.getWidth();
    }

    @Override
    public int getPreviewViewHeight() {
        return mAutoFitPreviewView.getHeight();
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initState(int currentFlash, int[] flashDrawableRes,
                          int currentTimer,
                          int currentGrid,
                          int currentCameraId, int[] cameraIdDrawableRes) {
        mCameraTopView.initItemState(
                currentFlash, flashDrawableRes,
                currentTimer,
                currentGrid,
                currentCameraId, cameraIdDrawableRes);
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
    public void onFlashClick(View view) {
        mCameraPresenter.onFlashClick();
    }

    @Override
    public void onTimerClick(View view) {
        mCameraPresenter.onTimerClick();
    }

    @Override
    public void onGridClick(View view) {
        mCameraPresenter.onGridClick();
    }

    @Override
    public void onCameraIdClick(View view) {
        mCameraPresenter.onCameraIdClick();
    }
}
