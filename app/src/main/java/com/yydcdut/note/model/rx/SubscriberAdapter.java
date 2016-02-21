package com.yydcdut.note.model.rx;

import com.yydcdut.note.utils.YLog;

import rx.Subscriber;

/**
 * Created by yuyidong on 16/2/21.
 */
public class SubscriberAdapter<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        YLog.i("yuyidong", "SubscriberAdapter onError message-->" + e.getMessage());
    }

    @Override
    public void onNext(T t) {

    }
}
