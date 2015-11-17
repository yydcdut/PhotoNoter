package com.yydcdut.note.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.bean.QQUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by yuyidong on 15/8/11.
 * todo 需要重构
 */
public class UserCenter {
    private static final String NULL = "";

    private static final String NAME = "User";

    private static final String Q_OPEN_ID = "q_open_id";
    private static final String Q_OPEN_ID_DEFAULT = "";

    private static final String Q_ACCESS_TOKEN = "q_access_token";
    private static final String Q_ACCESS_TOKEN_DEFAULT = "";

    private static final String Q_NAME = "q_name";
    private static final String Q_NAME_DEFAULT = "";

    private static final String Q_NET_IMAGE_PATH = "q_net_image_id";
    private static final String Q_NET_IMAGE_PATH_DEFAULT = "";

    private SharedPreferences mSharedPreferences;

    private IUser mQQUser = null;
    private User mEvernoteUser = null;
    private Future<User> mEvernoteFuture = null;


    private WeakReference<Activity> mQQActivity;
    private WeakReference<Activity> mEvernoteActivity;

    private Tencent mTencent;

    private UserCenter() {
        mSharedPreferences = NoteApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static class UserCenterHolder {
        private static final UserCenter INSTANCE = new UserCenter();
    }

    public static final UserCenter getInstance() {
        return UserCenterHolder.INSTANCE;
    }

    public boolean isLoginQQ() {
        String openId = mSharedPreferences.getString(Q_OPEN_ID, Q_OPEN_ID_DEFAULT);
        String accessToken = mSharedPreferences.getString(Q_ACCESS_TOKEN, Q_ACCESS_TOKEN_DEFAULT);
        String name = mSharedPreferences.getString(Q_NAME, Q_NAME_DEFAULT);
        String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
        if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean LoginQQ(String openId, String accessToken, String name, final String netImagePath) {
        if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
            return false;
        }
        mQQUser = null;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Q_OPEN_ID, openId);
        editor.putString(Q_ACCESS_TOKEN, accessToken);
        editor.putString(Q_NAME, name);
        editor.putString(Q_NET_IMAGE_PATH, netImagePath);
        editor.commit();
        return true;
    }

    public void doLoginQQ(Activity activity, OnLoginQQListener listener) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(BuildConfig.TENCENT_KEY, NoteApplication.getContext());
        }
        if (mQQActivity != null) {
            mQQActivity.clear();
            mQQActivity = null;
        }
        mQQActivity = new WeakReference<>(activity);

        mTencent.login(mQQActivity.get(), "all", new BaseUiListener(listener));
    }

    /**
     * 当自定义的监听器实现IUiListener接口后，必须要实现接口的三个方法，
     * onComplete  onCancel onError
     * 分别表示第三方登录成功，取消 ，错误。
     */
    private class BaseUiListener implements IUiListener {
        private OnLoginQQListener mListener;

        public BaseUiListener(OnLoginQQListener listener) {
            mListener = listener;
        }

        public void onCancel() {
            if (mListener != null) {
                mListener.onCancel();
            }
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
            UserInfo info = new UserInfo(NoteApplication.getContext(), qqToken);
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
                    if (mListener != null) {
                        mListener.onComplete(finalOpenid, finalAccessToken, name, image);
                    }
                }

                public void onCancel() {
                    if (mListener != null) {
                        mListener.onCancel();
                    }
                }

                public void onError(UiError arg0) {
                    if (mListener != null) {
                        mListener.onError();
                    }
                }

            });
        }

        @Override
        public void onError(UiError uiError) {
            if (mListener != null) {
                mListener.onError();
            }
        }
    }

    public IUser getQQ() {
        if (mQQUser == null) {
            String openId = mSharedPreferences.getString(Q_OPEN_ID, Q_OPEN_ID_DEFAULT);
            String accessToken = mSharedPreferences.getString(Q_ACCESS_TOKEN, Q_ACCESS_TOKEN_DEFAULT);
            String name = mSharedPreferences.getString(Q_NAME, Q_NAME_DEFAULT);
            String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
            if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
                return null;
            } else {
                mQQUser = new QQUser(openId, accessToken, name, netImagePath);
            }
        }
        return mQQUser;
    }

    public void logoutQQ() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Q_OPEN_ID, NULL);
        editor.putString(Q_ACCESS_TOKEN, NULL);
        editor.putString(Q_NAME, NULL);
        editor.putString(Q_NET_IMAGE_PATH, NULL);
        editor.commit();
    }

    public boolean isLoginEvernote() {
        return EvernoteSession.getInstance().isLoggedIn();
    }

    public void LoginEvernote() {
        if (!isLoginEvernote()) {
            mEvernoteFuture = null;
            return;
        }
        mEvernoteFuture = NoteApplication.getInstance().getExecutorPool().submit(new Callable<User>() {
            @Override
            public User call() throws Exception {
                User user = null;
                try {
                    user = EvernoteSession.getInstance().getEvernoteClientFactory().getUserStoreClient().getUser();
                } catch (EDAMUserException e) {
                    e.printStackTrace();
                } catch (EDAMSystemException e) {
                    e.printStackTrace();
                } catch (TException e) {
                    e.printStackTrace();
                }
                return user;
            }
        });
    }

    public void doLoginEvernote(Activity activity) {
        if (mEvernoteActivity != null) {
            mEvernoteActivity.clear();
            mEvernoteActivity = null;
        }
        mEvernoteActivity = new WeakReference<>(activity);
        EvernoteSession.getInstance().authenticate(mEvernoteActivity.get());
    }

    public User getEvernote() {
        if (!isLoginEvernote()) {
            mEvernoteFuture = null;
            mEvernoteUser = null;
            return null;
        }
        if (mEvernoteUser == null && mEvernoteFuture == null) {
            LoginEvernote();
            try {
                mEvernoteUser = mEvernoteFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (mEvernoteUser == null && mEvernoteFuture != null) {
            try {
                mEvernoteUser = mEvernoteFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return mEvernoteUser;
    }

    public void logoutEvernote() {
        EvernoteSession.getInstance().logOut();
    }

    public interface OnLoginQQListener {
        void onComplete(String openid, String accessToken, String name, String image);

        void onError();

        void onCancel();
    }

}
