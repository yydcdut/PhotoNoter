package com.yydcdut.note.mvp.p.login;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IUserDetailFragPresenter extends IPresenter {
    void bindData(int type);

    boolean checkInternet();

    void loginOrOutQQ();

    void loginOrOutEvernote();
}
