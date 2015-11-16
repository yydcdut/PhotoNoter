package com.yydcdut.note.mvp;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IPresenter {
    public static final int RESULT_NOTHING = 1;
    public static final int RESULT_DATA = 2;
    public static final int RESULT_PICTURE = 3;
    public static final int RESULT_DATA_QQ = 4;
    public static final int RESULT_DATA_EVERNOTE = 5;
    public static final int RESULT_DATA_USER = 6;

    public static final int REQUEST_NOTHING = 1;

    /**
     * 注入View，使之能够与View相互响应
     *
     * @param iView
     */
    void attachView(IView iView);

    void detachView();
}
