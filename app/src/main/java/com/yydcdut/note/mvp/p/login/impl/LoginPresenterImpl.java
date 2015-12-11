package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.ILoginPresenter;
import com.yydcdut.note.mvp.v.login.ILoginView;
import com.yydcdut.note.utils.NetworkUtils;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class LoginPresenterImpl implements ILoginPresenter, Handler.Callback {
    private ILoginView mLoginView;

    private Context mContext;
    private Activity mActivity;
    private UserCenter mUserCenter;

    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;
    private RxUser mRxUser;

    @Inject
    public LoginPresenterImpl(Activity activity, @ContextLife("Activity") Context context,
                              UserCenter userCenter, RxUser rxUser) {
        mActivity = activity;
        mContext = context;
        mUserCenter = userCenter;
        mRxUser = rxUser;
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
        mRxUser.isLoginQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
                    } else {
                        mRxUser.loginQQ(mActivity)
                                .doOnSubscribe(() -> mLoginView.showProgressBar())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<IUser>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mLoginView.hideProgressBar();
                                        mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                                                mContext.getResources().getString(R.string.toast_retry),
                                                new OnSnackBarActionListener() {
                                                    @Override
                                                    public void onClick() {
                                                        loginQQ();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onNext(IUser iUser) {
                                        mLoginView.hideProgressBar();
                                        mLoginView.finishActivityWithResult(RESULT_DATA_QQ);
                                    }
                                });
                    }
                });
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
        }
        return false;
    }

}
