package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.evernote.client.android.EvernoteSession;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.ILoginPresenter;
import com.yydcdut.note.mvp.v.login.ILoginView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuyidong on 15/11/16.
 */
public class LoginPresenterImpl implements ILoginPresenter, Handler.Callback {
    private ILoginView mLoginView;

    private Context mContext;

    private Activity mActivity;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;
    private static final int MESSAGE_LOGIN_EVERNOTE_OK = 2;
    private static final int MESSAGE_LOGIN_EVERNOTE_FAILED = 4;
    private Handler mHandler;

    private Tencent mTencent;

    public LoginPresenterImpl(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void attachView(IView iView) {
        mContext = NoteApplication.getContext();
        mLoginView = (ILoginView) iView;
        mHandler = new Handler(this);
        initTencent();
    }

    /**
     * 初始化腾讯的接口
     */
    private void initTencent() {
        mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, mContext);
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
        if (UserCenter.getInstance().isLoginQQ()) {
            mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
        } else {
            mTencent.login(mActivity, "all", new BaseUiListener());
        }
    }

    @Override
    public void loginEvernote() {
        if (UserCenter.getInstance().isLoginEvernote()) {
            mLoginView.showSnackBar(mContext.getResources().getString(R.string.toast_already_login));
        } else {
            EvernoteSession.getInstance().authenticate(mActivity);
        }
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            UserCenter.getInstance().LoginEvernote();
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_OK);
        } else {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_EVERNOTE_FAILED);
        }
    }

    /**
     * 当自定义的监听器实现IUiListener接口后，必须要实现接口的三个方法，
     * onComplete  onCancel onError
     * 分别表示第三方登录成功，取消 ，错误。
     */
    private class BaseUiListener implements IUiListener {

        public void onCancel() {
            mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_FAILED);
        }

        /*
            {
                "access_token": "15D69FFB81BC403D9DB3DFACCF2FDDFF",
	            "authority_cost": 2490,
	            "expires_in": 7776000,
	            "login_cost": 775,
	            "msg": "",
	            "openid": "563559BEF3E2F97B693A6F88308F8D21",
	            "pay_token": "0E13A21128EAFB5E39048E5DE9478AD4",
	            "pf": "desktop_m_qq-10000144-android-2002-",
	            "pfkey": "11157020df5d6a8ebeaa150e2a7c68ce",
	            "query_authority_cost": 788,
	            "ret": 0
            }
        */
        public void onComplete(Object response) {
            String openid = null;
            String accessToken = null;
            try {
                openid = ((JSONObject) response).getString("openid");
                accessToken = ((JSONObject) response).getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
              到此已经获得OpenID以及其他你想获得的内容了
              QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像
              sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(mContext, qqToken);
            //这样我们就拿到这个类了，之后的操作就跟上面的一样了，同样是解析JSON
            final String finalOpenid = openid;
            final String finalAccessToken = accessToken;
            info.getUserInfo(new IUiListener() {
                /*
                  {
	                 "city": "成都",
	                 "figureurl": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/30",
	                 "figureurl_1": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/50",
	                 "figureurl_2": "http://qzapp.qlogo.cn/qzapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
	                 "figureurl_qq_1": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/40",
	                 "figureurl_qq_2": "http://q.qlogo.cn/qqapp/1104732115/563559BEF3E2F97B693A6F88308F8D21/100",
	                 "gender": "男",
	                 "is_lost": 0,
	                 "is_yellow_vip": "0",
	                 "is_yellow_year_vip": "0",
	                 "level": "0",
	                 "msg": "",
	                 "nickname": "生命短暂，快乐至上。",
	                 "province": "四川",
	                 "ret": 0,
	                 "vip": "0",
	                 "yellow_vip_level": "0"
                    }
                 */
                public void onComplete(final Object response) {

                    JSONObject json = (JSONObject) response;
                    String name = null;
                    String image = null;
                    try {
                        name = json.getString("nickname");
                        image = json.getString("figureurl_qq_2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mLoginView.showProgressBar();
                    final String finalImage = image;
                    final String finalName = name;
                    NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (UserCenter.getInstance().LoginQQ(finalOpenid,
                                    finalAccessToken, finalName, finalImage)) {
                                Bitmap bitmap = ImageLoaderManager.loadImageSync(finalImage);
                                FilePathUtils.saveImage(FilePathUtils.getQQImagePath(), bitmap);
                                //登录成功
                                mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_OK);
                            }
                        }
                    });
                }

                public void onCancel() {
                    mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_FAILED);
                }

                public void onError(UiError arg0) {
                    mHandler.sendEmptyMessage(MESSAGE_LOGIN_QQ_FAILED);
                }

            });
        }

        @Override
        public void onError(UiError uiError) {
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_LOGIN_QQ_OK:
                mLoginView.hidePregressBar();
                mLoginView.finishActivityWithResult(RESULT_DATA_QQ);
                break;
            case MESSAGE_LOGIN_EVERNOTE_OK:
                mLoginView.hidePregressBar();
                mLoginView.finishActivityWithResult(RESULT_DATA_EVERNOTE);
                break;
            case MESSAGE_LOGIN_EVERNOTE_FAILED:
                mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                        mContext.getResources().getString(R.string.toast_retry),
                        new OnSnackBarActionListener() {
                            @Override
                            public void onClick() {
                                EvernoteSession.getInstance().authenticate(mActivity);
                            }
                        });
                break;
            case MESSAGE_LOGIN_QQ_FAILED:
                mLoginView.showSnackBarWithAction(mContext.getResources().getString(R.string.toast_fail),
                        mContext.getResources().getString(R.string.toast_retry),
                        new OnSnackBarActionListener() {
                            @Override
                            public void onClick() {
                                mTencent.login(mActivity, "all", new BaseUiListener());
                            }
                        });
                break;
        }
        return false;
    }
}
