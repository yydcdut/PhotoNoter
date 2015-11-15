package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.ISettingPresenter;
import com.yydcdut.note.mvp.v.setting.ISettingView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.LollipopCompat;

import org.json.JSONException;

import java.util.List;

/**
 * Created by yuyidong on 15/11/13.
 */
public class SettingPresenerImpl implements ISettingPresenter {
    private ISettingView mSettingView;

    private Context mContext;

    private static final boolean SUPPORT_CAMERA_5_0 = false;

    public SettingPresenerImpl() {
    }

    @Override
    public void attachView(IView iView) {
        mSettingView = (ISettingView) iView;
        mContext = NoteApplication.getContext();
        mSettingView.initPreferenceSetting();
        mSettingView.initAccountSetting();
        mSettingView.initCameraSetting(LocalStorageUtils.getInstance().getCameraSystem(),
                LocalStorageUtils.getInstance().getCameraNumber());
        mSettingView.initSyncSetting(false, false);
        mSettingView.initAboutSetting();

        setStatusBarClickabe();
        boolean isQQLogin = UserCenter.getInstance().isLoginQQ();
        if (isQQLogin) {
            mSettingView.initQQ(true, UserCenter.getInstance().getQQ().getName(),
                    UserCenter.getInstance().getQQ().getImagePath());
        }
        boolean isEvernoteLogin = UserCenter.getInstance().isLoginEvernote();
        if (isEvernoteLogin) {
            mSettingView.initEvernote(true, UserCenter.getInstance().getEvernote().getUsername());
        }

    }

    @Override
    public void detachView() {

    }


    @Override
    public void onClickSettingItem(String tag) {
        switch (tag) {
            case ISettingPresenter.TAG_THEME:
                mSettingView.showThemeColorChooser(LocalStorageUtils.getInstance().getThemeColor());
                break;
            case ISettingPresenter.TAG_STATUS_BAR:
                mSettingView.showStatusBarStyleChooser();
                break;
            case ISettingPresenter.TAG_FONT:
                mSettingView.showFontChooser();
                break;
            case ISettingPresenter.TAG_CATEGORY:
                mSettingView.jump2EditCategoryActivity();
                break;
            case ISettingPresenter.TAG_CAMERA_FIX:
                mSettingView.jump2CameraFixActivity();
                break;
            case ISettingPresenter.TAG_CAMERA_SYSTEM:
                boolean isSystem = LocalStorageUtils.getInstance().getCameraSystem();
                LocalStorageUtils.getInstance().setCameraSystem(!isSystem);
                mSettingView.setCameraSettingClickable(!isSystem,
                        LocalStorageUtils.getInstance().getCameraNumber());
                break;
            case ISettingPresenter.TAG_CAMERA2:
                boolean use = LocalStorageUtils.getInstance().getCameraSystem();
                if ((!LollipopCompat.AFTER_LOLLIPOP || !SUPPORT_CAMERA_5_0) && !use) {
                    mSettingView.showSnackbar(mContext.getString(R.string.toast_not_support));
                    return;
                }
                break;
            case ISettingPresenter.TAG_CAMERA_SIZE:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                int numbers = LocalStorageUtils.getInstance().getCameraNumber();
                if (numbers == 2) {
                    try {
                        mSettingView.showCameraIdsChooser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
                    }
                }
                break;
            case ISettingPresenter.TAG_CAMERA_SAVE:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                boolean isSave = LocalStorageUtils.getInstance().getCameraSaveSetting();
                LocalStorageUtils.getInstance().setCameraSaveSetting(!isSave);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA_SAVE, !isSave);
                break;
            case ISettingPresenter.TAG_CAMERA_MIRROR:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                boolean open = LocalStorageUtils.getInstance().getCameraMirrorOpen();
                LocalStorageUtils.getInstance().setCameraMirrorOpen(!open);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA_MIRROR, !open);
                break;
            case ISettingPresenter.TAG_SYNC_AUTO:
            case ISettingPresenter.TAG_SYNC_WIFI:
                mSettingView.showSnackbar(mContext.getString(R.string.toast_not_support));
                break;
            case ISettingPresenter.TAG_SPLASH:
                boolean splashOpen = LocalStorageUtils.getInstance().getSplashOpen();
                LocalStorageUtils.getInstance().setSplashOpen(!splashOpen);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_SPLASH, splashOpen);
                break;
            case ISettingPresenter.TAG_FEEDBACK:
                mSettingView.jump2FeedbackActivity();
                break;
            case ISettingPresenter.TAG_ABOUT:
                mSettingView.jump2AboutActivity();
                break;
        }
    }

    @Override
    public void onThemeSelected(int index) {
        LocalStorageUtils.getInstance().setThemeColor(index);
        mSettingView.restartActivity();
    }

    @Override
    public void onUseSystemFontSelected(boolean use) {
        LocalStorageUtils.getInstance().setSettingFontSystem(use);
    }

    @Override
    public void onStatusBarStyleSelected(boolean translate) {
        LocalStorageUtils.getInstance().setStatusBarTranslation(translate);
        mSettingView.restartActivity();
    }

    @Override
    public void onPictureSizeSelected(String cameraId, int index) {
        List<Size> list = null;
        try {
            list = LocalStorageUtils.getInstance().getPictureSizes(cameraId);
            Size size = list.get(index);
            LocalStorageUtils.getInstance().setPictureSize(cameraId, size);
        } catch (JSONException e) {
            e.printStackTrace();
            mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
        }
    }

    @Override
    public void onCameraIdsSelected(int index) {
        try {
            String cameraId = Const.CAMERA_BACK;
            List<Size> list = LocalStorageUtils.getInstance().getPictureSizes(cameraId);
            Size targetSize = LocalStorageUtils.getInstance().getPictureSize(cameraId);
            switch (index) {
                case 0:
                    cameraId = Const.CAMERA_BACK;
                    list = LocalStorageUtils.getInstance().getPictureSizes(cameraId);
                    targetSize = LocalStorageUtils.getInstance().getPictureSize(cameraId);
                    break;
                case 1:
                    cameraId = Const.CAMERA_FRONT;
                    list = LocalStorageUtils.getInstance().getPictureSizes(cameraId);
                    targetSize = LocalStorageUtils.getInstance().getPictureSize(cameraId);
                    break;
            }
            mSettingView.showPictureSizeChooser(cameraId, list, targetSize);
        } catch (JSONException e) {
            e.printStackTrace();
            mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
        }
    }

    private void setStatusBarClickabe() {
        if (!LollipopCompat.AFTER_LOLLIPOP) {
            mSettingView.setStatusBarClickable(false);
        } else {
            mSettingView.setStatusBarClickable(true);
        }
    }

}
