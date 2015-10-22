package com.yydcdut.note.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.bean.QQUser;

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


}
