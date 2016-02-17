package com.yydcdut.note.presenters.setting.impl;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.camera.ICameraModel;
import com.yydcdut.note.model.camera.impl.CameraModelImpl;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.presenters.setting.ISettingPresenter;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.utils.permission.Permission;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.setting.ISettingView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/13.
 */
public class SettingPresenterImpl implements ISettingPresenter, PermissionUtils.OnPermissionCallBacks {
    private ISettingView mSettingView;

    private Context mContext;
    private Activity mActivity;
    private LocalStorageUtils mLocalStorageUtils;
    private RxUser mRxUser;
    private ICameraModel mCameraModel;

    @Inject
    public SettingPresenterImpl(@ContextLife("Activity") Context context, Activity activity,
                                LocalStorageUtils localStorageUtils, RxUser rxUser, CameraModelImpl cameraModel) {
        mContext = context;
        mActivity = activity;
        mLocalStorageUtils = localStorageUtils;
        mRxUser = rxUser;
        mCameraModel = cameraModel;
    }

    @Override
    public void attachView(IView iView) {
        mSettingView = (ISettingView) iView;
        mSettingView.initPreferenceSetting();
        mSettingView.initAccountSetting();
        mSettingView.initCameraSetting(mLocalStorageUtils.getCameraSystem(),
                mCameraModel.getCameraNumber(mContext));
        if (!AppCompat.AFTER_LOLLIPOP) {
            mSettingView.showCamera2Gray();
        }
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
                adjustCameraWithPermission();
                break;
            case ISettingPresenter.TAG_CAMERA_SYSTEM:
                boolean isSystem = mLocalStorageUtils.getCameraSystem();
                mLocalStorageUtils.setCameraSystem(!isSystem);
                mSettingView.setCameraSettingClickable(!isSystem,
                        mCameraModel.getCameraNumber(mContext));
                break;
            case ISettingPresenter.TAG_CAMERA2:
                boolean useSystem = mLocalStorageUtils.getCameraSystem();
                if (!AppCompat.AFTER_LOLLIPOP || useSystem) {
                    return;
                }
                boolean useCamera2 = mLocalStorageUtils.getCameraAndroidLollipop();
                mLocalStorageUtils.setCameraAndroidLollipop(!useCamera2);
                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA2, !useCamera2);
                break;
            case ISettingPresenter.TAG_CAMERA_SIZE:
                if (mLocalStorageUtils.getCameraSystem()) {
                    break;
                }
                getPictureSizeWithPermission();
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
//                if (mLocalStorageUtils.getCameraSystem()) {
//                    break;
//                }
//                boolean open = mLocalStorageUtils.getCameraMirrorOpen();
//                mLocalStorageUtils.setCameraMirrorOpen(!open);
//                mSettingView.setCheckBoxState(ISettingPresenter.TAG_CAMERA_MIRROR, !open);
                mSettingView.showSnackbar(mContext.getString(R.string.toast_not_support));
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
    public boolean getCameraAndroidLollipop() {
        return mLocalStorageUtils.getCameraAndroidLollipop();
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
        return mCameraModel.getCameraNumber(mContext);
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

    /**
     * 初始化相机的拍照尺寸、相机个数
     */
    private void initCameraNumberAndPictureSize() throws JSONException {
        List<Size> initSizeList = mLocalStorageUtils.getPictureSizes(Const.CAMERA_BACK);
        if (initSizeList != null) {
            return;
        }
        //暂时用Camera的方法
        int total = mCameraModel.getCameraNumber(mContext);
        int[] cameraIds;
        if (total == 0) {
            cameraIds = new int[0];
        } else if (total == 1) {
            cameraIds = new int[]{0};
        } else {
            cameraIds = new int[]{0, 1};
        }
        for (int i = 0; i < cameraIds.length; i++) {
            try {
                List<Size> sizeList = getPictureSizeJsonArray(cameraIds[i]);
                Collections.sort(sizeList, new Comparator<Size>() {
                    @Override
                    public int compare(Size lhs, Size rhs) {
                        return -(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                    }
                });
                mLocalStorageUtils.setPictureSizes(String.valueOf(cameraIds[i]), sizeList);
                Size suitableSize = sizeList.get(0);
                mLocalStorageUtils.setPictureSize(String.valueOf(cameraIds[i]), suitableSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将List的数据存为JsonArray
     *
     * @param cameraId
     * @return
     * @throws JSONException
     */
    private List<Size> getPictureSizeJsonArray(int cameraId) throws JSONException {
        Camera camera = Camera.open(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> cameraSizeList = parameters.getSupportedPictureSizes();
        camera.release();
        List<Size> sizeList = new ArrayList<>();
        for (Camera.Size size : cameraSizeList) {
            sizeList.add(Size.parseSize(size));
        }
        return sizeList;
    }

    @Permission(PermissionUtils.CODE_CAMERA)
    private void getPictureSizeWithPermission() {
        boolean hasPermission = PermissionUtils.hasPermission4Camera(mContext);
        if (hasPermission) {
            try {
                initCameraNumberAndPictureSize();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int numbers = mCameraModel.getCameraNumber(mContext);
            if (numbers == 2) {
                try {
                    mSettingView.showCameraIdsChooser();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSettingView.showSnackbar(mContext.getString(R.string.toast_fail));
                }
            } else {
                onCameraIdsSelected(0);
            }
        } else {
            PermissionUtils.requestPermissionsWithDialog(mActivity, mContext.getString(R.string.permission_camera_init),
                    PermissionUtils.PERMISSION_CAMERA, PermissionUtils.CODE_CAMERA);
        }
    }

    @Permission(PermissionUtils.CODE_ADJUST_CAMERA)
    private void adjustCameraWithPermission() {
        boolean hasPermission = PermissionUtils.hasPermission4Camera(mContext);
        if (hasPermission) {
            try {
                initCameraNumberAndPictureSize();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSettingView.jump2CameraFixActivity();
        } else {
            PermissionUtils.requestPermissions(mActivity, mContext.getString(R.string.permission_camera),
                    PermissionUtils.PERMISSION_CAMERA, PermissionUtils.CODE_ADJUST_CAMERA, new PermissionUtils.OnRequestPermissionDeniedByUserListener() {
                        @Override
                        public void onDenied(int requestCode) {
                            mSettingView.showSnackbar(mContext.getString(R.string.permission_cancel));
                        }
                    });
        }
    }

    @Override
    public void onPermissionsGranted(List<String> permissions) {
    }

    @Override
    public void onPermissionsDenied(List<String> permissions) {
        PermissionUtils.requestPermissions(mActivity, mContext.getString(R.string.permission_storage_init),
                PermissionUtils.PERMISSION_CAMERA, PermissionUtils.CODE_CAMERA, new PermissionUtils.OnRequestPermissionDeniedByUserListener() {
                    @Override
                    public void onDenied(int requestCode) {
                        mSettingView.showSnackbar(mContext.getString(R.string.permission_cancel));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
