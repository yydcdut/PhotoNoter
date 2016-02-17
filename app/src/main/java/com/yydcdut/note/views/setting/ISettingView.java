package com.yydcdut.note.views.setting;

import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.views.IView;

import org.json.JSONException;

import java.util.List;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface ISettingView extends IView {
    /**
     * 初始化 偏好
     */
    void initPreferenceSetting();

    /**
     * 初始化 偏好中的状态栏，因为有API的判断逻辑，所以提出来设置
     *
     * @param clickable
     */
    void setStatusBarClickable(boolean clickable);

    /**
     * 初始化 帐号
     */
    void initAccountSetting();

    /**
     * 初始化QQ
     *
     * @param isLogin
     * @param name
     * @param imagePath
     */
    void initQQ(boolean isLogin, String name, String imagePath);

    /**
     * 初始化Evernote
     *
     * @param isLogin
     * @param name
     */
    void initEvernote(boolean isLogin, String name);

    /**
     * 初始化 相机
     *
     * @param isSystem
     * @param cameraNumbers
     */
    void initCameraSetting(boolean isSystem, int cameraNumbers);

    /**
     * 当设置为系统相机之后的UI变换
     *
     * @param isSystem
     * @param cameraNumbers
     */
    void setCameraSettingClickable(boolean isSystem, int cameraNumbers);

    /**
     * 初始化 同步
     *
     * @param sysnAuto
     * @param wifi
     */
    void initSyncSetting(boolean sysnAuto, boolean wifi);

    /**
     * 初始化 关于
     */
    void initAboutSetting();

    /**
     * Theme的dialog
     */
    void showThemeColorChooser(int index);

    /**
     * 字体的dialog
     */
    void showFontChooser();

    /**
     * API>21的状态栏
     */
    void showStatusBarStyleChooser();

    /**
     * 选择前置还是后置界面
     *
     * @throws JSONException
     */
    void showCameraIdsChooser() throws JSONException;

    /**
     * 选择照片尺寸dialog
     *
     * @param cameraId
     * @param sizeList
     * @param targetSize
     * @throws JSONException
     */
    void showPictureSizeChooser(String cameraId, List<Size> sizeList, Size targetSize) throws JSONException;

    /**
     * 显示Snackbar
     *
     * @param message
     */
    void showSnackbar(String message);

    /**
     * 重启activity
     */
    void restartActivity();

    /**
     * Camera保存的checkBox
     *
     * @param check
     */
    void setCheckBoxState(String tag, boolean check);

    /**
     * 跳转EditCategoryActivity
     */
    void jump2EditCategoryActivity();

    /**
     * 跳转CameraFixActivity
     */
    void jump2CameraFixActivity();

    /**
     * 跳转FeedbackActivity
     */
    void jump2FeedbackActivity();

    /**
     * 跳转AboutActivity
     */
    void jump2AboutActivity();

    /**
     * 让Camera2那选项变不可操作的颜色
     */
    void showCamera2Gray();

}
