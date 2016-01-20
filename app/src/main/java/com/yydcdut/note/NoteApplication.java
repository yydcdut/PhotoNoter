package com.yydcdut.note;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.injector.component.ApplicationComponent;
import com.yydcdut.note.injector.component.DaggerApplicationComponent;
import com.yydcdut.note.injector.module.ApplicationModule;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.YLog;

import java.io.File;
import java.io.IOException;

import us.pinguo.edit.sdk.PGEditImageLoader;

/**
 * Created by yyd on 15-3-29.
 */
public class NoteApplication extends Application {
    private static final String TAG = NoteApplication.class.getSimpleName();

    private ApplicationComponent mApplicationComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //开启dex进程的话也会进入application
        if (isDexProcess()) {
            return;
        }
        doInstallBeforeLollipop();
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isDexProcess()) {
            return;
        }

        LeakCanary.install(this);
//        BlockCanary.install(this, new NoteBlockCanaryContext(this)).start();

        initComponent();
        Utils.init(this);
        initImageLoader();
        PGEditImageLoader.initImageLoader(this);

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

    private void initComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    private boolean isAppFirstInstall() {
        SharedPreferences sharedPreferences = getSharedPreferences("install", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("firstInstall", true);
    }

    private void setAppNoteFirstInstall() {
        SharedPreferences sharedPreferences = getSharedPreferences("install", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("firstInstall", false).commit();
    }

    private boolean existTempFile() {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "photo.note";
        return new File(filePath).exists();
    }

    private void createTempFile() throws IOException {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "photo.note";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
    }

    private void startDexProcess() {
        Intent intent = new Intent(this, DexActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void doInstallBeforeLollipop() {
        //满足3个条件，1.第一次安装开启，2.主进程，3.API<21(因为21之后ART的速度比dalvik快接近10倍(毕竟5.0之后的手机性能也要好很多))
        if (isAppFirstInstall() && !isDexProcessOrOtherProcesses() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                createTempFile();
                startDexProcess();
                while (true) {
                    if (existTempFile()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        setAppNoteFirstInstall();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isDexProcessOrOtherProcesses() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (pid == appProcess.pid) {
                if (appProcess.processName.equals("com.yydcdut.note:dex") ||
                        appProcess.processName.equals("com.yydcdut.note:cameraphotos") ||
                        appProcess.processName.equals("com.yydcdut.note:remote") ||
                        appProcess.processName.equals("com.yydcdut.note:makephotos")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDexProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (pid == appProcess.pid) {
                if (appProcess.processName.equals("com.yydcdut.note:dex")) {
                    return true;
                }
            }
        }
        return false;
    }
}
