package com.yydcdut.note.mvp.p.home;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/18.
 */
public interface ISplashPresenter extends IPresenter {
    void onActivityStart();

    void onActivityPause();

    void isWannaCloseSplash();

    void doingSplash();
}
