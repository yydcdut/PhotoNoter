package com.yydcdut.note.views;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IView {

    /**
     * 权限用
     *
     * @return
     */
    IPresenter getPresenter();
}
