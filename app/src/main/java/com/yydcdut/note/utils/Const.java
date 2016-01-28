package com.yydcdut.note.utils;

/**
 * Created by yuyidong on 15/7/16.
 */
public interface Const {
    /* intent */
    String PHOTO_POSITION = "photoPosition";
    String CATEGORY_ID_4_PHOTNOTES = "categoryId4PhotoNotes";
    String COMPARATOR_FACTORY = "camparator";
    String USER_DETAIL_TYPE = "user_detail_type";

    String WEBVIEW_URL = "webview_url";
    String WEBVIEW_TITLE = "webview_title";
    /* 动画 */
    int DURATION = 1000;
    int DURATION_ACTIVITY = 2000;
    int RADIUS = 10;
    /* 缩略图 */
    int SMALL_PHOTO_WIDTH = 500;
    /* 广播 */
    String BROADCAST_PHOTONOTE_UPDATE = "com.yydcdut.note.model.photonotedbmodel";
    String TARGET_BROADCAST_PROCESS = "target_broadcast_process";//bool
    String TARGET_BROADCAST_PHOTO = "target_broadcast_target";
    String TARGET_BROADCAST_SERVICE = "target_broadcast_target";
    String BROADCAST_CAMERA_SERVICE_KILL = "com.yydcdut.note.killcameraservice";

    /* Camera */
    String CAMERA_SIZE_WIDTH = "width";
    String CAMERA_SIZE_HEIGHT = "height";
    String CAMERA_BACK = "0";
    String CAMERA_FRONT = "1";
    int CAMERA_SANDBOX_PHOTO_RATIO_4_3 = 0;
    int CAMERA_SANDBOX_PHOTO_RATIO_1_1 = 1;
    int CAMERA_SANDBOX_PHOTO_RATIO_FULL = 2;

    int LAYOUT_MAIN_CAPTURE = 101;

    int LAYOUT_PERSONAL_RATIO_1_1 = 4200;
    int LAYOUT_PERSONAL_RATIO_4_3 = 4201;
    int LAYOUT_PERSONAL_RATIO_FULL = 4202;
    int LAYOUT_PERSONAL_TIMER_0 = 4203;
    int LAYOUT_PERSONAL_TIMER_3 = 4204;
    int LAYOUT_PERSONAL_TIMER_5 = 4205;
    int LAYOUT_PERSONAL_TIMER_10 = 4206;
    int LAYOUT_PERSONAL_TIMER_15 = 4207;

    int CAMERA_ID_REAR = 1000;
    int CAMERA_ID_FRONT = 1001;

    int CAMERA_PARAMS_GRID_OFF = 4210;
    int CAMERA_PARAMS_GRID_ON = 4211;

    int CAMERA_PARAMS_SOUND_OFF = 4220;
    int CAMERA_PARAMS_SOUND_ON = 4221;

}
