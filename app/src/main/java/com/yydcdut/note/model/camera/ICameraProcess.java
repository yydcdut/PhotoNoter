package com.yydcdut.note.model.camera;

import com.yydcdut.note.widget.camera.AutoFitPreviewView;

/**
 * Created by yuyidong on 16/2/3.
 */
public interface ICameraProcess {
    ICameraSettingModel openCamera(String id, int orientation);

    ICameraSettingModel reopenCamera(String id, int orientation);

    ICameraFocus startPreview(AutoFitPreviewView.PreviewSurface previewSurface,
                              int previewViewWidth, int previewViewHeight);

    long capture(PictureReturnCallback pictureReturnCallback);

    void startStillCapture(StillPictureReturnCallback stillPictureReturnCallback);

    void stopStillCapture();

    void restartPreview();

    void stopPreview();

    void closeCamera();

    boolean isOpen();

    boolean isPreview();

    interface PictureReturnCallback {
        void onPictureTaken(boolean success, byte[] data, long time);
    }

    interface StillPictureReturnCallback {
        void onStillPictureTaken(int imageFormat, byte[] data, long time);
    }
}
