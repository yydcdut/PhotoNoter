package com.yydcdut.note.mvp.v.service;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/22.
 */
public interface ISandBoxServiceView extends IView {

    void notification();

    void cancelNotification();

    void sendBroadCast();

    void stopService();

    void killProgress();
}
