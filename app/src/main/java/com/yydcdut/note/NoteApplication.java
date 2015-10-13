package com.yydcdut.note;

import android.content.Intent;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.service.InitService;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.YLog;

import org.litepal.LitePalApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import us.pinguo.edit.sdk.PGEditImageLoader;
import us.pinguo.edit.sdk.base.PGEditSDK;

/**
 * Created by yyd on 15-3-29.
 */
public class NoteApplication extends LitePalApplication {
    private static final String TAG = NoteApplication.class.getSimpleName();
    private static NoteApplication mInstance;
    private static final int MAX_THREAD_POOL_NUMBER = 5;
    private ExecutorService mPool;
    private RefWatcher mRefWatcher;

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

        /* Camera360 */
        PGEditImageLoader.initImageLoader(this);
        PGEditSDK.instance().initSDK(this);

        //打点
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setCatchUncaughtExceptions(false);

//        CrashHandler.getInstance().init(getApplicationContext());

        YLog.setDEBUG(true);
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
            Intent initIntent = new Intent(this, InitService.class);
            startService(initIntent);
        } else {
            Intent checkIntent = new Intent(this, CheckService.class);
            startService(checkIntent);
        }
    }

}
