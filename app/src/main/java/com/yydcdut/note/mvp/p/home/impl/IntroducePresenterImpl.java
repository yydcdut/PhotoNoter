package com.yydcdut.note.mvp.p.home.impl;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.IIntroducePresenter;
import com.yydcdut.note.mvp.v.home.IIntroduceView;
import com.yydcdut.note.service.InitService;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/18.
 */
public class IntroducePresenterImpl implements IIntroducePresenter, Handler.Callback {
    private IIntroduceView mIntroduceView;
    private InitService.InitBinder mInitBinder;
    /**
     * 与InitService的连接
     * 主要是判断当点击“start”之后，Service有没有初始化完，初始化完才跳转
     */
    private ServiceConnection mServiceConnection;

    private Handler mHandler;

    @Inject
    public IntroducePresenterImpl() {
    }

    @Override
    public void attachView(IView iView) {
        mIntroduceView = (IIntroduceView) iView;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mInitBinder = (InitService.InitBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mInitBinder = null;
            }
        };
        mIntroduceView.bindServiceConnection(mServiceConnection);
        mHandler = new Handler(this);
    }

    @Override
    public void detachView() {

    }

    @Override
    public void wannaFinish() {
        if (mInitBinder.isFinished()) {
            mIntroduceView.unbindServiceConnection(mServiceConnection);
            mIntroduceView.jump2Album();
        } else {
            mIntroduceView.showProgressBar();
            checkService();
        }
    }

    private void checkService() {
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mInitBinder.isFinished()) {
            mIntroduceView.unbindServiceConnection(mServiceConnection);
            mIntroduceView.jump2Album();
        } else {
            mIntroduceView.showProgressBar();
            checkService();
        }
        return false;
    }
}
