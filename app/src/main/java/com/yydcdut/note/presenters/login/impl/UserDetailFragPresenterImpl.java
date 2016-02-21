package com.yydcdut.note.presenters.login.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.user.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.presenters.login.IUserDetailFragPresenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.login.IUserDetailFragView;

import java.text.DecimalFormat;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class UserDetailFragPresenterImpl implements IUserDetailFragPresenter {
    private IUserDetailFragView mUserDetailFragView;
    private Activity mActivity;
    private Context mContext;
    private RxPhotoNote mRxPhotoNote;
    private RxCategory mRxCategory;
    private RxSandBox mRxSandBox;
    private RxUser mRxUser;
    private LocalStorageUtils mLocalStorageUtils;

    private LocationClient mLocationClient;

    private int mType;
    private String mLocation;

    @Inject
    public UserDetailFragPresenterImpl(Activity activity, @ContextLife("Activity") Context context, RxSandBox rxSandBox,
                                       RxCategory rxCategory, RxPhotoNote rxPhotoNote, RxUser rxUser,
                                       LocalStorageUtils localStorageUtils) {
        mActivity = activity;
        mContext = context;
        mRxCategory = rxCategory;
        mRxPhotoNote = rxPhotoNote;
        mRxSandBox = rxSandBox;
        mLocalStorageUtils = localStorageUtils;
        mRxUser = rxUser;
    }

    @Override
    public void attachView(IView iView) {
        mUserDetailFragView = (IUserDetailFragView) iView;
        switch (mType) {
            case 0:
                mLocation = mContext.getResources().getString(R.string.uc_unknown);
                mUserDetailFragView.initUserDetail(getLocation(), getUseAge(), getPhone(), getAndroid(), calculateStorage());
                break;
            case 1:
                mUserDetailFragView.addView();
                mRxUser.isLoginQQ()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                mRxUser.getQQ()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(iUser -> mUserDetailFragView.addQQView(true, iUser.getName()));
                            } else {
                                mUserDetailFragView.addQQView(aBoolean, mContext.getResources().getString(R.string.not_login));
                            }
                        });
                mRxUser.isLoginEvernote()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                mRxUser.getEvernote()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(iUser -> mUserDetailFragView.addEvernoteView(true, iUser.getName()));
                            } else {
                                mUserDetailFragView.addEvernoteView(false, mContext.getResources().getString(R.string.not_login));
                            }
                        });
                mUserDetailFragView.addUseStorageView(getFolderStorage());
                mRxPhotoNote.getAllPhotoNotesNumber()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> mUserDetailFragView.addNoteNumberView(integer + ""));
                mRxSandBox.getNumber()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> mUserDetailFragView.addSandBoxNumber(integer + ""));
                mRxPhotoNote.getWordsNumber()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> mUserDetailFragView.addWordNumber(integer + ""));
                mUserDetailFragView.addCloud(getCloud());

                break;
        }
    }

    @Override
    public void detachView() {
        if (mType == 0 && mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    @Override
    public void bindData(int type) {
        mType = type;
    }

    @Override
    public boolean checkInternet() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            //没有网络
            mUserDetailFragView.showSnakebar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void loginOrOutQQ() {
        mRxUser.isLoginQQ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxUser.logoutQQ()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aBoolean1 -> mUserDetailFragView.logoutQQ());
                    } else {
                        mRxUser.loginQQ(mActivity)
                                .doOnSubscribe(() -> mUserDetailFragView.showProgressBar())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<IUser>() {
                                    @Override
                                    public void onCompleted() {
                                        mUserDetailFragView.hideProgressBar();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mUserDetailFragView.showSnakebar(mContext.getResources().getString(R.string.toast_fail));
                                    }

                                    @Override
                                    public void onNext(IUser iUser) {
                                        mUserDetailFragView.showQQ(iUser.getName(), iUser.getImagePath());
                                        mUserDetailFragView.showSnakebar(mContext.getResources().getString(R.string.toast_success));
                                    }
                                });
                    }
                });
    }

    @Override
    public void loginOrOutEvernote() {
        mRxUser.isLoginEvernote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxUser.logoutEvernote().subscribe();
                        mUserDetailFragView.logoutEvernote();
                    } else {
                        mRxUser.loginEvernote(mActivity);
                    }
                });
    }

    private String getFolderStorage() {
        long storage = FilePathUtils.getFolderStorage();
        if (storage == -1) {
            return mContext.getResources().getString(R.string.uc_unknown);
        } else {
            if (storage > 1024) {
                float storageF = storage / 1024.0f;
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足1位,会以0补足
                return decimalFormat.format(storageF) + "G";
            } else {
                return storage + "M";
            }
        }
    }

    private String getWordNumber() {
        return mContext.getResources().getString(R.string.uc_unknown);
    }

    private String getCloud() {
        return mContext.getResources().getString(R.string.uc_unknown);
    }


    private String getLocation() {
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLocation = bdLocation.getAddress().address;
                mUserDetailFragView.updateLocation(mLocation);
                mLocationClient.stop();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        return mLocation;
    }

    private String getUseAge() {
        long startTime = mLocalStorageUtils.getStartUsageTime();
        long now = System.currentTimeMillis();
        return calculateDeltaTime(now, startTime) + " " + mContext.getResources().getString(R.string.uc_usage_age_unit);
    }

    private String getPhone() {
        return android.os.Build.MODEL + "";
    }

    private String getAndroid() {
        return Build.VERSION.RELEASE;
    }

    private String calculateStorage() {
        long[] storages = FilePathUtils.getSDCardStorage();
        if (storages[0] == -1) {
            return mContext.getResources().getString(R.string.uc_no_sdcard);
        } else {
            if (storages[0] > 1024) {
                float avail = ((float) storages[0]) / 1024;
                float total = ((float) storages[1]) / 1024;
                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足1位,会以0补足
                return (decimalFormat.format(avail) + "G / ") + (decimalFormat.format(total) + "G");
            } else {
                return (storages[0] + "M / ") + (storages[1] + "M");
            }
        }
    }

    /**
     * 与类关系不大，那么用static的速度要快那么几毫秒
     *
     * @param now
     * @param before
     * @return
     */
    private static String calculateDeltaTime(long now, long before) {
        long delta = (now - before) / 1000 / 60 / 60 / 24;
        return (delta + 1) + "";
    }
}
