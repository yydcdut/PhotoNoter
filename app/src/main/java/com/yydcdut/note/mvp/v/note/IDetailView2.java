package com.yydcdut.note.mvp.v.note;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.mvp.IView;

import java.util.List;

/**
 * Created by yuyidong on 16/1/8.
 */
public interface IDetailView2 extends IView {

    void setFontSystem(boolean usSystem);

    void setViewPagerAdapter(List<PhotoNote> list, int position, int comparator);

    int getCurrentPosition();

    void showNote(String title, String content, String createdTime, String editedTime);

    void showExif(String exif);

    void jump2EditTextActivity(int categoryId, int position, int comparator);

    void upAnimation();

    void downAnimation();

    void showPopupMenu();

    void showBlurImage(int width, int height, String path);
}
