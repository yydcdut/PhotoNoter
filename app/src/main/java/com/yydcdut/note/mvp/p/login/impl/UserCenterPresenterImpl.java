package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.evernote.edam.type.User;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.IUserCenterPresenter;
import com.yydcdut.note.mvp.v.login.IUserCenterView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/16.
 */
public class UserCenterPresenterImpl implements IUserCenterPresenter, Handler.Callback {
    private IUserCenterView mUserCenterView;
    private Context mContext;
    private UserCenter mUserCenter;
    private ThreadExecutorPool mThreadExecutorPool;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;
    private Activity mActivity;

    private boolean[] mInitState;

    @Inject
    public UserCenterPresenterImpl(Activity activity, @ContextLife("Activity") Context context,
                                   UserCenter userCenter, ThreadExecutorPool threadExecutorPool) {
        mActivity = activity;
        mHandler = new Handler(this);
        mContext = context;
        mInitState = new boolean[2];
        mUserCenter = userCenter;
        mThreadExecutorPool = threadExecutorPool;
        mInitState[0] = mUserCenter.isLoginQQ();
        mInitState[1] = mUserCenter.isLoginEvernote();

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
        if (mUserCenter.isLoginQQ()) {
            return;
        } else {
            mUserCenter.doLoginQQ(mActivity, mLoginQQListener);
        }
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
        if (mUserCenter.isLoginQQ() && mUserCenter.getQQ() != null) {
            IUser qqUser = mUserCenter.getQQ();
            mUserCenterView.showQQInfo(qqUser.getName(), qqUser.getImagePath());
        } else {
            mUserCenterView.showQQInfo(null, null);
        }
    }

    @Override
    public void initEvernote() {
        mUserCenterView.showEvernote(mUserCenter.isLoginEvernote());
    }

    @Override
    public void finish() {
        if (mInitState[0] != mUserCenter.isLoginQQ() || mInitState[1] != mUserCenter.isLoginEvernote()) {
            mUserCenterView.finishActivityWithResult(RESULT_DATA_USER);
        } else {
            mUserCenterView.finishActivityWithResult(-1);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                initQQ();
                mUserCenterView.showQQInfoInFrag(mUserCenter.getQQ().getName());
                mUserCenterView.hideProgressBar();
                mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                break;
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
            case MESSAGE_LOGIN_QQ_FAILED:
                mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
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
            mUserCenterView.showProgressBar();
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
