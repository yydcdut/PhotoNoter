package com.yydcdut.note;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.injector.component.ApplicationComponent;
import com.yydcdut.note.injector.component.DaggerApplicationComponent;
import com.yydcdut.note.injector.module.ApplicationModule;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.YLog;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import us.pinguo.edit.sdk.PGEditImageLoader;
import us.pinguo.edit.sdk.base.PGEditSDK;

/**
 * Created by yyd on 15-3-29.
 */
public class NoteApplication extends Application {
    private static final String TAG = NoteApplication.class.getSimpleName();
    private RefWatcher mRefWatcher;

    private ApplicationComponent mApplicationComponent;

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        initComponent();

        mRefWatcher = LeakCanary.install(this);

        initImageLoader();
        FilePathUtils.initEnvironment(this);
        Evi.init(this);
        if (!isFromOtherProgress()) {
            checkDisks();

            initBaiduSdk();
             /* Camera360 */
            PGEditImageLoader.initImageLoader(this);
            PGEditSDK.instance().initSDK(this);
            /* 语音 */
            SpeechUtility.createUtility(this, "appid=" + "55cc5db3");
        }

        //打点
        MobclickAgent.setDebugMode(BuildConfig.LOG_DEBUG);
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setCatchUncaughtExceptions(!BuildConfig.LOG_DEBUG);

//        CrashHandler.getInstance().init(getApplicationContext());

        YLog.setDEBUG(BuildConfig.LOG_DEBUG);
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderManager.init(getApplicationContext());
    }

    private void initBaiduSdk() {
        SDKInitializer.initialize(this);
    }

    private boolean isFromOtherProgress() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (pid == appProcess.pid) {
                if (appProcess.processName.equals("com.yydcdut.note:cameraphots") ||
                        appProcess.processName.equals("com.yydcdut.note:remote") ||
                        appProcess.processName.equals("com.yydcdut.note:makephotos")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    private void checkDisks() {
        if (!mApplicationComponent.getLocalStorageUtils().isFirstTime()) {
            Observable.from(new File(FilePathUtils.getPath()).listFiles())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .filter(new Func1<File, Boolean>() {//过滤掉 文件夹
                        @Override
                        public Boolean call(File file) {
                            return !file.isDirectory();
                        }
                    })
                    .filter(new Func1<File, Boolean>() {//过滤出 jpg、png、JPEG
                        @Override
                        public Boolean call(File file) {
                            return file.getName().toLowerCase().endsWith(".jpg") ||
                                    file.getName().toLowerCase().endsWith(".png") ||
                                    file.getName().toLowerCase().endsWith(".jpeg");
                        }
                    })
                    .subscribe(new Observer<File>() {
                        private int mNumber = 0;

                        @Override
                        public void onCompleted() {
                            int dbNumber = mApplicationComponent.getPhotoNoteDBModel().getAllNumber();
                            if (mNumber != dbNumber) {
                                Intent checkIntent = new Intent(getApplicationContext(), CheckService.class);
                                startService(checkIntent);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(File file) {
                            mNumber++;
                        }
                    });
        }
    }

}
