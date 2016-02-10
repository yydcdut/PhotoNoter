package com.yydcdut.note.presenters.camera;

import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraPresenter extends IPresenter {

    void bindData(int categoryId);

    void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, boolean sizeChanged, int width, int height);

    void onSurfaceDestroy();

    void onDown();

    void onUp();

    void onFlashClick();

    void onTimerClick();

    void onGridClick();

    void onCameraIdClick();
}
