package com.yydcdut.note.model.camera.impl;

import android.graphics.Rect;
import android.hardware.Camera;

import com.yydcdut.note.model.camera.ICameraFocus;
import com.yydcdut.note.utils.YLog;

import java.util.Arrays;

/**
 * Created by yuyidong on 16/2/6.
 */
public class CameraFocusModel implements ICameraFocus {
    private static final String TAG = CameraFocusModel.class.getSimpleName();

    private static final int LEFT = -1000;
    private static final int TOP = -1000;
    private static final int RIGHT = 1000;
    private static final int BOTTOM = 1000;
    private static final int WIDTH = 2000;
    private static final int HEIGHT = 2000;
    private static final int HALF_WIDTH = 1000;
    private static final int HALF_HEIGHT = 1000;

    private static final int MEASURE_WIDTH = 200;
    private static final int MEASURE_HEIGHT = 200;
    private static final int WEIGHT = 900;

    private int mState = FOCUS_STATE_IDLE;

    private Camera mCamera;

    private boolean mIsSupport = false;
    private int mViewWidth;
    private int mViewHeight;

    public CameraFocusModel(Camera camera, int viewWidth, int viewHeight) {
        this.mCamera = camera;
        mIsSupport = isSupportFocus();
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;

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
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFocusAreas(Arrays.asList(convert(calculateTapArea(viewWidth, viewHeight, x, y, 1f))));
            parameters.setMeteringAreas(Arrays.asList(convert(calculateTapArea(viewWidth, viewHeight, x, y, 2f))));
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                YLog.i(TAG, "triggerFocus   setParameters fail");
            }
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }

    /**
     * Convert touch position x:y in (-1000~1000)
     */
    private Rect calculateTapArea(int viewWidth, int viewHeight, float x, float y, float coefficient) {
        int areaSize = Float.valueOf(300 * coefficient).intValue();
        x = x / viewWidth;
        y = y / viewHeight;

        float cameraX = y;
        float cameraY = 1 - x;

        int centerX = (int) (cameraX * 2000 - 1000);
        int centerY = (int) (cameraY * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void cancelFocus() {
        mCamera.cancelAutoFocus();
    }

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                mState = FOCUS_STATE_FOCUSED_GOOD;
            } else {
                mState = FOCUS_STATE_FOCUSED_BAD;
            }
        }
    };


    /**
     * 将自己的Area转换成Camera.Area
     *
     * @return
     */
    private Camera.Area convert(int x, int y) {
        return new Camera.Area(convertArea(x, y), WEIGHT);
    }

    private Camera.Area convert(Rect rect) {
        return new Camera.Area(rect, WEIGHT);
    }

    /**
     * 判断是否支持focus
     *
     * @return
     */
    private boolean isSupportFocus() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            if (parameters.getMaxNumFocusAreas() <= 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    /**
     * 将坐标转换
     */
    @Deprecated
    private Rect convertArea(int x, int y) {
        int newX = (int) ((((float) y) / mViewHeight) * WIDTH - HALF_WIDTH);
        int newY = (int) (-((((float) x) / mViewWidth) * HEIGHT)) + HALF_HEIGHT;
        int newLeft = newX - MEASURE_WIDTH / 2;
        int newTop = newY - MEASURE_WIDTH / 2;
        int newRight = newX + MEASURE_WIDTH / 2;
        int newBottom = newY + MEASURE_WIDTH / 2;
        newLeft = newLeft < LEFT ? LEFT : newLeft;
        newTop = newTop < TOP ? TOP : newTop;
        newRight = newRight > RIGHT ? RIGHT : newRight;
        newBottom = newBottom > BOTTOM ? BOTTOM : newBottom;
        YLog.i(TAG, new Rect(newLeft, newTop, newRight, newBottom).toString());
        return new Rect(newLeft, newTop, newRight, newBottom);
    }
}
