package com.yydcdut.note.mvp.v.setting;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IAboutAppView extends IView {
    /**
     * 显示版本号
     *
     * @param version 版本号
     */
    void showVersion(String version);

    /**
     * 更新版本
     */
    void updateApk();

    /**
     * 反馈
     */
    void jump2FeedbackActivity();

    /**
     * 分享
     */
    void share();

    /**
     * viewGitHub
     */
    void viewGitHub();
}
