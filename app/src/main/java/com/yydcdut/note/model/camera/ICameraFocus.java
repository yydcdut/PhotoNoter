package com.yydcdut.note.model.camera;

/**
 * Created by yuyidong on 16/2/6.
 */
public interface ICameraFocus {
    int FOCUS_STATE_IDLE = 0;
    int FOCUS_STATE_FOCUSING = 1;
    int FOCUS_STATE_FOCUSED_GOOD = 2;
    int FOCUS_STATE_FOCUSED_BAD = 3;

    int getFocusState();

    void triggerFocus(int viewWidth, int viewHeight, int x, int y);

    void cancelFocus();

}
