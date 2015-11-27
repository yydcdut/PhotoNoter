package com.yydcdut.note.mvp.p.note;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditTextPresenter extends IPresenter {
    void bindData(int categoryId, int position, int comparator);

    void saveText();

    void update2Evernote();

    void finishActivity(boolean saved);

    void startVoice();

    void stopVoice();

    void onBackPressEvent();

}
