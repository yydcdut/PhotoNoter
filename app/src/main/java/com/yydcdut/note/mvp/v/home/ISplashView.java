package com.yydcdut.note.mvp.v.home;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/18.
 */
public interface ISplashView extends IView {
    void startCheckService();

    void jump2Introduce();

    void jump2Album();
}
