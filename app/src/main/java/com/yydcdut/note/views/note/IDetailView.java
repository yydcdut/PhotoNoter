package com.yydcdut.note.views.note;

import android.support.annotation.DrawableRes;
import android.text.SpannableStringBuilder;

import com.yydcdut.note.entity.PhotoNote;
import com.yydcdut.note.views.IView;

import java.util.List;

/**
 * Created by yuyidong on 16/1/8.
 */
public interface IDetailView extends IView {

    void setFontSystem(boolean usSystem);

    void setViewPagerAdapter(List<PhotoNote> list, int position, int comparator);

    int getCurrentPosition();

    void showNoteWithoutContent(String title, String createdTime, String editedTime);

    void showContent(SpannableStringBuilder ssb);

    void showExif(String exif);

    void initAnimationView();

    void jump2EditTextActivity(int categoryId, int position, int comparator);

    void jump2MapActivity(int categoryId, int position, int comparator);

    void upAnimation();

    void downAnimation();

    void showPopupMenu();

    void showFabIcon(@DrawableRes int iconRes);

    void showSnackBar(String content);
}
