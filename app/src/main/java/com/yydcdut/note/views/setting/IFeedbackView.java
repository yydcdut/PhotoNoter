package com.yydcdut.note.views.setting;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IFeedbackView extends IView {
    String getEmail();

    String getContent();

    /**
     * 在toolbar上显示feedback的title
     */
    void showFeedbackTitle();

    /**
     * 在toolbar上显示contanct的title
     */
    void showContactTitle();

    /**
     * 显示progressbar
     */
    void showLoading();

    /**
     * 隐藏progressbar并且finish掉activity
     */
    void hideLoadingAndFinish();

    /**
     * 显示SnackBar显示信息
     *
     * @param message
     */
    void showSnackBar(String message);
}
