package com.yydcdut.note.mvp.v.note;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditTextView {

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

    void finishActivitywithAnimation(boolean saved, String category, int position, int comparator);

    interface OnSnackBarActionListener {
        void onClick();
    }
}
