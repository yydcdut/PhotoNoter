package com.yydcdut.note.camera.model;

import android.content.Context;

/**
 * Created by yyd on 15-4-12.
 */
public interface ICameraModel extends ICameraProcess {

    public ICameraSetting getSettingModel();

    public ICameraFocus getFocusModel();

    public void setTouchArea(int width, int height);

    // ---------- Model的生命周期，与Activity相关联 -----------

    public void onCreate(Context context);

    public void onDestroy(Context context);

}
