package com.yydcdut.note.model.observer;

/**
 * Created by yuyidong on 15/10/16.
 */
public interface IObserver {
    int OBSERVER_PHOTONOTE_CREATE = 1;
    int OBSERVER_PHOTONOTE_RETRIEVE = 2;
    int OBSERVER_PHOTONOTE_UPDATE = 3;
    int OBSERVER_PHOTONOTE_DELETE = 4;

}
