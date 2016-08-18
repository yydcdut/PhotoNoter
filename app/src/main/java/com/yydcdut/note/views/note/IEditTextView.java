package com.yydcdut.note.views.note;

import com.yydcdut.note.views.IView;
import com.yydcdut.note.widget.fab.OnSnackBarActionListener;
import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.RxMDEditText;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditTextView extends IView {

    void initEditView(RxMDConfiguration rxMDConfiguration);

    String getNoteTitle();

    String getNoteContent();

    void setEditNoteTitle(String title);

    void setNoteContent(String content);

    RxMDEditText getRxMDEditText();

    void showProgressBar();

    void hideProgressBar();

    void showSnakeBar(String message);

    void showSnakeBarWithAction(String message, String action, OnSnackBarActionListener listener);

    void showToast(String message);

    void hideVoiceAnimation();

    void setRippleVoice(float volume);

    boolean isFabMenuLayoutOpen();

    void closeFabMenuLayout();

    void setFabMenuLayoutClickable(boolean clickable);

    void finishActivityWithAnimation(boolean saved, int categoryId, int position, int comparator);

    void clearMarkdownPreview();

}
