package com.yydcdut.note.views.camera;

import android.os.RemoteException;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraView extends IView {

    void setSize(int w, int h);

    void add2Service(java.lang.String fileName, int size, java.lang.String cameraId, long time,
                     int categoryId, boolean isMirror, int ratio, int orientation,
                     java.lang.String latitude, java.lang.String longitude, int whiteBalance,
                     int flash, int imageLength, int imageWidth, java.lang.String make,
                     java.lang.String model, int imageFormat) throws RemoteException;

    int getPreviewViewWidth();

    int getPreviewViewHeight();

    int getTopViewHeight();

    void showToast(String toast);

    void initState(int currentFlash, int[] flashDrawableRes,
                   int currentRatio,
                   int currentTimer,
                   int currentGrid,
                   int currentCameraId, int[] cameraIdDrawableRes);

    void do43RatioAnimation();

    void do11RatioAnimation();

    void doFullRatioAnimation();

    void setGridUI(boolean show, int top, int bottom, int previewWidth, int previewHeight);

    void startTimer(int time);

    boolean isTimerCounting();

    void interruptTimer();

    void initFocus(boolean visible);

    int getIsoViewMaxValue();

    void setIsoViewValue(int value);
}
