package com.yydcdut.note.camera.param;

import android.hardware.Camera;

import com.yydcdut.note.camera.model.ICameraParams;


/**
 * Created by yuyidong on 15/8/20.
 */
public class ParamsConvert {
    public static final int NONE = -1;

    public static int convertFlash(String string) {
        switch (string) {
            case Camera.Parameters.FLASH_MODE_ON:
                return ICameraParams.FLASH_ON;
            case Camera.Parameters.FLASH_MODE_OFF:
                return ICameraParams.FLASH_OFF;
            case Camera.Parameters.FLASH_MODE_AUTO:
                return ICameraParams.FLASH_AUTO;
            case Camera.Parameters.FLASH_MODE_TORCH:
                return ICameraParams.FLASH_TORCH;
            default:
                return NONE;
        }
    }

    public static int convertWhiteBalance(String string) {
        switch (string) {
            case Camera.Parameters.WHITE_BALANCE_AUTO:
                return ICameraParams.WHITE_BALANCE_AUTO;
            case Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT:
                return ICameraParams.WHITE_BALANCE_CLOUDY_DAYLIGHT;
            case Camera.Parameters.WHITE_BALANCE_DAYLIGHT:
                return ICameraParams.WHITE_BALANCE_DAYLIGHT;
            case Camera.Parameters.WHITE_BALANCE_FLUORESCENT:
                return ICameraParams.WHITE_BALANCE_FLUORESCENT;
            case Camera.Parameters.WHITE_BALANCE_INCANDESCENT:
                return ICameraParams.WHITE_BALANCE_INCANDESCENT;
            case Camera.Parameters.WHITE_BALANCE_SHADE:
                return ICameraParams.WHITE_BALANCE_SHADE;
            case Camera.Parameters.WHITE_BALANCE_TWILIGHT:
                return ICameraParams.WHITE_BALANCE_TWILIGHT;
            case Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT:
                return ICameraParams.WHITE_BALANCE_WARM_FLUORESCENT;
            default:
                return NONE;
        }
    }
}
