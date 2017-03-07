package com.yydcdut.note.utils.performance;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by yuyidong on 2017/3/7.
 */
class LooperMonitor implements Handler.Callback {
    private static final long DELAY_TIME = 1000l;
    private static final int MESSAGE_WHAT = 4399;
    private static LooperMonitor sInstance = new LooperMonitor();
    private HandlerThread mHandlerThread;
    private Handler mLogHandler;

    private LooperMonitor() {
        mHandlerThread = new HandlerThread("log");
        mHandlerThread.start();
        mLogHandler = new Handler(mHandlerThread.getLooper(), this);
    }

    public static LooperMonitor getInstance() {
        return sInstance;
    }

    public void startMonitor() {
        mLogHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY_TIME);
    }

    public void removeMonitor() {
        mLogHandler.removeMessages(MESSAGE_WHAT);
    }

    @Override
    public boolean handleMessage(Message msg) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTraceElements = Looper.getMainLooper().getThread().getStackTrace();
        for (StackTraceElement s : stackTraceElements) {
            sb.append(s.toString() + "\n");
        }
        Log.i("LooperMonitor", sb.toString());
        return false;
    }
}
