package com.yydcdut.note.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yydcdut.note.model.camera.ICameraParams;
import com.yydcdut.note.utils.camera.param.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yuyidong on 15-4-1.
 */
public class LocalStorageUtils {

    private static final String SETTING_NAME = "Setting";

    private static final String FIRST_TIME = "first_time";
    private static final boolean FIRST_TIME_DEFAULT = true;

    private static final String INTRODUCE = "introduce";
    private static final String INTRODUCE_1_2_0 = "introduce 1.2.0";
    private static final boolean INTRODUCE_DEFAULT = false;

    private static final String START_USAGE_TIME = "start_usage_time";
    private static final long START_USAGE_TIME_DEFAULT = 0l;

    private static final String ALBUM_SORT_NAME = "album_sort";//相册排序方式
    private static final int ALBUM_SORT_DEFAULT = 1;//相册排序默认值

    private static final String THEME_NAME = "theme";//主题
    private static final int THEME_DEFAULT = 0;//主题默认

    private static final String PICTURE_SIZES_0 = "picture_sizes_0";//Camera中所有拍照图片大小 后置
    private static final String PICTURE_SIZES_1 = "picture_sizes_1";//Camera中所有拍照图片大小 前置
    private static final String PICTURE_SIZE_0 = "picture_size_1";//设置的拍照的图片大小 后置
    private static final String PICTURE_SIZE_1 = "picture_size_2";//设置的拍照的图片大小 前置
    private static final String PICTURE_DEFAULT = "";//默认拍照图片大小 后置

    private static final String CAMERA_SYSTEM = "camera_system";//是否是用系统的相机
    private static final boolean CAMERA_SYSTEM_DEFAULT = false;//默认是不使用

    private static final String CAMERA_ANDROID_LOLLIPOP = "camera_android_lollipop";
    private static final boolean CAMERA_ANDROID_LOLLIPOP_DEFAULT = false;

    private static final String CAMERA_SAVE_SETTING = "camera_save_setting";//退出时保存相机参数
    private static final boolean CAMERA_SAVE_SETTING_DEFAULT = false;//退出时保存相机参数的默认值，不保存

    private static final String CAMERA_SAVE_CAMERA_ID = "camera_save_camera_id";//相机ID
    private static final String CAMERA_SAVE_CAMERA_ID_DEFAULT = Const.CAMERA_BACK;//相机ID默认值

    private static final String CAMERA_SAVE_TIMER = "camera_save_time";//相机倒计时拍照时间
    private static final int CAMERA_SAVE_TIMER_DEFAULT = Const.LAYOUT_PERSONAL_TIMER_0;//相机倒计时拍照时间默认值

    private static final String CAMERA_SAVE_FLASH = "camera_save_flash";//相机闪光灯状态
    private static final int CAMERA_SAVE_FLASH_DEFAULT = ICameraParams.FLASH_OFF;//相机闪光灯状态默认值

    private static final String CAMERA_EXPOSURECOMPENSATION = "camera_exposureCompensation";//相机曝光
    private static final int CAMERA_EXPOSURECOMPENSATION_DEFAULT = 0;//相机曝光的默认值为0

    private static final String CAMERA_LOCATION = "camera_location";//拍照地点
    private static final boolean CAMERA_LOCATION_DEFAULT = false;//默认关闭

    private static final String CAMERA_WHITE_BALANCE = "camera_white_balance";//白平衡
    private static final int CAMERA_WHITE_BALANCE_DEFAULT = 0;//ICameraParams.WHITE_BALANCE_AUTO;//白平衡默认值为auto

    private static final String CAMERA_GRID_OPEN = "camera_grid_open";//grid是否打开
    private static final boolean CAMERA_GRID_OPEN_DEFAULT = false;//grid打开状态默认值是关闭

    private static final String CAMERA_MIRROR_OPEN = "camera_mirror_open";//是否开启镜像
    private static final boolean CAMERA_MIRROR_OPEN_DEFAULT = false;//默认镜像是关闭

    private static final String CAMERA_BACK_ROTATION = "camera_back_rotation";//后置摄像头方向
    private static final int CAMERA_BACK_ROTATION_DEFAULT = 90;//默认90

    private static final String CAMERA_FRONT_ROTATION = "camera_front_rotation";//前置摄像头方向
    private static final int CAMERA_FRONT_ROTATION_DEFAULT = 90;//默认90

    private static final String CAMERA_SOUND_OPEN = "camera_sound_open";//相机声音
    private static final boolean CAMERA_SOUND_OPEN_DEFAULT = false;//默认味关闭，0

    private static final String CAMERA_PREVIEW_RATIO = "camera_preview_ratio";//相机预览的比例
    private static final int CAMERA_PREVIEW_RATIO_DEFAULT = Const.LAYOUT_PERSONAL_RATIO_4_3;

    private static final String SETTING_FONT_SYSTEM = "setting_font_system";//是否用系统字体
    private static final boolean SETTING_FONT_SYSTEM_DEFAULT = true;//true代表用系统字体

    private static final String SETTING_SPLASH_OPEN = "setting_splash_close";//是否打开引导页
    private static final boolean SETTING_SPLASH_OPEN_DEFAULT = true;//默认开启

    private static final String SETTING_STATUS_BAR_TRANSLATION = "setting_status_bar_translation";//状态栏是透明的还是沉浸的
    private static final boolean SETTING_STATUS_BAR_TRANSLATION_DEFAULT = false;//默认沉浸

    private static final String DEVICE_UUID = "device_uuid";
    private static final String DEVICE_UUID_DEFAULT = "";

    private static final String UMENG_UID = "umeng_uid";
    private static final String UMENG_UID_DEFAULT = "umeng_uid_default";

    private static final String ALBUM_ITEM_NUMBER = "album_item_number";
    private static final int ALBUM_ITEM_NUMBER_DEFAULT = 3;

    private static SharedPreferences mSharedPreferences;

    //    @Inject
//    @Singleton
    public LocalStorageUtils(
//            @ContextLife("Application")
            Context context) {
        mSharedPreferences = context.getSharedPreferences(SETTING_NAME, Context.MODE_PRIVATE);
        sInstance = this;
    }

    private static LocalStorageUtils sInstance;

    //TODO 这里还是同一个对象吗？毕竟变成public了，很多地方都可以调用
    public static LocalStorageUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LocalStorageUtils.class) {
                if (sInstance == null) {
                    sInstance = new LocalStorageUtils(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 判断是不是第一次进入
     *
     * @return
     */
    public boolean isFirstTime() {
        boolean value = mSharedPreferences.getBoolean(FIRST_TIME, FIRST_TIME_DEFAULT);
        long startTime = getStartUsageTime();
        if (value) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(FIRST_TIME, false);
            if (startTime == START_USAGE_TIME_DEFAULT) {
                editor.putLong(START_USAGE_TIME, System.currentTimeMillis());
            }
            editor.commit();
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (startTime == START_USAGE_TIME_DEFAULT) {
                editor.putLong(START_USAGE_TIME, System.currentTimeMillis());
            }
            editor.commit();
        }
        return value;
    }

    /**
     * 是不是进入introduce界面
     *
     * @return
     */
    public boolean notGotoIntroduce() {
        boolean value = mSharedPreferences.getBoolean(INTRODUCE_1_2_0, INTRODUCE_DEFAULT);
        long startTime = getStartUsageTime();
        if (!value) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(INTRODUCE_1_2_0, true);
            if (startTime == START_USAGE_TIME_DEFAULT) {
                editor.putLong(START_USAGE_TIME, System.currentTimeMillis());
            }
            editor.commit();
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (startTime == START_USAGE_TIME_DEFAULT) {
                editor.putLong(START_USAGE_TIME, System.currentTimeMillis());
            }
            editor.commit();
        }
        return value;
    }

    /**
     * 得到第一次使用的时间
     *
     * @return
     */
    public long getStartUsageTime() {
        return mSharedPreferences.getLong(START_USAGE_TIME, START_USAGE_TIME_DEFAULT);
    }

    /**
     * 判断null
     *
     * @param object
     */
    private void checkNotNull(Object object) {
        if (null == object) {
            throw new NullPointerException("不能是null");
        }
    }

    /**
     * 设置album排序方式
     *
     * @param i
     */
    public void setSortKind(int i) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ALBUM_SORT_NAME, i);
        editor.commit();
    }

    /**
     * 获取album排序方式
     *
     * @return 1, 创建大->小;2, 创建小->大3,修改大->小;4,修改小->大;5,大小大->小;6,大小小->大;
     */
    public int getSortKind() {
        return mSharedPreferences.getInt(ALBUM_SORT_NAME, ALBUM_SORT_DEFAULT);
    }

    /**
     * 保存主题索引
     *
     * @param i
     */
    public void setThemeColor(int i) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(THEME_NAME, i);
        editor.commit();
    }

    /**
     * 获得主题索引
     *
     * @return
     */
    public int getThemeColor() {
        int colorIndex = mSharedPreferences.getInt(THEME_NAME, THEME_DEFAULT);
        if (colorIndex > 15) {
            colorIndex = 15;
        }
        return colorIndex;
    }

    /**
     * 设置picture的所有size
     *
     * @param cameraId
     * @param sizeList
     * @throws JSONException
     */
    public void setPictureSizes(String cameraId, List<Size> sizeList) throws JSONException {
        checkNotNull(cameraId);
        checkNotNull(sizeList);
        JSONArray jsonArray = new JSONArray();
        for (Size size : sizeList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Const.CAMERA_SIZE_WIDTH, size.getWidth());
            jsonObject.put(Const.CAMERA_SIZE_HEIGHT, size.getHeight());
            jsonArray.put(jsonObject);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (cameraId.equals(Const.CAMERA_BACK)) {
            editor.putString(PICTURE_SIZES_0, jsonArray.toString());
        } else {
            editor.putString(PICTURE_SIZES_1, jsonArray.toString());
        }
        editor.commit();
    }

    /**
     * 获取picture的所有size
     *
     * @param cameraId
     * @return
     * @throws JSONException
     */
    public List<Size> getPictureSizes(String cameraId) throws JSONException {
        checkNotNull(cameraId);
        String jsonArrayString;
        if (cameraId.equals(Const.CAMERA_BACK)) {
            jsonArrayString = mSharedPreferences.getString(PICTURE_SIZES_0, PICTURE_DEFAULT);
        } else {
            jsonArrayString = mSharedPreferences.getString(PICTURE_SIZES_1, PICTURE_DEFAULT);
        }
        if (jsonArrayString.equals(PICTURE_DEFAULT)) {
            return null;
        }
        List<Size> sizeList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int width = (int) jsonObject.get(Const.CAMERA_SIZE_WIDTH);
            int height = (int) jsonObject.get(Const.CAMERA_SIZE_HEIGHT);
            sizeList.add(new Size(width, height));
        }
        return sizeList;
    }

    /**
     * 设置picture的size
     *
     * @param cameraId
     * @param size
     * @throws JSONException
     */
    public void setPictureSize(String cameraId, Size size) throws JSONException {
        checkNotNull(cameraId);
        checkNotNull(size);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Const.CAMERA_SIZE_WIDTH, size.getWidth());
        jsonObject.put(Const.CAMERA_SIZE_HEIGHT, size.getHeight());
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (cameraId.equals(Const.CAMERA_BACK)) {
            editor.putString(PICTURE_SIZE_0, jsonObject.toString());
        } else {
            editor.putString(PICTURE_SIZE_1, jsonObject.toString());
        }
        editor.commit();
    }

    /**
     * 获取picture的size
     *
     * @param cameraId
     * @return
     * @throws JSONException
     */
    public Size getPictureSize(String cameraId) throws JSONException {
        checkNotNull(cameraId);
        String jsonString;
        if (cameraId.equals(Const.CAMERA_BACK)) {
            jsonString = mSharedPreferences.getString(PICTURE_SIZE_0, PICTURE_DEFAULT);
        } else {
            jsonString = mSharedPreferences.getString(PICTURE_SIZE_1, PICTURE_DEFAULT);
        }
        if (jsonString.equals(PICTURE_DEFAULT)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        int width = (int) jsonObject.get(Const.CAMERA_SIZE_WIDTH);
        int height = (int) jsonObject.get(Const.CAMERA_SIZE_HEIGHT);
        return new Size(width, height);
    }

    /**
     * 设置是否是用系统相机
     *
     * @param use true--->用
     */
    public void setCameraSystem(boolean use) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_SYSTEM, use);
        editor.commit();
    }

    /**
     * 获取是否是使用系统相机
     *
     * @return true--->用
     */
    public boolean getCameraSystem() {
        return mSharedPreferences.getBoolean(CAMERA_SYSTEM, CAMERA_SYSTEM_DEFAULT);
    }

    /**
     * 是否使用Android5.0的Camera2
     *
     * @param use
     */
    public void setCameraAndroidLollipop(boolean use) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_ANDROID_LOLLIPOP, use);
        editor.commit();
    }

    /**
     * 是否使用Android5.0的Camera2
     *
     * @return
     */
    public boolean getCameraAndroidLollipop() {
        return mSharedPreferences.getBoolean(CAMERA_ANDROID_LOLLIPOP, CAMERA_ANDROID_LOLLIPOP_DEFAULT);
    }

    /**
     * 设置是否是退出时保持相机参数
     *
     * @param save save--->存
     */
    public void setCameraSaveSetting(boolean save) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_SAVE_SETTING, save);
        editor.commit();
    }

    /**
     * 获取是否是退出时保持相机参数
     *
     * @return true--->存
     */
    public boolean getCameraSaveSetting() {
        return mSharedPreferences.getBoolean(CAMERA_SAVE_SETTING, CAMERA_SAVE_SETTING_DEFAULT);
    }

    /**
     * 保存相机ID
     *
     * @param cameraId
     */
    public void setCameraSaveCameraId(String cameraId) {
        checkNotNull(cameraId);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(CAMERA_SAVE_CAMERA_ID, cameraId);
        editor.commit();
    }

    /**
     * 获取相机ID
     *
     * @return
     */
    public String getCameraSaveCameraId() {
        return mSharedPreferences.getString(CAMERA_SAVE_CAMERA_ID, CAMERA_SAVE_CAMERA_ID_DEFAULT);
    }

    /**
     * 保存相机倒计时
     *
     * @param time
     */
    public void setCameraSaveTimer(int time) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_SAVE_TIMER, time);
        editor.commit();
    }

    /**
     * 获取相机倒计时
     *
     * @return
     */
    public int getCameraSaveTimer() {
        return mSharedPreferences.getInt(CAMERA_SAVE_TIMER, CAMERA_SAVE_TIMER_DEFAULT);
    }

    /**
     * 设置相机闪光灯
     *
     * @param flash
     */
    public void setCameraSaveFlash(int flash) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_SAVE_FLASH, flash);
        editor.commit();
    }

    /**
     * 获取相机闪光灯
     *
     * @return
     */
    public int getCameraSaveFlash() {
        return mSharedPreferences.getInt(CAMERA_SAVE_FLASH, CAMERA_SAVE_FLASH_DEFAULT);
    }

    /**
     * 设置相机的曝光
     *
     * @param exposureCompensation
     */
    public void setCameraExposureCompensation(int exposureCompensation) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_EXPOSURECOMPENSATION, exposureCompensation);
        editor.commit();
    }

    /**
     * 获得相机的曝光
     *
     * @return
     */
    public int getCameraExposureCompensation() {
        return mSharedPreferences.getInt(CAMERA_EXPOSURECOMPENSATION, CAMERA_EXPOSURECOMPENSATION_DEFAULT);
    }


    /**
     * 设置相机的location
     *
     * @param open
     */
    public void setCameraLocation(boolean open) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_LOCATION, open);
        editor.commit();
    }

    /**
     * 获得相机的location
     *
     * @return
     */
    public boolean getCameraLocation() {
        return mSharedPreferences.getBoolean(CAMERA_LOCATION, CAMERA_LOCATION_DEFAULT);
    }

    /**
     * 保存白平衡
     *
     * @param wb
     */
    public void setCameraWhiteBalance(int wb) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_WHITE_BALANCE, wb);
        editor.commit();
    }

    /**
     * 获得相机的b白平衡
     *
     * @return
     */
    public int getCameraWhiteBalance() {
        return mSharedPreferences.getInt(CAMERA_WHITE_BALANCE, CAMERA_WHITE_BALANCE_DEFAULT);
    }

    /**
     * 设置相机的Grid是否打开
     *
     * @param open
     */
    public void setCameraGridOpen(boolean open) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_GRID_OPEN, open);
        editor.commit();
    }

    /**
     * 获得相机的grid是否打开
     *
     * @return
     */
    public boolean getCameraGridOpen() {
        return mSharedPreferences.getBoolean(CAMERA_GRID_OPEN, CAMERA_GRID_OPEN_DEFAULT);
    }

    /**
     * 设置相机前置摄像头是否镜像
     *
     * @param open
     */
    public void setCameraMirrorOpen(boolean open) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_MIRROR_OPEN, open);
        editor.commit();
    }

    /**
     * 获得相机前置摄像头拍照是否镜像
     *
     * @return
     */
    public boolean getCameraMirrorOpen() {
//        return mSharedPreferences.getBoolean(CAMERA_MIRROR_OPEN, CAMERA_MIRROR_OPEN_DEFAULT);
        return false;
    }

    /**
     * 保存后置摄像头方向
     *
     * @param degree
     */
    public void setCameraBackRotation(int degree) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_BACK_ROTATION, degree);
        editor.commit();
    }

    /**
     * 获取后置摄像头方向
     *
     * @return
     */
    public int getCameraBackRotation() {
        return mSharedPreferences.getInt(CAMERA_BACK_ROTATION, CAMERA_BACK_ROTATION_DEFAULT);
    }

    /**
     * 保存前置摄像头方向
     *
     * @param degree
     */
    public void setCameraFrontRotation(int degree) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_FRONT_ROTATION, degree);
        editor.commit();
    }

    /**
     * 获取前置摄像头方向
     *
     * @return
     */
    public int getCameraFrontRotation() {
        return mSharedPreferences.getInt(CAMERA_FRONT_ROTATION, CAMERA_FRONT_ROTATION_DEFAULT);
    }

    /**
     * 保存Camera是否开启声音
     *
     * @param bool
     */
    public void setCameraSoundOpen(boolean bool) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CAMERA_SOUND_OPEN, bool);
        editor.commit();
    }

    /**
     * 获得Camera是否开启声音
     *
     * @return
     */
    public boolean getCameraSoundOpen() {
        return mSharedPreferences.getBoolean(CAMERA_SOUND_OPEN, CAMERA_SOUND_OPEN_DEFAULT);
    }

    /**
     * 保存Camera预览的比例
     *
     * @param i
     */
    public void setCameraPreviewRatio(int i) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CAMERA_PREVIEW_RATIO, i);
        editor.commit();
    }

    /**
     * 获得Camera预览的比例
     *
     * @return
     */
    public int getCameraPreviewRatioDefault() {
        return mSharedPreferences.getInt(CAMERA_PREVIEW_RATIO, CAMERA_PREVIEW_RATIO_DEFAULT);
    }

    /**
     * 得到是否用系统的
     *
     * @return
     */
    public boolean getSettingFontSystem() {
        return mSharedPreferences.getBoolean(SETTING_FONT_SYSTEM, SETTING_FONT_SYSTEM_DEFAULT);
    }

    /**
     * 保存引导页是否开启
     *
     * @param bool
     */
    public void setSplashOpen(boolean bool) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SETTING_SPLASH_OPEN, bool);
        editor.commit();
    }

    /**
     * 获得引导页是否打开
     *
     * @return
     */
    public boolean getSplashOpen() {
        return mSharedPreferences.getBoolean(SETTING_SPLASH_OPEN, SETTING_SPLASH_OPEN_DEFAULT);
    }

    /**
     * 设置是否用系统的
     *
     * @param useSystem
     */
    public void setSettingFontSystem(boolean useSystem) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SETTING_FONT_SYSTEM, useSystem);
        editor.commit();
    }

    /**
     * 获得状态栏是透明的还是沉浸的
     *
     * @return
     */
    public boolean getStatusBarTranslation() {
        return mSharedPreferences.getBoolean(SETTING_STATUS_BAR_TRANSLATION, SETTING_STATUS_BAR_TRANSLATION_DEFAULT);
    }

    /**
     * 设置状态栏味沉浸还是透明
     *
     * @param translation
     */
    public void setStatusBarTranslation(boolean translation) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SETTING_STATUS_BAR_TRANSLATION, translation);
        editor.commit();
    }

    public String getDeviceUuid() {
        String device_uuid = mSharedPreferences.getString(DEVICE_UUID, DEVICE_UUID_DEFAULT);
        if (TextUtils.isEmpty(device_uuid)) {
            device_uuid = UUID.randomUUID().toString();
            mSharedPreferences.edit().putString(DEVICE_UUID, device_uuid).commit();
        }
        return device_uuid;
    }

    public String getUmengUid() {
        return mSharedPreferences.getString(UMENG_UID, UMENG_UID_DEFAULT);
    }

    public void setUmengUid(String uid) {
        mSharedPreferences.edit().putString(UMENG_UID, uid).commit();
    }

    public int getAlbumItemNumber() {
        return mSharedPreferences.getInt(ALBUM_ITEM_NUMBER, ALBUM_ITEM_NUMBER_DEFAULT);
    }

    public void setAlbumItemNumber(int number) {
        mSharedPreferences.edit().putInt(ALBUM_ITEM_NUMBER, number).commit();
    }
}
