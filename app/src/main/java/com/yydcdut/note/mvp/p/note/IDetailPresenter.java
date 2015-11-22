package com.yydcdut.note.mvp.p.note;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailPresenter extends IPresenter {

    void bindData(String categoryLabel, int position, int comparator);

    void showExif();

    void showNote(int position);

    void updateNote(String label, int position, int comparator);

    void jump2EditTextActivity();
}
