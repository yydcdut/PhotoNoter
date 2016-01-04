package com.yydcdut.note.mvp.v.home;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/18.
 */
public interface IIntroduceView extends IView {

    void showProgressBar();

    void hideProgressBar();

    void jump2Album();

}
