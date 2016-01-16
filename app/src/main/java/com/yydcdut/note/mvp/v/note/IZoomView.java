package com.yydcdut.note.mvp.v.note;

import com.yydcdut.note.mvp.IView;

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
