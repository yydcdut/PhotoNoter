package com.yydcdut.note.mvp.p.home.impl;

import android.app.Activity;

import com.baidu.mapapi.SDKInitializer;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bus.CategoryCreateEvent;
import com.yydcdut.note.bus.CategoryDeleteEvent;
import com.yydcdut.note.bus.CategoryEditEvent;
import com.yydcdut.note.bus.CategoryMoveEvent;
import com.yydcdut.note.bus.CategoryUpdateEvent;
import com.yydcdut.note.bus.PhotoNoteCreateEvent;
import com.yydcdut.note.bus.PhotoNoteDeleteEvent;
import com.yydcdut.note.model.compare.ComparatorFactory;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.IHomePresenter;
import com.yydcdut.note.mvp.v.home.IHomeView;
import com.yydcdut.note.utils.LocalStorageUtils;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/19.
 */
public class HomePresenterImpl implements IHomePresenter {
    private IHomeView mHomeView;
    /**
     * 当前的category的Id
     */
    private int mCategoryId = -1;

    private RxCategory mRxCategory;
    private RxPhotoNote mRxPhotoNote;
    private RxUser mRxUser;
    private LocalStorageUtils mLocalStorageUtils;
    private Activity mActivity;

    @Inject
    public HomePresenterImpl(Activity activity, RxCategory rxCategory, RxPhotoNote rxPhotoNote, RxUser rxUser,
                             LocalStorageUtils localStorageUtils) {
        mRxCategory = rxCategory;
        mRxPhotoNote = rxPhotoNote;
        mRxUser = rxUser;
        mLocalStorageUtils = localStorageUtils;
        mActivity = activity;
    }

    @Override
    public void attachView(IView iView) {
        mHomeView = (IHomeView) iView;
        EventBus.getDefault().register(this);
        initBaiduSdk();
    }

    private void initBaiduSdk() {
        /**
         * 01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err: java.lang.SecurityException: getSubscriberId: Neither user 10067 nor current process has android.permission.READ_PHONE_STATE.
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.os.Parcel.readException(Parcel.java:1599)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.os.Parcel.readException(Parcel.java:1552)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.android.internal.telephony.IPhoneSubInfo$Stub$Proxy.getSubscriberIdForSubscriber(IPhoneSubInfo.java:557)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.telephony.TelephonyManager.getSubscriberId(TelephonyManager.java:2003)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.telephony.TelephonyManager.getSubscriberId(TelephonyManager.java:1984)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.platform.comapi.util.f.e(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.platform.comapi.util.f.b(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.platform.comapi.a.c(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.platform.comapi.c.a(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.mapapi.SDKInitializer.initialize(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.baidu.mapapi.SDKInitializer.initialize(Unknown Source)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.yydcdut.note.mvp.p.home.impl.HomePresenterImpl.initBaiduSdk(HomePresenterImpl.java:64)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.yydcdut.note.mvp.p.home.impl.HomePresenterImpl.attachView(HomePresenterImpl.java:60)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.yydcdut.note.mvp.v.home.impl.HomeActivity.initUiAndListener(HomeActivity.java:114)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.yydcdut.note.mvp.v.BaseActivity.onCreate(BaseActivity.java:137)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.yydcdut.note.mvp.v.home.impl.HomeActivity.onCreate(HomeActivity.java:101)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.Activity.performCreate(Activity.java:6237)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1107)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2369)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2476)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.ActivityThread.-wrap11(ActivityThread.java)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1344)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:102)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.os.Looper.loop(Looper.java:148)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at android.app.ActivityThread.main(ActivityThread.java:5417)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at java.lang.reflect.Method.invoke(Native Method)
         01-04 19:30:50.461 2405-2405/com.yydcdut.note W/System.err:     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:726)

         */
        SDKInitializer.initialize(mActivity.getApplication());
    }

    @Override
    public void detachView() {
        EventBus.getDefault().unregister(this);
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    @Override
    public int getCategoryId() {
        return mCategoryId;
    }

    @Override
    public void setCheckCategoryPosition() {
        //todo
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    boolean checkSuccessful = false;
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).isCheck()) {
                            mHomeView.setCheckPosition(i);
                            checkSuccessful = true;
                            break;
                        }
                    }
                    if (!checkSuccessful) {
                        mHomeView.setCheckPosition(0);
                    }
                });
    }

    @Override
    public void setCheckedCategoryPosition(int position) {
        mRxCategory.getAllCategories()
                .subscribe(categories -> {
                    mRxCategory.setCategoryMenuPosition(categories.get(position).getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(categories1 -> {
                                mHomeView.notifyCategoryDataChanged();
                                mCategoryId = categories1.get(position).getId();
                                mHomeView.changeFragment(mCategoryId);
                            });
                });
    }

    @Override
    public void changeCategoryAfterSaving(Category category) {
        mRxCategory.setCategoryMenuPosition(category.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    mHomeView.notifyCategoryDataChanged();
                    mCategoryId = category.getId();
                    mHomeView.changePhotos4Category(mCategoryId);
                });
    }

    @Override
    public void setAdapter() {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mHomeView.setCategoryList(categories));
    }

    @Override
    public void drawerUserClick(int which) {
        switch (which) {
            case USER_ONE:
                mRxUser.isLoginQQ()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                mHomeView.jump2UserCenterActivity();
                            } else {
                                mHomeView.jump2LoginActivity();
                            }
                        });
                break;
            case USER_TWO:
                mRxUser.isLoginEvernote()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                mHomeView.jump2UserCenterActivity();
                            } else {
                                mHomeView.jump2LoginActivity();
                            }
                        });
                break;
        }
    }

    @Override
    public void drawerCloudClick() {
        mHomeView.cloudSyncAnimation();
    }

    @Override
    public void updateQQInfo() {
        mRxUser.isLoginQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxUser.getQQ()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(iUser -> mHomeView.updateQQInfo(true, iUser.getName(), iUser.getImagePath()));
                    } else {
                        mHomeView.updateQQInfo(false, null, null);
                    }
                });
    }

    @Override
    public void updateEvernoteInfo() {
        mRxUser.isLoginEvernote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mHomeView.updateEvernoteInfo(true);
                    } else {
                        mHomeView.updateEvernoteInfo(false);
                    }
                });

    }

    @Override
    public void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service) {
        //有时候categoryLabel为null，感觉原因是activity被回收了，但是一直解决不掉，所以迫不得已的解决办法
        if (mCategoryId == -1) {
            mRxCategory.getAllCategories()
                    .subscribe(categories -> {
                        for (Category category : categories) {
                            if (category.isCheck()) {
                                mCategoryId = category.getId();
                            }
                        }
                    });
        }

        //从另外个进程过来的数据
        if (broadcast_process) {
            mRxPhotoNote.refreshByCategoryId(mCategoryId, ComparatorFactory.FACTORY_NOT_SORT)
                    .subscribe(photoNoteList -> {
                        mRxCategory.findByCategoryId(mCategoryId)
                                .subscribe(category -> {
                                    category.setPhotosNumber(photoNoteList.size());
                                    mRxCategory.updateCategory(category)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(categories -> {
                                                mHomeView.notifyCategoryDataChanged();
                                            });
                                });
                    });
        }

        //从Service中来
        if (broadcast_service) {
            mRxCategory.getAllCategories()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(categories -> mHomeView.updateCategoryList(categories));
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryCreateEvent(CategoryCreateEvent categoryCreateEvent) {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mHomeView.updateCategoryList(categories));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryUpdateEvent(CategoryUpdateEvent categoryUpdateEvent) {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mHomeView.updateCategoryList(categories));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryMoveEvent(CategoryMoveEvent categoryMoveEvent) {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mHomeView.updateCategoryList(categories));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryRenameEvent(CategoryEditEvent categoryEditEvent) {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mHomeView.updateCategoryList(categories));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryDeleteEvent(CategoryDeleteEvent categoryDeleteEvent) {
        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    int beforeCategoryId = mCategoryId;
                    for (Category category : categories) {
                        if (category.isCheck()) {
                            mCategoryId = category.getId();
                            break;
                        }
                    }
                    mHomeView.updateCategoryList(categories);
                    if (mCategoryId != beforeCategoryId) {
                        mHomeView.changePhotos4Category(mCategoryId);
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPhotoNoteCreateEvent(PhotoNoteCreateEvent photoNoteCreateEvent) {
        mRxPhotoNote.findByCategoryId(mCategoryId, ComparatorFactory.FACTORY_NOT_SORT)
                .subscribe(photoNoteList -> {
                    mRxCategory.findByCategoryId(mCategoryId)
                            .subscribe(category -> {
                                category.setPhotosNumber(photoNoteList.size());
                                mRxCategory.updateCategory(category)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(categories -> {
                                            mHomeView.updateCategoryList(categories);
                                        });
                            });
                });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPhotoNoteDeleteEvent(PhotoNoteDeleteEvent photoNoteDeleteEvent) {
        mRxPhotoNote.findByCategoryId(mCategoryId, ComparatorFactory.FACTORY_NOT_SORT)
                .subscribe(photoNoteList -> {
                    mRxCategory.findByCategoryId(mCategoryId)
                            .subscribe(category -> {
                                category.setPhotosNumber(photoNoteList.size());
                                mRxCategory.updateCategory(category)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(categories -> {
                                            mHomeView.updateCategoryList(categories);
                                        });
                            });
                });
    }
}
