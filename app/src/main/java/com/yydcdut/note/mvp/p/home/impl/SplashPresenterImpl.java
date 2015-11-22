package com.yydcdut.note.mvp.p.home.impl;

import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.ISplashPresenter;
import com.yydcdut.note.mvp.v.home.ISplashView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/18.
 */
public class SplashPresenterImpl implements ISplashPresenter, Handler.Callback {
    private ISplashView mSplashView;

    private Handler mHandler;

    private static final int MESSAGE_WHAT = 1;

    private PhotoNoteDBModel mPhotoNoteDBModel;
    private LocalStorageUtils mLocalStorageUtils;

    @Inject
    public SplashPresenterImpl(PhotoNoteDBModel photoNoteDBModel, LocalStorageUtils localStorageUtils) {
        mPhotoNoteDBModel = photoNoteDBModel;
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
        if (mHandler != null && mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.removeMessages(MESSAGE_WHAT);
        }
    }

    @Override
    public void initGlobalData() {
        //todo 这个是耗时操作，最好放线程，或者一天检查一次就够了
        if (!mLocalStorageUtils.isFirstTime()) {
            int dbNumber = mPhotoNoteDBModel.getAllNumber();
            File file = new File(FilePathUtils.getPath());
            int fileNumber = 0;
            File[] fileArr = file.listFiles();
            for (File file1 : fileArr) {
                if (file1.isDirectory()) {
                    continue;
                }
                if (file1.getName().toLowerCase().endsWith("jpg") ||
                        file1.getName().toLowerCase().endsWith("png") ||
                        file1.getName().toLowerCase().endsWith("jpeg")) {
                    fileNumber++;
                }
            }
            if (fileNumber != dbNumber) {
                mSplashView.startCheckService();
            }
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
}
