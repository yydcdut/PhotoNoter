package com.yydcdut.note.presenters.login.impl;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.presenters.login.ILoginPresenter;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.login.ILoginView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class LoginPresenterImpl implements ILoginPresenter {
    private ILoginView mLoginView;

    private Context mContext;
    private Activity mActivity;

    private RxUser mRxUser;

    @Inject
    public LoginPresenterImpl(Activity activity, @ContextLife("Activity") Context context, RxUser rxUser) {
        mActivity = activity;
        mContext = context;
        mRxUser = rxUser;
    }

    @Override
    public void attachView(IView iView) {
        mLoginView = (ILoginView) iView;
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
                                .subscribe((iUser -> {
                                    mLoginView.hideProgressBar();
                                    mLoginView.finishActivityWithResult(RESULT_DATA_QQ);
                                }), ((throwable -> {
                                    YLog.e(throwable);
                                    mLoginView.hideProgressBar();
                                    mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail), mContext.getResources().getString(R.string.toast_retry), (() -> loginQQ()));
                                })));
                    }
                }, (throwable -> YLog.e(throwable)));
    }

    @Override
    public void loginEvernote() {
        mRxUser.isLoginEvernote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
                    } else {
                        mRxUser.loginEvernote(mActivity).subscribe();
                    }
                }, (throwable -> YLog.e(throwable)));
    }

    @Override
    public void onEvernoteLoginFinished(boolean successful) {
        if (successful) {
            mRxUser.saveEvernote()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((iUser -> mLoginView.finishActivityWithResult(RESULT_DATA_EVERNOTE)),
                            (throwable -> {
                                YLog.e(throwable);
                                mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                                        mContext.getResources().getString(R.string.toast_retry),
                                        (() -> mRxUser.loginEvernote(mActivity).subscribe((bool) -> {
                                                }, (throwable1 -> YLog.e(throwable1))
                                        )));
                            }));
        } else {
            mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                    mContext.getResources().getString(R.string.toast_retry), (() -> mRxUser.loginEvernote(mActivity).subscribe((bool) -> {
                    }, (throwable -> YLog.e(throwable)))));
        }
    }
}
