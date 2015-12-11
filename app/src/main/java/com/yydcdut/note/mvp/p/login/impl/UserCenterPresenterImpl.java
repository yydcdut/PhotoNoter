package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.evernote.edam.type.User;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.IUserCenterPresenter;
import com.yydcdut.note.mvp.v.login.IUserCenterView;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class UserCenterPresenterImpl implements IUserCenterPresenter, Handler.Callback {
    private IUserCenterView mUserCenterView;
    private Context mContext;
    private UserCenter mUserCenter;
    private RxUser mRxUser;
    private ThreadExecutorPool mThreadExecutorPool;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;
    private Activity mActivity;

    private final boolean[] mInitState;

    @Inject
    public UserCenterPresenterImpl(Activity activity, @ContextLife("Activity") Context context,
                                   UserCenter userCenter, ThreadExecutorPool threadExecutorPool, RxUser rxUser) {
        mActivity = activity;
        mHandler = new Handler(this);
        mContext = context;
        mInitState = new boolean[2];
        mUserCenter = userCenter;
        mThreadExecutorPool = threadExecutorPool;
//        mInitState[0] = mUserCenter.isLoginQQ();
        mInitState[1] = mUserCenter.isLoginEvernote();
        mRxUser = rxUser;
        mRxUser.isLoginQQ()
                .subscribe(aBoolean -> mInitState[0] = aBoolean);

    }

    @Override
    public void attachView(IView iView) {
        mUserCenterView = (IUserCenterView) iView;
        initQQ();
        initEvernote();
    }

    @Override
    public void detachView() {

    }

    @Override
    public boolean checkInternet() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            //没有网络
            mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void loginQQ() {
        mRxUser.isLoginQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        mRxUser.loginQQ(mActivity)
                                .doOnSubscribe(() -> mUserCenterView.showProgressBar())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<IUser>() {
                                    @Override
                                    public void onCompleted() {
                                        mUserCenterView.hideProgressBar();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
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
                                        initQQ();
                                        mUserCenterView.showQQInfoInFrag(iUser.getName());
                                        mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                                    }
                                });
                    }
                });
    }

    @Override
    public void loginEvernote() {
        if (mUserCenter.isLoginEvernote()) {
            return;
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
    public void initQQ() {
        mRxUser.getQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserCenterView.showQQInfo(null, null);
                    }

                    @Override
                    public void onNext(IUser iUser) {
                        mUserCenterView.showQQInfo(iUser.getName(), iUser.getImagePath());
                    }
                });
    }

    @Override
    public void initEvernote() {
        mUserCenterView.showEvernote(mUserCenter.isLoginEvernote());
    }

    @Override
    public void finish() {
        mRxUser.isLoginQQ()
                .subscribe(aBoolean -> {
                    if (mInitState[0] == aBoolean) {
                        mUserCenterView.finishActivityWithResult(RESULT_DATA_USER);
                    } else {
                        //todo Evernote
                        mUserCenterView.finishActivityWithResult(-1);
                    }
                });


//        if (mInitState[1] != mUserCenter.isLoginEvernote()) {
//            mUserCenterView.finishActivityWithResult(RESULT_DATA_USER);
//        } else {
//            mUserCenterView.finishActivityWithResult(-1);
//        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_EVERNOTE_OK:
                initEvernote();
                User evernoteUser = mUserCenter.getEvernote();
                if (evernoteUser == null) {
                    mUserCenterView.showEvernoteInFrag(true, mContext.getResources().getString(R.string.user_failed));
                } else {
                    mUserCenterView.showEvernoteInFrag(true, evernoteUser.getName());
                }
                mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                break;
            case MESSAGE_LOGIN_EVERNOTE_FAILED:
                mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
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
