package com.yydcdut.note.presenters.camera;

import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

/**
 * Created by yuyidong on 16/2/16.
 */
public interface IAdjustCameraPresenter extends IPresenter {
    void switchCamera();

    void clickBack();

    void clickRotation();

    void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height);

    void onSurfaceDestroy();
}
