package com.yydcdut.note.model.rx;

import android.content.Context;

import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.dao.SandBoxDB;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/27.
 */
public class RxSandBox {
    private SandBoxDB mSandBoxDB;

    @Singleton
    @Inject
    public RxSandBox(@ContextLife("Application") Context context) {
        mSandBoxDB = new SandBoxDB(context);
    }

    /**
     * 找到第一个
     *
     * @return
     */
    public Observable<SandPhoto> findFirstOne() {
        return Observable.create(new Observable.OnSubscribe<SandPhoto>() {
            @Override
            public void call(Subscriber<? super SandPhoto> subscriber) {
                subscriber.onNext(mSandBoxDB.findFirstOne());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 找到所有
     */
    public Observable<List<SandPhoto>> findAll() {
        return Observable.create(new Observable.OnSubscribe<List<SandPhoto>>() {
            @Override
            public void call(Subscriber<? super List<SandPhoto>> subscriber) {
                subscriber.onNext(mSandBoxDB.finaAll());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存一个SandPhoto
     *
     * @param sandPhoto
     * @return
     */
    public Observable<SandPhoto> saveOne(SandPhoto sandPhoto) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                subscriber.onNext(mSandBoxDB.save(sandPhoto));
            }
        })
                .subscribeOn(Schedulers.io())
                .map(aLong -> mSandBoxDB.findById(aLong));
    }

    /**
     * 删除
     *
     * @param sandPhoto
     */
    public Observable<Integer> deleteOne(SandPhoto sandPhoto) {
        return Observable.just(sandPhoto)
                .subscribeOn(Schedulers.io())
                .map(sandPhoto1 -> mSandBoxDB.delete(sandPhoto1));
    }

    /**
     * 得到sandPhoto数量
     */
    public Observable<Integer> getNumber() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mSandBoxDB.getAllNumber());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
