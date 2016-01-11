package com.yydcdut.note.mvp.p.note;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 16/1/11.
 */
public interface IMapPresenter extends IPresenter {

    void bindData(int categoryID, int position, int comparator);
}
