package com.yydcdut.note.mvp.p.note;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailFragPresenter extends IPresenter {
    void bindData(int categoryId, int position, int comparator);

    void showImage();

    void jump2ZoomActivity();
}
