package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.ILoginPresenter;
import com.yydcdut.note.mvp.v.login.ILoginView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/16.
 */
public class LoginPresenterImpl implements ILoginPresenter, Handler.Callback {
    private ILoginView mLoginView;

    private Context mContext;
    private Activity mActivity;
    private ThreadExecutorPool mThreadExecutorPool;
    private UserCenter mUserCenter;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;

    @Inject
    public LoginPresenterImpl(Activity activity, @ContextLife("Activity") Context context,
                              ThreadExecutorPool threadExecutorPool, UserCenter userCenter) {
        mActivity = activity;
        mContext = context;
        mThreadExecutorPool = threadExecutorPool;
        mUserCenter = userCenter;
    }

    @Override
    public void attachView(IView iView) {
        mLoginView = (ILoginView) iView;
        mHandler = new Handler(this);
    }

    @Override
    public void detachView() {

    }

    @Override
    public boolean checkInternet() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            //没有网络
            mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void loginQQ() {
        if (mUserCenter.isLoginQQ()) {
            mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
        } else {
            mUserCenter.doLoginQQ(mActivity, mLoginQQListener);
        }
    }

    @Override
    public void loginEvernote() {
        if (mUserCenter.isLoginEvernote()) {
            mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
        } else {
            mUserCenter.doLoginEvernote(mActivity);
        }
    }

    @Override
    public void onEvernoteLoginFinished(boolean successful) {
        if (successful) {
            mUserCenter.LoginEvernote();
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_OK);
        } else {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_FAILED);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                mLoginView.hideProgressBar();
                mLoginView.finishActivityWithResult(RESULT_DATA_QQ);
                break;
            case MESSAGE_LOGIN_EVERNOTE_OK:
                mLoginView.finishActivityWithResult(RESULT_DATA_EVERNOTE);
                break;
            case MESSAGE_LOGIN_EVERNOTE_FAILED:
                mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                        mContext.getResources().getString(R.string.toast_retry),
                        new OnSnackBarActionListener() {
                            @Override
                            public void onClick() {
                                mUserCenter.doLoginEvernote(mActivity);
                            }
                        });
                break;
            case MESSAGE_LOGIN_QQ_FAILED:
                mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                        mContext.getResources().getString(R.string.toast_retry),
                        new OnSnackBarActionListener() {
                            @Override
                            public void onClick() {
                                mUserCenter.doLoginQQ(mActivity, mLoginQQListener);
                            }
                        });
                break;
        }
        return false;
    }

    private UserCenter.OnLoginQQListener mLoginQQListener = new UserCenter.OnLoginQQListener() {
        @Override
        public void onComplete(final String openid, final String accessToken, final String name, final String image) {
            mLoginView.showProgressBar();
            mThreadExecutorPool.getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (mUserCenter.LoginQQ(openid, accessToken, name, image)) {
                        Bitmap bitmap = ImageLoaderManager.loadImageSync(image);
                        FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), bitmap);
                        mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_OK);
                    }
                }
            });
        }

        @Override
        public void onError() {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_FAILED);
        }

        @Override
        public void onCancel() {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_FAILED);
        }
    };

}
