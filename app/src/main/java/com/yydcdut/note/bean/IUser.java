package com.yydcdut.note.bean;

/**
 * Created by yuyidong on 15/8/11.
 */
public interface IUser extends IObject {

    String getName();

    String getNetImagePath();

    String getLocalImagePath();

    String getOpenId();

    String getAccessToken();

    String getType();
}
