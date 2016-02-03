package com.yydcdut.note.presenters.home;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/18.
 */
public interface ISplashPresenter extends IPresenter {
    void onActivityStart();

    void onActivityPause();

    void isWannaCloseSplash();

    void doingSplash();
}
