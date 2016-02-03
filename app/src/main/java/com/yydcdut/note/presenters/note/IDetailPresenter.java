package com.yydcdut.note.presenters.note;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailPresenter extends IPresenter {

    void bindData(int categoryID, int position, int comparator);

    void showExif(int position);

    void showNote(int position);

    void updateNote(int categoryId, int position, int comparator);

    void jump2EditTextActivity();

    void jump2MapActivity();

    void doCardViewAnimation();

    void showMenuIfNotHidden();
}
