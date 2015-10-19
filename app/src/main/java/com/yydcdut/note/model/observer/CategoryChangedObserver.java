package com.yydcdut.note.model.observer;

/**
 * Created by yuyidong on 15/10/16.
 */
public interface CategoryChangedObserver extends IObserver {
    void onUpdate(int CRUD);
}
