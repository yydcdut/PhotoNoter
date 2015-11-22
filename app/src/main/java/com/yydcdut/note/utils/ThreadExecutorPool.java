package com.yydcdut.note.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/22.
 */
public class ThreadExecutorPool {
    private static final int MAX_THREAD_POOL_NUMBER = 5;

    private ExecutorService mPool;

    @Inject
    public ThreadExecutorPool() {
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
}
