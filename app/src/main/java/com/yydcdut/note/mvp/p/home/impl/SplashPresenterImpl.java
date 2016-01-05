package com.yydcdut.note.mvp.p.home.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.ISplashPresenter;
import com.yydcdut.note.mvp.v.home.ISplashView;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.permission.Permission;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/18.
 */
public class SplashPresenterImpl implements ISplashPresenter, Handler.Callback,
        PermissionUtils.OnPermissionCallBacks {
    private ISplashView mSplashView;

    private Handler mHandler;

    private static final int MESSAGE_WHAT = 1;

    private LocalStorageUtils mLocalStorageUtils;
    private RxSandBox mRxSandBox;
    private Activity mActivity;

    private Context mContext;

    @Inject
    public SplashPresenterImpl(@ContextLife("Activity") Context context, Activity activity,
                               LocalStorageUtils localStorageUtils, RxSandBox rxSandBox) {
        mLocalStorageUtils = localStorageUtils;
        mActivity = activity;
        mRxSandBox = rxSandBox;
        mContext = context;
    }

    @Override
    public void attachView(IView iView) {
        mSplashView = (ISplashView) iView;
        checkDisks();
    }

    @Override
    public void onActivityStart() {
        if (mHandler != null && !mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 500);
        }
    }

    @Override
    public void onActivityPause() {
        if (mHandler != null && mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.removeMessages(MESSAGE_WHAT);
        }
    }

    @Override
    public void isWannaCloseSplash() {
        if (!mLocalStorageUtils.getSplashOpen()) {
            mSplashView.jump2Album();
        } else {
            mHandler = new Handler(this);
        }
    }

    @Override
    public void doingSplash() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 3000);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_WHAT) {
            if (mSplashView.isAnimationRunning()) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 500);
                return false;
            }
            if (!mLocalStorageUtils.notGotoIntroduce()) {
                mSplashView.jump2Introduce();
            } else {
                mSplashView.jump2Album();
            }
        }
        return false;
    }

    @Override
    public void detachView() {

    }

    private void checkDisks() {
        if (!mLocalStorageUtils.isFirstTime()) {
            initFiles();
            Observable.from(new File(FilePathUtils.getPath()).listFiles())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .filter(file1 -> !file1.isDirectory())
                    .filter(file -> file.getName().toLowerCase().endsWith(".jpg") ||
                            file.getName().toLowerCase().endsWith(".png") ||
                            file.getName().toLowerCase().endsWith(".jpeg"))
                    .count()
                    .subscribe(fileNumber -> {
                        mRxSandBox.getNumber()
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer dbNumber) {
                                        if (fileNumber != dbNumber) {
                                            Intent checkIntent = new Intent(mContext, CheckService.class);
                                            mContext.startService(checkIntent);
                                        }
                                    }
                                });
                    });
        }
    }

    @Permission(PermissionUtils.CODE_STORAGE)
    private void initFiles() {
        if (PermissionUtils.hasPermission4Storage(mContext)) {
            FilePathUtils.initEnvironment(mContext);
        } else {
            PermissionUtils.requestPermissions(mActivity, mContext.getResources().getString(R.string.permission_storage),
                    PermissionUtils.PERMISSION_STORAGE, PermissionUtils.CODE_STORAGE, null);
        }

    }

    @Override
    public void onPermissionsGranted(List<String> permissions) {
    }

    @Override
    public void onPermissionsDenied(List<String> permissions) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
