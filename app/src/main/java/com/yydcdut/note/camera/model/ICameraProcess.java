package com.yydcdut.note.camera.model;


/**
 * Created by yuyidong on 15-4-9.
 */
public interface ICameraProcess {

    public void openCamera(String id, int orientation);

    public void reopenCamera(String id, int orientation);

    public void startPreview();

    public void reStartPreview();

    public void stopPreview();

    public void closeCamera();

    public long capture(boolean sound, int ratio, boolean isMirror);

}
