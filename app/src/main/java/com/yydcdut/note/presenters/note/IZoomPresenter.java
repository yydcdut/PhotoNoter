package com.yydcdut.note.presenters.note;

import android.graphics.Bitmap;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IZoomPresenter extends IPresenter {

    void bindData(int categoryId, int position, int comparator);

    void jump2PGEditActivity();

    void refreshImage();

    void saveSmallImage(Bitmap thumbNail);

    void finishActivity();

}
