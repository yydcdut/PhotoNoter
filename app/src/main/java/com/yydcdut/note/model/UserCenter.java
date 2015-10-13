package com.yydcdut.note.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.bean.QQUser;

/**
 * Created by yuyidong on 15/8/11.
 */
public class UserCenter {
    private static final String NULL = "";

    private static final String NAME = "User";

    private static final String SORT = "sort";
    private static final String SORT_DEFAULT = "";

    private static final String Q_OPEN_ID = "q_open_id";
    private static final String Q_OPEN_ID_DEFAULT = "";

    private static final String Q_ACCESS_TOKEN = "q_access_token";
    private static final String Q_ACCESS_TOKEN_DEFAULT = "";

    private static final String Q_NAME = "q_name";
    private static final String Q_NAME_DEFAULT = "";

    private static final String Q_NET_IMAGE_PATH = "q_net_image_id";
    private static final String Q_NET_IMAGE_PATH_DEFAULT = "";

    private static final String S_OPEN_ID = "s_open_id";
    private static final String S_OPEN_ID_DEFAULT = "";

    private static final String S_ACCESS_TOKEN = "s_access_token";
    private static final String S_ACCESS_TOKEN_DEFAULT = "";

    private static final String S_NAME = "s_name";
    private static final String S_NAME_DEFAULT = "";

    private static final String S_NET_IMAGE_PATH = "s_net_image_id";
    private static final String S_NET_IMAGE_PATH_DEFAULT = "";

    public static final String USER_TYPE_QQ = "qq";
    public static final String USER_TYPE_SINA = "sina";

    private SharedPreferences mSharedPreferences;

    private UserCenter() {
        mSharedPreferences = NoteApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static class UserCenterHolder {
        private static final UserCenter INSTANCE = new UserCenter();
    }

    public static final UserCenter getInstance() {
        return UserCenterHolder.INSTANCE;
    }

    public boolean set(String user, String openId, String accessToken, String name, final String netImagePath, boolean cover) {
        if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
            return false;
        }
        //todo 把照片存到本地
        switch (user) {
            case USER_TYPE_QQ:
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Q_OPEN_ID, openId);
                editor.putString(Q_ACCESS_TOKEN, accessToken);
                editor.putString(Q_NAME, name);
                editor.putString(Q_NET_IMAGE_PATH, netImagePath);
                if (TextUtils.isEmpty(mSharedPreferences.getString(SORT, SORT_DEFAULT))) {
                    editor.putString(SORT, USER_TYPE_QQ);
                }
                editor.commit();
                break;
            case USER_TYPE_SINA:
                break;
            default:
                return false;
        }
        return true;
    }

    public IUser userFactory(String userType) {
        IUser iUser;
        switch (userType) {
            case USER_TYPE_QQ:
                String openId = mSharedPreferences.getString(Q_OPEN_ID, Q_OPEN_ID_DEFAULT);
                String accessToken = mSharedPreferences.getString(Q_ACCESS_TOKEN, Q_ACCESS_TOKEN_DEFAULT);
                String name = mSharedPreferences.getString(Q_NAME, Q_NAME_DEFAULT);
                String netImagePath = mSharedPreferences.getString(Q_NET_IMAGE_PATH, Q_NET_IMAGE_PATH_DEFAULT);
                if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(name) || TextUtils.isEmpty(netImagePath)) {
                    return null;
                }
                iUser = new QQUser(openId, accessToken, name, netImagePath);
                return iUser;
            case USER_TYPE_SINA:
                break;
            default:
                break;
        }
        return null;
    }

    public int existUserNumber() {
        int number = 0;
        if (!TextUtils.isEmpty(mSharedPreferences.getString(Q_OPEN_ID, Q_OPEN_ID_DEFAULT))) {
            number++;
        }
        if (!TextUtils.isEmpty(mSharedPreferences.getString(S_OPEN_ID, S_OPEN_ID_DEFAULT))) {
            number++;
        }
        return number;
    }

    public String getFirstUserType() {
        return mSharedPreferences.getString(SORT, SORT_DEFAULT);
    }

    public String getAnotherUser() {
        String userType = mSharedPreferences.getString(SORT, SORT_DEFAULT);
        String another = null;
        switch (userType) {
            case USER_TYPE_QQ:
                another = USER_TYPE_SINA;
                break;
            case USER_TYPE_SINA:
                another = USER_TYPE_QQ;
                break;
        }
        return another;
    }

    public void cleanAll() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Q_OPEN_ID, NULL);
        editor.putString(Q_ACCESS_TOKEN, NULL);
        editor.putString(Q_NAME, NULL);
        editor.putString(Q_NET_IMAGE_PATH, NULL);
        editor.putString(S_OPEN_ID, NULL);
        editor.putString(S_ACCESS_TOKEN, NULL);
        editor.putString(S_NAME, NULL);
        editor.putString(S_NET_IMAGE_PATH, NULL);
        if (TextUtils.isEmpty(mSharedPreferences.getString(SORT, SORT_DEFAULT))) {
            editor.putString(SORT, NULL);
        }
        editor.commit();
    }

}
