package com.yydcdut.note.model.observer;

import com.yydcdut.note.bean.Category;

/**
 * Created by yuyidong on 15/10/16.
 */
public interface CategoryChangedObserver extends IObserver {
    void onUpdate(int CRUD, Category category);
}
