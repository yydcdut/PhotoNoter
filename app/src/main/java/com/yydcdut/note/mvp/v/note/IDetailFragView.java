package com.yydcdut.note.mvp.v.note;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailFragView extends IView {

    void showImage(String path);

    void jump2ZoomActivity(int categoryId, int position, int comparator);

}
