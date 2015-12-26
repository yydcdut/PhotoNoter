package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.setting.ISettingPresenter;
import com.yydcdut.note.mvp.v.setting.ISettingView;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;

import org.json.JSONException;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/13.
 */
public class SettingPresenterImpl implements ISettingPresenter {
    private ISettingView mSettingView;

    private Context mContext;
    private LocalStorageUtils mLocalStorageUtils;
    private RxUser mRxUser;

    private static final boolean SUPPORT_CAMERA_5_0 = false;

    @Inject
    public SettingPresenterImpl(@ContextLife("Activity") Context context, LocalStorageUtils localStorageUtils, RxUser rxUser) {
        mContext = context;
        mLocalStorageUtils = localStorageUtils;
        mRxUser = rxUser;
    }

    @Override
    public void attachView(IView iView) {
        mSettingView = (ISettingView) iView;
        mSettingView.initPreferenceSetting();
        mSettingView.initAccountSetting();
        mSettingView.initCameraSetting(mLocalStorageUtils.getCameraSystem(),
                mLocalStorageUtils.getCameraNumber());
        mSettingView.initSyncSetting(false, false);
        mSettingView.initAboutSetting();

        setStatusBarClickable();

        mRxUser.isLoginQQ()
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxUser.getQQ()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(iUser -> mSettingView.initQQ(true,
                                        iUser.getName(), iUser.getImagePath()));
                    }
                });
        mRxUser.isLoginEvernote()
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxUser.getEvernote()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(iUser -> mSettingView.initEvernote(true, iUser.getName()));
                    }
                });
    }

    @Override
    public void detachView() {

    }


    @Override
    public void onClickSettingItem(String tag) {
        switch (tag) {
            case ISettingPresenter.TAG_THEME:
                mSettingView.showThemeColorChooser(mLocalStorageUtils.getThemeColor());
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
                boolean isSystem = mLocalStorageUtils.getCameraSystem();
                mLocalStorageUtils.setCameraSystem(!isSystem);
                mSettingView.setCameraSettingClickable(!isSystem,
                        mLocalStorageUtils.getCameraNumber());
                break;
            case ISettingPresenter.TAG_CAMERA2:
                boolean use = mLocalStorageUtils.getCameraSystem();
                if ((!AppCompat.AFTER_LOLLIPOP || !SUPPORT_CAMERA_5_0) && !use) {
                    mSettingView.showSnackbar(mContext.getString(R.string.toast_not_support));
                    return;
                }
                break;
            case ISettingPresenter.TAG_CAMERA_SIZE:
                if (mLocalStorageUtils.getCameraSystem()) {
                    break;
                }
                int numbers = mLocalStorageUtils.getCameraNumber();
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
                if (mLocalStorageUtils.getCameraSystem()) {
                    break;
                }
                boolean isSave = mLocalStorageUtils.getCameraSaveSetting();
                mLocalStorageUtils.setCameraSaveSetting(!isSave);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA_SAVE, !isSave);
                break;
            case ISettingPresenter.TAG_CAMERA_MIRROR:
                if (mLocalStorageUtils.getCameraSystem()) {
                    break;
                }
                boolean open = mLocalStorageUtils.getCameraMirrorOpen();
                mLocalStorageUtils.setCameraMirrorOpen(!open);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA_MIRROR, !open);
                break;
            case ISettingPresenter.TAG_SYNC_AUTO:
            case ISettingPresenter.TAG_SYNC_WIFI:
                mSettingView.showSnackbar(mContext.getString(R.string.toast_not_support));
                break;
            case ISettingPresenter.TAG_SPLASH:
                boolean splashOpen = mLocalStorageUtils.getSplashOpen();
                mLocalStorageUtils.setSplashOpen(!splashOpen);
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
        mLocalStorageUtils.setThemeColor(index);
        mSettingView.restartActivity();
    }

    @Override
    public void onUseSystemFontSelected(boolean use) {
        mLocalStorageUtils.setSettingFontSystem(use);
    }

    @Override
    public void onStatusBarStyleSelected(boolean translate) {
        mLocalStorageUtils.setStatusBarTranslation(translate);
        mSettingView.restartActivity();
    }

    @Override
    public void onPictureSizeSelected(String cameraId, int index) {
        List<Size> list = null;
        try {
            list = mLocalStorageUtils.getPictureSizes(cameraId);
            Size size = list.get(index);
            mLocalStorageUtils.setPictureSize(cameraId, size);
        } catch (JSONException e) {
            e.printStackTrace();
            mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
        }
    }

    @Override
    public void onCameraIdsSelected(int index) {
        try {
            String cameraId = Const.CAMERA_BACK;
            List<Size> list = mLocalStorageUtils.getPictureSizes(cameraId);
            Size targetSize = mLocalStorageUtils.getPictureSize(cameraId);
            switch (index) {
                case 0:
                    cameraId = Const.CAMERA_BACK;
                    list = mLocalStorageUtils.getPictureSizes(cameraId);
                    targetSize = mLocalStorageUtils.getPictureSize(cameraId);
                    break;
                case 1:
                    cameraId = Const.CAMERA_FRONT;
                    list = mLocalStorageUtils.getPictureSizes(cameraId);
                    targetSize = mLocalStorageUtils.getPictureSize(cameraId);
                    break;
            }
            mSettingView.showPictureSizeChooser(cameraId, list, targetSize);
        } catch (JSONException e) {
            e.printStackTrace();
            mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
        }
    }

    @Override
    public boolean getCameraSystem() {
        return mLocalStorageUtils.getCameraSystem();
    }

    @Override
    public boolean getCameraSaveSetting() {
        return mLocalStorageUtils.getCameraSaveSetting();
    }

    @Override
    public boolean getCameraMirrorOpen() {
        return mLocalStorageUtils.getCameraMirrorOpen();
    }

    @Override
    public int getCameraNumber() {
        return mLocalStorageUtils.getCameraNumber();
    }

    @Override
    public boolean getSplashOpen() {
        return mLocalStorageUtils.getSplashOpen();
    }

    private void setStatusBarClickable() {
        if (!AppCompat.AFTER_LOLLIPOP) {
            mSettingView.setStatusBarClickable(false);
        } else {
            mSettingView.setStatusBarClickable(true);
        }
    }

}
