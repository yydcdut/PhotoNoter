package com.yydcdut.note;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.injector.component.ApplicationComponent;
import com.yydcdut.note.injector.component.DaggerApplicationComponent;
import com.yydcdut.note.injector.module.ApplicationModule;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.YLog;

import us.pinguo.edit.sdk.PGEditImageLoader;

/**
 * Created by yyd on 15-3-29.
 */
public class NoteApplication extends Application {
    private static final String TAG = NoteApplication.class.getSimpleName();
//    private RefWatcher mRefWatcher;

    private ApplicationComponent mApplicationComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initComponent();

//        mRefWatcher = LeakCanary.install(this);

        initImageLoader();
        FilePathUtils.initEnvironment(this);
        Evi.init(this);
        if (!isFromOtherProgress()) {
            initBaiduSdk();
             /* Camera360 */
            PGEditImageLoader.initImageLoader(this);

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
                if (appProcess.processName.equals("com.yydcdut.note:cameraphotos") ||
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

}
