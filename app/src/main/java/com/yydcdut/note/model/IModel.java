package com.yydcdut.note.model;

import com.yydcdut.note.model.observer.IObserver;

/**
 * Created by yuyidong on 15/7/16.
 */
public interface IModel {

    boolean addObserver(IObserver iObserver);
}
