package com.yydcdut.note.mvp;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IPresenter {
    /**
     * 注入View，使之能够与View相互响应
     *
     * @param iView
     */
    void attachView(IView iView);

    void detachView();
}
