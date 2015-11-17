package com.yydcdut.note.mvp.v.login;

import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface ILoginView extends IView {
    void showSnackBar(String message);

    void showProgressBar();

    void hideProgressBar();

    void finishActivityWithResult(int result);

    void showSnackBarWithAction(String message, String action, OnSnackBarActionListener listener);

}
