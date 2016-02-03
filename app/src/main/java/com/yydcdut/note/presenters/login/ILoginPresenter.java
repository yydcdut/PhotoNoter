package com.yydcdut.note.presenters.login;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface ILoginPresenter extends IPresenter {
    boolean checkInternet();

    void loginQQ();

    void loginEvernote();

    void onEvernoteLoginFinished(boolean successful);
}
