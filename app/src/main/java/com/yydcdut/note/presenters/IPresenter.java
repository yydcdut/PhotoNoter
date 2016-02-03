package com.yydcdut.note.presenters;

import android.support.annotation.NonNull;

import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IPresenter {
    int RESULT_NOTHING = 1;
    int RESULT_DATA = 2;
    int RESULT_PICTURE = 3;
    int RESULT_DATA_QQ = 4;
    int RESULT_DATA_EVERNOTE = 5;
    int RESULT_DATA_USER = 6;

    int REQUEST_NOTHING = 1;

    /**
     * 注入View，使之能够与View相互响应
     *
     * @param iView
     */
    void attachView(@NonNull IView iView);

    /**
     * 释放资源
     */
    void detachView();
}
