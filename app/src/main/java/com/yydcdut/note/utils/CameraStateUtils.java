package com.yydcdut.note.utils;

import com.yydcdut.note.model.camera.ICameraParams;

/**
 * Created by yuyidong on 16/2/10.
 */
public class CameraStateUtils {

    public static int changeFlahsSaveState2UIState(int saveState) {
        switch (saveState) {
            case ICameraParams.FLASH_AUTO:
                return 1;
            case ICameraParams.FLASH_OFF:
                return 0;
            case ICameraParams.FLASH_ON:
                return 2;
            case ICameraParams.FLASH_TORCH:
            case ICameraParams.FLASH_TORCH_OFF:
            default:
                return 0;
        }
    }

    public static int changeTimerSaveState2UIState(int saveState) {
        switch (saveState) {
            case Const.LAYOUT_PERSONAL_TIMER_0:
                return 0;
            case Const.LAYOUT_PERSONAL_TIMER_3:
                return 1;
            case Const.LAYOUT_PERSONAL_TIMER_10:
                return 2;
            default:
                return 0;
        }
    }

    public static int changeCameraIdSaveState2UIState(String cameraId) {
        return Const.CAMERA_BACK.equals(cameraId) ? 0 : 1;
    }

    public static int changeGridSaveState2UIState(boolean open) {
        return open ? 1 : 0;
    }
}
