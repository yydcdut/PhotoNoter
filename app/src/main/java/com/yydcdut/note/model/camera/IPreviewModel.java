package com.yydcdut.note.model.camera;

import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

/**
 * Created by yuyidong on 16/2/15.
 */
public interface IPreviewModel {
    void startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                      OnCameraPreviewCallback callback, Size previewSize);

    void continuePreview();

    void stopPreview();

    boolean isPreview();

    interface OnCameraPreviewCallback {
        void onPreview(ICaptureModel captureModel, ICameraFocus cameraFocus);

        void onPreviewError();
    }
}
