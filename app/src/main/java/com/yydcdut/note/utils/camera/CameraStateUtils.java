package com.yydcdut.note.utils.camera;

import com.yydcdut.note.model.camera.ICameraParams;
import com.yydcdut.note.utils.Const;

/**
 * Created by yuyidong on 16/2/10.
 */
public class CameraStateUtils {

    public static int changeFlashLogicState2UIState(int saveState) {
        switch (saveState) {
            case ICameraParams.FLASH_AUTO:
                return 1;
            case ICameraParams.FLASH_OFF:
                return 0;
            case ICameraParams.FLASH_ON:
                return 2;
            default:
                return 0;
        }
    }

    public static int changeFlashUIState2LogicState(int state) {
        switch (state) {
            case 1:
                return ICameraParams.FLASH_AUTO;
            case 2:
                return ICameraParams.FLASH_ON;
            case 0:
            default:
                return ICameraParams.FLASH_OFF;
        }
    }

    public static int changeTimerLogicState2UIState(int saveState) {
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

    public static int changeTimerUIState2LogicState(int state) {
        switch (state) {
            case 1:
                return Const.LAYOUT_PERSONAL_TIMER_3;
            case 2:
                return Const.LAYOUT_PERSONAL_TIMER_10;
            case 0:
            default:
                return Const.LAYOUT_PERSONAL_TIMER_0;
        }
    }

    public static int changeCameraIdLogicState2UIState(String cameraId) {
        return Const.CAMERA_BACK.equals(cameraId) ? 0 : 1;
    }

    public static int changeGridLogicState2UIState(boolean open) {
        return open ? 1 : 0;
    }

    public static boolean changeGridUIState2LogicState(int state) {
        return state == 1 ? true : false;
    }

    public static int changeRatioLogicState2UIState(int ratioState) {
        switch (ratioState) {
            case Const.LAYOUT_PERSONAL_RATIO_1_1:
                return 2;
            case Const.LAYOUT_PERSONAL_RATIO_4_3:
                return 1;
            case Const.LAYOUT_PERSONAL_RATIO_FULL:
            default:
                return 0;
        }
    }

    public static int changeRatioUIState2LogicState(int state) {
        switch (state) {
            case 1:
                return Const.LAYOUT_PERSONAL_RATIO_4_3;
            case 2:
                return Const.LAYOUT_PERSONAL_RATIO_1_1;
            case 0:
            default:
                return Const.LAYOUT_PERSONAL_RATIO_FULL;
        }
    }

    public static int changeRatioState2SandBoxState(int state) {
        switch (state) {
            case Const.LAYOUT_PERSONAL_RATIO_1_1:
                return Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1;
            case Const.LAYOUT_PERSONAL_RATIO_4_3:
                return Const.CAMERA_SANDBOX_PHOTO_RATIO_4_3;
            case Const.LAYOUT_PERSONAL_RATIO_FULL:
            default:
                return Const.CAMERA_SANDBOX_PHOTO_RATIO_FULL;
        }
    }
}
