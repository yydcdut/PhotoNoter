package com.yydcdut.note.presenters.setting;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IAboutAppPresenter extends IPresenter {
    /**
     * 升级版本逻辑
     */
    void doUpdateVersion();

    /**
     * 反馈的逻辑
     */
    void doFeedback();

    /**
     * 分享的逻辑
     */
    void doShare();

    /**
     * github的逻辑
     */
    void gotoGithub();
}
