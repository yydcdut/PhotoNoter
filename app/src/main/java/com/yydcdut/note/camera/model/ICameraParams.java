package com.yydcdut.note.camera.model;

/**
 * Created by yuyidong on 15/8/20.
 */
public interface ICameraParams {
    int NOTHING = -1;

    int FLASH_OFF = 0;
    int FLASH_AUTO = 1;
    int FLASH_ON = 2;
    int FLASH_TORCH = 3;
    int FLASH_TORCH_OFF = 4;

    int WHITE_BALANCE_AUTO = 10;
    int WHITE_BALANCE_CLOUDY_DAYLIGHT = 11;
    int WHITE_BALANCE_DAYLIGHT = 12;
    int WHITE_BALANCE_FLUORESCENT = 13;
    int WHITE_BALANCE_INCANDESCENT = 14;
    int WHITE_BALANCE_SHADE = 15;
    int WHITE_BALANCE_TWILIGHT = 16;
    int WHITE_BALANCE_WARM_FLUORESCENT = 17;

    int LOCATION_OFF = 20;
    int LOCATION_ON = 21;


}
