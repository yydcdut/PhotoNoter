package com.yydcdut.note.views.login;

import com.yydcdut.note.views.IView;
import com.yydcdut.note.widget.fab2.snack.OnSnackBarActionListener;

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
