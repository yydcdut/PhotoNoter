package com.yydcdut.note.mvp.p.login.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.evernote.edam.type.User;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.login.IUserDetailFragPresenter;
import com.yydcdut.note.mvp.v.login.IUserDetailFragView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;
import com.yydcdut.note.utils.TimeDecoder;

import java.text.DecimalFormat;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 * todo 应该拆分
 */
public class UserDetailFragPresenterImpl implements IUserDetailFragPresenter {
    private IUserDetailFragView mUserDetailFragView;
    private Activity mActivity;
    private Context mContext;
    private UserCenter mUserCenter;
    private RxPhotoNote mRxPhotoNote;
    private RxCategory mRxCategory;
    private RxSandBox mRxSandBox;
    private RxUser mRxUser;
    private LocalStorageUtils mLocalStorageUtils;
    private ThreadExecutorPool mThreadExecutorPool;

    private static final int MESSAGE_LOGIN_QQ_OK = 1;
    private static final int MESSAGE_LOGIN_QQ_FAILED = 3;

    private LocationClient mLocationClient;

    private int mType;
    private String mLocation;

    @Inject
    public UserDetailFragPresenterImpl(Activity activity, @ContextLife("Activity") Context context,
                                       UserCenter userCenter, RxSandBox rxSandBox,
                                       RxCategory rxCategory, RxPhotoNote rxPhotoNote,
                                       LocalStorageUtils localStorageUtils, ThreadExecutorPool threadExecutorPool,
                                       RxUser rxUser) {
        mActivity = activity;
        mContext = context;
        mUserCenter = userCenter;
        mRxCategory = rxCategory;
        mRxPhotoNote = rxPhotoNote;
        mRxSandBox = rxSandBox;
        mLocalStorageUtils = localStorageUtils;
        mThreadExecutorPool = threadExecutorPool;
        mRxUser = rxUser;
    }

    @Override
    public void attachView(IView iView) {
        mUserDetailFragView = (IUserDetailFragView) iView;
        switch (mType) {
            case 0:
                mLocation = mContext.getResources().getString(R.string.uc_unkown);
                mUserDetailFragView.initUserDetail(getLocation(), getUseAge(), getPhone(), getAndroid(), calculateStorage());
                break;
            case 1:
//                mUserDetailFragView.initUserImages(mPhotoNoteDBModel.findByCategoryId(
//                        mCategoryDBModel.findAll().get(0).getId(), ComparatorFactory.FACTORY_NOT_SORT));
                break;
            case 2:
//                mUserDetailFragView.initUserInfo(mUserCenter.isLoginQQ(), getQQName(),
//                        mUserCenter.isLoginEvernote(), getEvernoteName(), getFolderStorage(),
//                        getNotesNumber(), getSandboxNumber(), getWordNumber(), getCloud());
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
        if (mUserCenter.isLoginEvernote()) {
            mUserCenter.logoutEvernote();
            mUserDetailFragView.logoutEvernote();
        } else {
            mUserCenter.doLoginEvernote(mActivity);
        }
    }

    private String getQQName() {
//        mRxUser.isLoginQQ()
//                .subscribe(aBoolean -> {
//                    if (aBoolean){
//                        mRxUser.getQQ()
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(iUser -> iUser.getName());
//                    }
//                })

//        if (mUserCenter.isLoginQQ()) {
//            return mUserCenter.getQQ().getName();
//        } else {
//            return mContext.getResources().getString(R.string.not_login);
//        }
        return null;
    }

    private String getEvernoteName() {
        if (mUserCenter.isLoginEvernote()) {
            User user = mUserCenter.getEvernote();
            if (user == null) {
                return mContext.getResources().getString(R.string.user_failed);
            } else {
                return user.getName();
            }
        } else {
            return mContext.getResources().getString(R.string.not_login);
        }
    }

    private String getFolderStorage() {
        long storage = FilePathUtils.getFolderStorage();
        if (storage == -1) {
            return mContext.getResources().getString(R.string.uc_unkown);
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

//    private String getNotesNumber() {
//        return mPhotoNoteDBModel.getAllNumber() + "";
//    }
//
//    private String getSandboxNumber() {
//        return mSandBoxDBModel.getAllNumber() + "";
//    }

    private String getWordNumber() {
        return mContext.getResources().getString(R.string.uc_unkown);
    }

    private String getCloud() {
        return mContext.getResources().getString(R.string.uc_unkown);
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
        return TimeDecoder.calculateDeltaTime(now, startTime) + " " + mContext.getResources().getString(R.string.uc_usage_age_unit);
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


}
