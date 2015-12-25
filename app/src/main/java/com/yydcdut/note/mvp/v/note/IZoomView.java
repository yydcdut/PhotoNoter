package com.yydcdut.note.mvp.v.note;

import android.graphics.Bitmap;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IZoomView extends IView {

    void showProgressBar();

    void hideProgressBar();

    void showImage(Bitmap bitmap);

    void jump2PGEditActivity(String path);

    void finishActivity(boolean hasResult);

    void showSnackBar(String massage);

}
