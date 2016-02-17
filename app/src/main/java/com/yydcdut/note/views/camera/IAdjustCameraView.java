package com.yydcdut.note.views.camera;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 16/2/16.
 */
public interface IAdjustCameraView extends IView {

    void setSize(int w, int h);

    void finishActivity();
}
