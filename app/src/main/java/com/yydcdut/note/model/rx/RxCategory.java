package com.yydcdut.note.model.rx;

import android.content.Context;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.dao.CategoryDB;
import com.yydcdut.note.model.rx.exception.RxException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/26.
 * todo: 有关涉及到的mCache的重构，contact() merge()
 */
public class RxCategory {
    private List<Category> mCache;

    private CategoryDB mCategoryDB;

    @Singleton
    @Inject
    public RxCategory(@ContextLife("Application") Context context) {
        mCategoryDB = new CategoryDB(context);
        mCache = mCategoryDB.findAll();
    }

    /**
     * 获得所有的category
     *
     * @return
     */
    public Observable<List<Category>> getAllCategories() {
        return Observable.create(new Observable.OnSubscribe<List<Category>>() {
            @Override
            public void call(Subscriber<? super List<Category>> subscriber) {
                subscriber.onNext(mCache);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 强制刷新缓存
     *
     * @return
     */
    public Observable<List<Category>> refreshCategories() {
        return Observable.create(new Observable.OnSubscribe<List<Category>>() {
            @Override
            public void call(Subscriber<? super List<Category>> subscriber) {
                mCache.clear();
                mCache.addAll(mCategoryDB.findAll());
                subscriber.onNext(mCache);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 设置check哪个category
     *
     * @param _id
     * @return
     */
    public Observable<List<Category>> setCategoryMenuPosition(int _id) {
        return Observable.from(mCache)
                .subscribeOn(Schedulers.io())
                .filter(category -> category.isCheck())//过滤出check为true的
                .map(category3 -> {
                    category3.setCheck(false);
                    mCategoryDB.update(category3);
                    return mCache;
                })
                .flatMap(categories -> Observable.from(categories))//转换成一个个Category来处理
                .filter(category1 -> category1.getId() == _id)//过滤出与ID相同的Category
                .lift(new Observable.Operator<Category, Category>() {
                    @Override
                    public Subscriber<? super Category> call(Subscriber<? super Category> subscriber) {
                        return new Subscriber<Category>() {
                            /* 因为我想经过filter之后，如果没有数据就返回onError,所以设置这个参数 */
                            private int mInTimes = 0;

                            @Override
                            public void onCompleted() {
                                if (mInTimes == 0) {
                                    subscriber.onError(new RxException("找不到这个ID的Category"));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Category category) {
                                mInTimes++;
                                subscriber.onNext(category);
                            }
                        };
                    }
                })
                .map(category2 -> {
                    category2.setCheck(true);
                    int row = mCategoryDB.update(category2);
                    return row > 0 ? mCache : mCategoryDB.findAll();
                });
    }

    /**
     * 添加Category，这个category的check是true
     * 要做的事情：
     * 1、判断是否有这个字段了
     * 2、将其他的Category都取消check
     *
     * @param label
     * @param photosNumber
     * @param sort
     * @param isCheck
     * @return
     */
    public Observable<List<Category>> saveCategory(String label, int photosNumber, int sort, boolean isCheck) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            private int mInTimes = 0;

            @Override
            public void call(Subscriber<? super Long> subscriber) {
                boolean exist = checkLabelExist(label);
                if (exist && mInTimes == 0) {
                    //在没有mInTimes的时候，这里也会执行，不知道为啥.......
                    subscriber.onError(new RxException("这个Label已经有了"));
                } else {
                    mInTimes++;
                    long id = mCategoryDB.save(label, photosNumber, sort, /* isCheck */true);
                    if (mCache.size() != 0) {
                        subscriber.onNext(id);
                        subscriber.onCompleted();
                    } else {
                        //如果mCache中没有数据，直接跳到lift中
                        subscriber.onCompleted();
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .map(aLong -> mCache)//重新获取cache数据
                .flatMap(categories1 -> Observable.from(categories1))//转换成一个个的
                .filter(category -> category.isCheck())//过滤出check为true的
                .lift(new Observable.Operator<List<Category>, Category>() {
                    @Override
                    public Subscriber<? super Category> call(Subscriber<? super List<Category>> subscriber) {
                        return new Subscriber<Category>() {
                            @Override
                            public void onCompleted() {
                                mCache.clear();
                                mCache.addAll(mCategoryDB.findAll());
                                subscriber.onNext(mCache);
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(Category category3) {
                                //如果有check为true的话，进入到这里，如果没有的话直接进入到onCompleted
                                category3.setCheck(false);
                                mCategoryDB.update(category3);
                            }
                        };
                    }
                });
    }

    /**
     * 判断这个ShowLabel是否存在
     *
     * @param newLabel
     * @return
     */
    private boolean checkLabelExist(String newLabel) {
        for (Category item : mCache) {
            if (item.getLabel().equals(newLabel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新Category
     *
     * @param category
     * @return
     */
    public Observable<List<Category>> updateCategory(Category category) {
        return Observable.from(mCache)
                .subscribeOn(Schedulers.io())
                .filter(category1 -> category.getId() == category1.getId())//判断有没有这个category
                .map(category2 -> {
                    mCategoryDB.update(category);
                    return mCache;
                });
    }


    /**
     * 更新Label
     *
     * @param categoryId
     * @param newLabel
     * @return
     */
    public Observable<List<Category>> updateLabel(int categoryId, String newLabel) {
        return Observable.from(mCache)
                .subscribeOn(Schedulers.io())
                .filter(category1 -> !category1.getLabel().equals(newLabel))//先判断是不是有Category
                .filter(category2 -> category2.getId() == categoryId)//过滤出就是这个id的Category
                .map(category -> {
                    category.setLabel(newLabel);
                    mCategoryDB.update(category);
                    return mCache;
                });
    }

    /**
     * 当做了转移图片到其他Category的时候，做更新操作
     *
     * @param oldCategoryId
     * @param targetCategoryId
     * @param changeNumber
     * @return
     */
    public Observable<List<Category>> updateChangeCategory(int oldCategoryId, int targetCategoryId, int changeNumber) {
        return Observable.just(oldCategoryId)//先处理旧的
                .subscribeOn(Schedulers.io())
                .map(integer -> findCategoryByIdInCache(integer))//找到Category
                .map(category -> {//做操作
                    category.setPhotosNumber(category.getPhotosNumber() - changeNumber);
                    mCategoryDB.update(category);
                    return targetCategoryId;
                })
                .map(integer1 -> findCategoryByIdInCache(integer1))
                .map(category1 -> {
                    category1.setPhotosNumber(category1.getPhotosNumber() + changeNumber);
                    mCategoryDB.update(category1);
                    return mCache;
                });
    }

    /**
     * 通过ID在cache中找到Category
     *
     * @param id
     * @return
     */
    private Category findCategoryByIdInCache(int id) {
        for (Category category : mCache) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

    /**
     * 更新顺序
     *
     * @return
     */
    public Observable<List<Category>> updateOrder() {
        return Observable.create(new Observable.OnSubscribe<List<Category>>() {
            @Override
            public void call(Subscriber<? super List<Category>> subscriber) {
                for (int i = 0; i < mCache.size(); i++) {
                    Category category = mCache.get(i);
                    category.setSort(i);
                    mCategoryDB.update(category);
                }
                subscriber.onNext(mCache);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<List<Category>> delete(int id) {
        return Observable.just(id)
                .subscribeOn(Schedulers.io())
                .map(integer -> findCategoryByIdInCache(id))
                .map(category -> {
                    mCategoryDB.delete(category);
                    mCache.clear();
                    mCache.addAll(mCategoryDB.findAll());
                    return mCache;
                });
    }

    public Observable<Category> findByCategoryId(int id) {
        return Observable.from(mCache)
                .subscribeOn(Schedulers.computation())
                .filter(category -> category.getId() == id);
    }


}
