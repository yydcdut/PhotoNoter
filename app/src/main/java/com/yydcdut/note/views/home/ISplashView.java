package com.yydcdut.note.views.home;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 15/11/18.
 */
public interface ISplashView extends IView {
    void jump2Introduce();

    void jump2Album();

    boolean isAnimationRunning();
}
