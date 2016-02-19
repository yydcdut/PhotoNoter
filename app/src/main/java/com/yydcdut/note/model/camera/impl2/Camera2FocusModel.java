package com.yydcdut.note.model.camera.impl2;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;

import com.yydcdut.note.model.camera.ICameraFocus;

/**
 * Created by yuyidong on 16/2/14.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2FocusModel implements ICameraFocus {
    private static final int LENGTH = 200;
    private static final String TAG = Camera2FocusModel.class.getSimpleName();

    private int mState = FOCUS_STATE_IDLE;

    private boolean mIsSupport = false;
    private CaptureRequest.Builder mBuilder;
    private Rect mRect;

    public Camera2FocusModel(boolean isSupport, CaptureRequest.Builder builder, Rect rect) {
        mIsSupport = isSupport;
        mBuilder = builder;
        mRect = rect;
    }

    @Override
    public int getFocusState() {
        if (mIsSupport) {
            return mState;
        } else {
            //不支持，那么状态一直为IDLE
            return FOCUS_STATE_IDLE;
        }
    }

    @Override
    public void triggerFocus(int viewWidth, int viewHeight, int x, int y) {
        if (mIsSupport) {
            mState = FOCUS_STATE_FOCUSING;
            float ratioRowX = ((float) x) / viewWidth;
            float ratioRowY = ((float) y) / viewHeight;
            float newX = (ratioRowX) * mRect.height();
            float newY = (ratioRowY) * mRect.width();
            float left = newX - LENGTH < mRect.left ? mRect.left : newX - LENGTH;
            left = left > mRect.right ? mRect.right - 1 : left;
            float right = newX + LENGTH > mRect.right ? mRect.right : newX + LENGTH;
            right = right < mRect.left ? mRect.left + 1 : right;
            float top = newY - LENGTH < mRect.top ? mRect.top : newY - LENGTH;
            top = top > mRect.bottom ? mRect.bottom - 1 : top;
            float boom = newY + LENGTH > mRect.bottom ? mRect.bottom : newY + LENGTH;
            boom = boom < mRect.top ? mRect.top + 1 : boom;
            MeteringRectangle meteringRectangle = new MeteringRectangle(new Rect((int) left, (int) top, (int) right, (int) boom),
                    MeteringRectangle.METERING_WEIGHT_MAX);
            mBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{meteringRectangle});
            mBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{meteringRectangle});
            mBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
            doChange();
        }
    }

    @Override
    public void cancelFocus() {
        mBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
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

    private OnParameterChangedListener mOnParameterChangedListener;

    public void setOnParameterChangedListener(OnParameterChangedListener onParameterChangedListener) {
        mOnParameterChangedListener = onParameterChangedListener;
    }

    public interface OnParameterChangedListener {
        void onChanged(CaptureRequest.Builder builder);
    }

    private void doChange() {
        if (mOnParameterChangedListener != null) {
            mOnParameterChangedListener.onChanged(mBuilder);
        }
    }

}
