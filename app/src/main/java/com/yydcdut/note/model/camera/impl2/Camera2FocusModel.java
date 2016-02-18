package com.yydcdut.note.model.camera.impl2;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;

import com.yydcdut.note.model.camera.ICameraFocus;

/**
 * Created by yuyidong on 16/2/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2FocusModel implements ICameraFocus {

    private int mState = FOCUS_STATE_IDLE;


    @Override
    public int getFocusState() {
        return 0;
    }

    @Override
    public void triggerFocus(int viewWidth, int viewHeight, int x, int y) {

    }

    @Override
    public void cancelFocus() {

    }

    public void onCaptureProgressed(CaptureRequest request, CaptureResult result) {
        judgeFocus(result);
    }

    public void onCaptureCompleted(CaptureRequest request, TotalCaptureResult result) {
        judgeFocus(result);
    }


    private void judgeFocus(CaptureResult result) {
        Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
        if (afState != null) {
            switch (afState.intValue()) {
                case CaptureResult.CONTROL_AF_STATE_INACTIVE:
                    mState = FOCUS_STATE_IDLE;
                    break;
                case CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN:
                case CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN:
                    mState = FOCUS_STATE_FOCUSING;
                    break;
                case CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED:
                case CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED:
                    mState = FOCUS_STATE_FOCUSED_GOOD;
                    break;
                case CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
                case CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
                    mState = FOCUS_STATE_FOCUSED_BAD;
                    break;
            }
        }
    }

}
