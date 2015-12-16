package com.yydcdut.note.mvp.p.home.impl;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.ISplashPresenter;
import com.yydcdut.note.mvp.v.home.ISplashView;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.YLog;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/18.
 */
public class SplashPresenterImpl implements ISplashPresenter, Handler.Callback {
    private ISplashView mSplashView;

    private Handler mHandler;

    private static final int MESSAGE_WHAT = 1;

    private LocalStorageUtils mLocalStorageUtils;

    @Inject
    public SplashPresenterImpl(LocalStorageUtils localStorageUtils) {
        mLocalStorageUtils = localStorageUtils;
    }

    @Override
    public void attachView(IView iView) {
        mSplashView = (ISplashView) iView;
    }


    @Override
    public void onActivityStart() {
        if (mHandler != null && !mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 500);
        }
    }

    @Override
    public void onActivityPause() {
        YLog.wtf("yuyidong", "onActivityPause");
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
            //如果还在dex的话，不进入下一个activity
//            if (!isSecondDexFinish()) {
//                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 500);
//                return false;
//            }
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

    private boolean isSecondDexFinish() {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "photo.note";
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
