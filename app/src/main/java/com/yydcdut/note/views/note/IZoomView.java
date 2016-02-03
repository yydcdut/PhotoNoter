package com.yydcdut.note.views.note;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IZoomView extends IView {

    void showProgressBar();

    void hideProgressBar();

    void showImage(String path);

    void jump2PGEditActivity(String path);

    void finishActivity(boolean hasResult);

    void showSnackBar(String massage);
}
