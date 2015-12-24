package com.yydcdut.note.mvp.v.note;

import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.view.fab2.snack.OnSnackBarActionListener;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditTextView extends IView {

    String getNoteTitle();

    String getNoteContent();

    void setNoteTitle(String title);

    void setNoteContent(String content);

    void showProgressBar();

    void hideProgressBar();

    void showSnakeBar(String messge);

    void showSnakeBarWithAction(String message, String action, OnSnackBarActionListener listener);

    void showToast(String message);

    void hideVoiceAnimation();

    void setRippleVoice(float volume);

    boolean isFabMenuLayoutOpen();

    void closeFabMenuLayout();

    void setFabMenuLayoutClickable(boolean clickable);

    void finishActivityWithAnimation(boolean saved, int categoryId, int position, int comparator);

}
