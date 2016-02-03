package com.yydcdut.note.presenters.note;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailFragPresenter extends IPresenter {
    void bindData(int categoryId, int position, int comparator);

    void showImage();

    void jump2ZoomActivity();
}
