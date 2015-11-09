package com.yydcdut.note;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.evernote.client.android.EvernoteSession;
import com.github.mmin18.layoutcast.LayoutCast;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.YLog;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import us.pinguo.edit.sdk.PGEditImageLoader;
import us.pinguo.edit.sdk.base.PGEditSDK;

/**
 * Created by yyd on 15-3-29.
 */
public class NoteApplication extends Application {
    private static final String TAG = NoteApplication.class.getSimpleName();
    private static NoteApplication mInstance;
    private static final int MAX_THREAD_POOL_NUMBER = 5;
    private ExecutorService mPool;
    private RefWatcher mRefWatcher;

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    private static Context mContext;

    public NoteApplication() {
        mContext = this;
    }

    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Application context is null. Maybe you haven\'t configured your application name with \"org.litepal.LitePalApplication\" in your AndroidManifest.xml. Or you can write your own application class, but remember to extend LitePalApplication as parent class.");
        } else {
            return mContext;
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        mContext = this.getApplicationContext();
    }

    @Override
    public void onCreate() {
        mInstance = NoteApplication.this;
        super.onCreate();

        mRefWatcher = LeakCanary.install(this);

        initImageLoader();
        initExecutor();
        FilePathUtils.initEnvironment();
        Evi.init();
        initService();
        if (!isFromOtherProgress()) {
            initUser();
            initBaiduSdk();
             /* Camera360 */
            PGEditImageLoader.initImageLoader(this);
            PGEditSDK.instance().initSDK(this);
        }

        //打点
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setCatchUncaughtExceptions(true);

//        CrashHandler.getInstance().init(getApplicationContext());

        YLog.setDEBUG(true);

        if (BuildConfig.DEBUG) {
            LayoutCast.init(this);
        }
    }


    /**
     * 获得application变量
     *
     * @return
     */
    public static NoteApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化线程池
     */
    private void initExecutor() {
        mPool = Executors.newFixedThreadPool(MAX_THREAD_POOL_NUMBER);
    }

    /**
     * 获得线程池
     *
     * @return
     */
    public ExecutorService getExecutorPool() {
        return mPool;
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderManager.init(getApplicationContext());
    }


    /**
     * 在服务里面初始化一些东西
     */
    private void initService() {
        if (!LocalStorageUtils.getInstance().isFirstTime()) {
            int dbNumber = PhotoNoteDBModel.getInstance().getAllNumber();
            File file = new File(FilePathUtils.getPath());
            int fileNumber = 0;
            File[] fileArr = file.listFiles();
            for (File file1 : fileArr) {
                if (file1.isDirectory()) {
                    continue;
                }
                if (file1.getName().toLowerCase().endsWith("jpg") ||
                        file1.getName().toLowerCase().endsWith("png") ||
                        file1.getName().toLowerCase().endsWith("jpeg")) {
                    fileNumber++;
                }
            }
            if (fileNumber == dbNumber) {
                Intent checkIntent = new Intent(this, CheckService.class);
                startService(checkIntent);
            }
        }
    }

    private void initUser() {
        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
                .build(BuildConfig.EVERNOTE_CONSUMER_KEY, BuildConfig.EVERNOTE_CONSUMER_SECRET)
                .asSingleton();
        if (UserCenter.getInstance().isLoginEvernote()) {
            UserCenter.getInstance().LoginEvernote();
        }
    }

    private void initBaiduSdk() {
        SDKInitializer.initialize(getContext());
    }

    private boolean isFromOtherProgress() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
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

}
