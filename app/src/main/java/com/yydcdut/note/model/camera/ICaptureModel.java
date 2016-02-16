package com.yydcdut.note.model.camera;

/**
 * Created by yuyidong on 16/2/15.
 */
public interface ICaptureModel {
    long capture(PictureReturnCallback pictureReturnCallback);

    void startStillCapture(StillPictureReturnCallback stillPictureReturnCallback);

    void stopStillCapture();

    interface PictureReturnCallback {
        void onPictureTaken(boolean success, byte[] data, long time);
    }

    interface StillPictureReturnCallback {
        void onStillPictureTaken(int imageFormat, byte[] data, long time, int width, int height);
    }
}
