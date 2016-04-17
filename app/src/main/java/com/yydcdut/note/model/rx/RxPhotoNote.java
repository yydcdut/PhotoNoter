package com.yydcdut.note.model.rx;

import android.content.Context;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.compare.ComparatorFactory;
import com.yydcdut.note.model.dao.PhotoNoteDB;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/27.
 * FIXME: 测试的时候把 subscribeOn() 注释掉
 * todo: 有关涉及到的mCache的重构，contact() merge()
 */
public class RxPhotoNote {
    private Map<Integer, List<PhotoNote>> mCache;

    private PhotoNoteDB mPhotoNoteDB;

    @Singleton
    @Inject
    public RxPhotoNote(@ContextLife("Application") Context context) {
        mPhotoNoteDB = new PhotoNoteDB(context);
        mCache = new HashMap<>();
    }

    /**
     * 查找
     *
     * @param categoryId
     * @return
     */
    public Observable<List<PhotoNote>> findByCategoryId(int categoryId, int comparatorFactory) {
        return Observable.just(categoryId)
                .subscribeOn(Schedulers.io())
                .map(integer -> mCache.get(integer))//通过categoryId从缓存中找到数据
                .map(photoNoteList -> {//如果数据为空，从数据库中取数据，如果不为空，直接使用这数据，最后经过排序之后返回
                    if (photoNoteList == null) {
                        photoNoteList = mPhotoNoteDB.findByCategoryId(categoryId);
                        mCache.put(categoryId, photoNoteList);
                    }
                    sortList(photoNoteList, comparatorFactory);
                    return photoNoteList;
                });
    }

    /**
     * 刷新
     *
     * @param categoryId
     * @param comparatorFactory
     * @return
     */
    public Observable<List<PhotoNote>> refreshByCategoryId(int categoryId, int comparatorFactory) {
        return Observable.just(categoryId)
                .subscribeOn(Schedulers.io())
                .map(integer -> mPhotoNoteDB.findByCategoryId(integer))//通过categoryId从数据库中找到数据
                .map(photoNoteList -> {//经过排序之后返回
                    sortList(photoNoteList, comparatorFactory);
                    mCache.put(categoryId, photoNoteList);
                    return photoNoteList;
                });
    }

    public Observable<List<PhotoNote>> updatePhotoNotes(List<PhotoNote> photoNoteList) {
        if (photoNoteList.size() == 0) {
            throw new IllegalArgumentException("参数的长度为0");
        }
        int categoryId = photoNoteList.get(0).getCategoryId();
        return Observable.from(photoNoteList)
                .subscribeOn(Schedulers.io())
                .map(photoNote1 -> mPhotoNoteDB.update(photoNote1))//更新
                .map(integer -> categoryId)//得到CategoryId
                .lift(new Observable.Operator<List<PhotoNote>, Integer>() {
                    @Override
                    public Subscriber<? super Integer> call(Subscriber<? super List<PhotoNote>> subscriber) {
                        return new Subscriber<Integer>() {
                            private int mCategoryId = -1;

                            @Override
                            public void onCompleted() {
                                if (mCategoryId != -1) {
                                    subscriber.onNext(mCache.get(mCategoryId));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Integer integer) {
                                mCategoryId = integer;
                            }
                        };
                    }
                });

    }

    public Observable<List<PhotoNote>> updatePhotoNote(PhotoNote photoNote) {
        return Observable.just(photoNote)
                .subscribeOn(Schedulers.io())
                .map(photoNote1 -> mPhotoNoteDB.update(photoNote1))//更新
                .map(integer -> photoNote.getCategoryId())//得到CategoryId
                .map(integer1 -> mCache.get(integer1));//返回数据

    }

    public Observable<List<PhotoNote>> savePhotoNotes(List<PhotoNote> photoNoteList) {
        return Observable.from(photoNoteList)
                .subscribeOn(Schedulers.io())
                .filter(photoNote -> photoNote.getId() == PhotoNote.NO_ID)
                .map(photoNote1 -> mPhotoNoteDB.save(photoNote1))
                .filter(aLong -> aLong != -1)
                .map(aLong1 -> mPhotoNoteDB.findByPhotoNoteId(aLong1))
                .lift(new Observable.Operator<List<PhotoNote>, PhotoNote>() {
                    @Override
                    public Subscriber<? super PhotoNote> call(Subscriber<? super List<PhotoNote>> subscriber) {
                        return new Subscriber<PhotoNote>() {
                            private int mCategoryId = -1;

                            @Override
                            public void onCompleted() {
                                if (mCategoryId != -1) {
                                    mCache.remove(mCategoryId);
                                    mCache.put(mCategoryId, mPhotoNoteDB.findByCategoryId(mCategoryId));
                                    subscriber.onNext(mCache.get(mCategoryId));
                                }

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(PhotoNote photoNote) {
                                mCategoryId = photoNote.getId();
                            }
                        };
                    }
                });

    }

    public Observable<PhotoNote> savePhotoNote(PhotoNote photoNote) {
        return Observable.just(photoNote)
                .subscribeOn(Schedulers.io())
                .filter(photoNote1 -> photoNote1.getId() == PhotoNote.NO_ID)//确定这个是新的PhotoNote
                .map(photoNote2 -> mPhotoNoteDB.save(photoNote2))//保存
                .filter(aLong -> aLong != -1)//获取到ID
                .map(aLong1 -> mPhotoNoteDB.findByPhotoNoteId(aLong1))//通过这个ID再找到存在数据库中的
                .map(photoNote4 -> {//存到缓存中
                    mCache.remove(photoNote4.getCategoryId());
                    mCache.put(photoNote4.getCategoryId(), mPhotoNoteDB.findByCategoryId(photoNote4.getCategoryId()));
                    return photoNote4;
                });
    }

    public Observable<List<PhotoNote>> deletePhotoNotes(List<PhotoNote> photoNoteList, int categoryId) {
//        if (photoNoteList.size() == 0) {
//            throw new IllegalArgumentException("参数的长度为0");
//        }
//        int categoryId = photoNoteList.get(0).getCategoryId();
        return Observable.from(photoNoteList)
                .subscribeOn(Schedulers.io())
                .map(photoNote1 -> mPhotoNoteDB.delete(photoNote1))//做删除操作
                .filter(integer -> integer > 0)//返回的是删除的条数
                .lift(new Observable.Operator<List<PhotoNote>, Integer>() {
                    @Override
                    public Subscriber<? super Integer> call(Subscriber<? super List<PhotoNote>> subscriber) {
                        return new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                mCache.remove(categoryId);
                                mCache.put(categoryId, mPhotoNoteDB.findByCategoryId(categoryId));
                                subscriber.onNext(mCache.get(categoryId));
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Integer integer) {
                            }
                        };
                    }
                });
    }

    public Observable<List<PhotoNote>> deletePhotoNote(PhotoNote photoNote) {
        int categoryId = photoNote.getCategoryId();
        return Observable.just(photoNote)
                .subscribeOn(Schedulers.io())
                .map(photoNote1 -> mPhotoNoteDB.delete(photoNote1))//做删除操作
                .filter(integer -> integer > 0)//返回的是删除的条数
                .map(integer1 -> {
                    mCache.remove(categoryId);
                    mCache.put(categoryId, mPhotoNoteDB.findByCategoryId(categoryId));
                    return mCache.get(categoryId);
                });

    }

    public Observable<Integer> getAllPhotoNotesNumber() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mPhotoNoteDB.getAllNumber());
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Integer> getWordsNumber() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mPhotoNoteDB.getWordsNumber());
                subscriber.onCompleted();
            }
        });
    }

    private void sortList(List<PhotoNote> photoNoteList, int comparatorFactory) {
        if (comparatorFactory != ComparatorFactory.FACTORY_CREATE_FAR ||
                comparatorFactory != ComparatorFactory.FACTORY_CREATE_CLOSE ||
                comparatorFactory != ComparatorFactory.FACTORY_EDITED_FAR ||
                comparatorFactory != ComparatorFactory.FACTORY_EDITED_CLOSE) {
            return;
        }
        Collections.sort(photoNoteList, ComparatorFactory.get(comparatorFactory));
    }


}
