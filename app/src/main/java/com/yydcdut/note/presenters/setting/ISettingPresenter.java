package com.yydcdut.note.presenters.setting;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface ISettingPresenter extends IPresenter {
    String TAG_THEME = "theme";
    String TAG_STATUS_BAR = "status_bar";
    String TAG_FLOATING = "floating_action_button";
    String TAG_FONT = "font";
    String TAG_CATEGORY = "category";
    String TAG_CAMERA2 = "camera2";
    String TAG_CAMERA_SYSTEM = "camera_system";
    String TAG_CAMERA_SIZE = "camera_size";
    String TAG_CAMERA_SAVE = "camera_save";
    String TAG_CAMERA_MIRROR = "camera_mirror";
    String TAG_CAMERA_FIX = "camera_fix";
    String TAG_SYNC_AUTO = "sync_auto";
    String TAG_SYNC_WIFI = "sync_wifi";
    String TAG_ABOUT = "about";
    String TAG_FEEDBACK = "feedback";
    String TAG_SPLASH = "splash";
    String TAG_QQ = "qq";
    String TAG_EVERNOTE = "evernote";

    void onClickSettingItem(String tag);

    void onThemeSelected(int index);

    void onUseSystemFontSelected(boolean use);

    void onStatusBarStyleSelected(boolean translate);

    void onPictureSizeSelected(String cameraId, int index);

    void onCameraIdsSelected(int index);

    boolean getCameraSystem();

    boolean getCameraAndroidLollipop();

    boolean getCameraSaveSetting();

    boolean getCameraMirrorOpen();

    int getCameraNumber();

    boolean getSplashOpen();

}
