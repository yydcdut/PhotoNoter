package com.yydcdut.note.presenters.camera;

import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraPresenter extends IPresenter {

    void bindData(int categoryId);

    void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height);

    void onSurfaceDestroy();

    void onDown();

    void onUp();

    void onFlashClick(int state);

    void onRatioClick(int state);

    void onTimerClick(int state);

    void onGridClick(int state);

    void onCameraIdClick(int state);

    void onTimerCancel();

    void onTimerFinish();

    boolean onZoomChange(float num);

    boolean onZoomBegin(float currentSpan);

    void onFocusTrigger(int viewWidth, int viewHeight, float x, float y);

    void onValueChanged(int value);

}
