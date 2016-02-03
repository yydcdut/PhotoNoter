package com.yydcdut.note.presenters.note;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 16/1/11.
 */
public interface IMapPresenter extends IPresenter {

    void bindData(int categoryID, int position, int comparator);
}
