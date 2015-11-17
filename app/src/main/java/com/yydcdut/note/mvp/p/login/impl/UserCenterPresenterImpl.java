package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.evernote.edam.type.User;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.IUserCenterPresenter;
import com.yydcdut.note.mvp.v.login.IUserCenterView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.NetworkUtils;

/**
 * Created by yuyidong on 15/11/16.
 */
public class UserCenterPresenterImpl implements IUserCenterPresenter, Handler.Callback {
    private IUserCenterView mUserCenterView;
    private Context mContext;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;
    private Activity mActivity;

    private boolean[] mInitState;

    public UserCenterPresenterImpl(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(this);
        mContext = NoteApplication.getContext();
        mInitState = new boolean[2];
        mInitState[0] = UserCenter.getInstance().isLoginQQ();
        mInitState[1] = UserCenter.getInstance().isLoginEvernote();

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
        if (UserCenter.getInstance().isLoginQQ()) {
            return;
        } else {
            UserCenter.getInstance().doLoginQQ(mActivity, mLoginQQListener);
        }
    }

    @Override
    public void loginEvernote() {
        if (UserCenter.getInstance().isLoginEvernote()) {
            return;
        } else {
            UserCenter.getInstance().doLoginEvernote(mActivity);
        }
    }

    @Override
    public void onEvernoteLoginFinished(boolean successful) {
        if (successful) {
            UserCenter.getInstance().LoginEvernote();
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_OK);
        } else {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_FAILED);
        }
    }

    @Override
    public void initQQ() {
        if (UserCenter.getInstance().isLoginQQ() && UserCenter.getInstance().getQQ() != null) {
            IUser qqUser = UserCenter.getInstance().getQQ();
            mUserCenterView.showQQInfo(qqUser.getName(), qqUser.getImagePath());
        } else {
            mUserCenterView.showQQInfo(null, null);
        }
    }

    @Override
    public void initEvernote() {
        mUserCenterView.showEvernote(UserCenter.getInstance().isLoginEvernote());
    }

    @Override
    public void finish() {
        if (mInitState[0] != UserCenter.getInstance().isLoginQQ() || mInitState[1] != UserCenter.getInstance().isLoginEvernote()) {
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
                mUserCenterView.showQQInfoInFrag(UserCenter.getInstance().getQQ().getName());
                mUserCenterView.hideProgressBar();
                mUserCenterView.showSnackBar(mContext.getResources().getString(R.string.toast_success));
                break;
            case MESSAGE_LOGIN_EVERNOTE_OK:
                initEvernote();
                User evernoteUser = UserCenter.getInstance().getEvernote();
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
                                UserCenter.getInstance().doLoginEvernote(mActivity);
                            }
                        });
                break;
            case MESSAGE_LOGIN_QQ_FAILED:
                mUserCenterView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                        mContext.getResources().getString(R.string.toast_retry),
                        new OnSnackBarActionListener() {
                            @Override
                            public void onClick() {
                                UserCenter.getInstance().doLoginQQ(mActivity, mLoginQQListener);
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
            NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (UserCenter.getInstance().LoginQQ(openid, accessToken, name, image)) {
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
