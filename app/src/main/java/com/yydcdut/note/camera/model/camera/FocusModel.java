package com.yydcdut.note.camera.model.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;

import com.yydcdut.note.camera.model.ICameraFocus;

import java.util.Arrays;

/**
 * Created by yyd on 15-4-13.
 */
public class FocusModel implements ICameraFocus {
    private static final String TAG = FocusModel.class.getSimpleName();

    public static final int FOCUS_STATE_IDEL = 0;
    public static final int FOCUS_STATE_FOCUSING = 1;
    public static final int FOCUS_STATE_FOCUSED_GOOD = 2;
    public static final int FOCUS_STATE_FOCUSED_BAD = 3;

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

    private int mState = FOCUS_STATE_IDEL;

    private Camera mCamera;

    private boolean mIsSupport = false;
    private int mViewWidth;
    private int mViewHeight;

    public FocusModel(Camera camera, int viewWidth, int viewHeight) {
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
            //不支持，那么状态一直为IDEL
            return FOCUS_STATE_IDEL;
        }
    }

    @Override
    public void triggerFocus(int x, int y) {
        if (mIsSupport) {
            mState = FOCUS_STATE_FOCUSING;
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFocusAreas(Arrays.asList(convert(x, y)));
            parameters.setMeteringAreas(Arrays.asList(convert(x, y)));
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                Log.i(TAG, "triggerFocus   setParameters fail");
            }
            mCamera.autoFocus(mAutoFocusCallback);
        }
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
        Log.i(TAG, new Rect(newLeft, newTop, newRight, newBottom).toString());
        return new Rect(newLeft, newTop, newRight, newBottom);
    }
}
