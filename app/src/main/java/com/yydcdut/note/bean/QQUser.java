package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/8/11.
 */
public class QQUser implements IUser {
    private String openId;
    private String accessToken;

    private String name;
    private String netImagePath;
    private String localImagePath;

    public QQUser(String openId, String accessToken, String nameQQ, String netImagePath) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.name = nameQQ;
        this.netImagePath = netImagePath;
    }

    public String getOpenId() {
        return openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getNetImagePath() {
        return netImagePath;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }
}
