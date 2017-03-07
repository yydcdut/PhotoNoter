package com.yydcdut.note.utils.performance;

import android.os.Looper;
import android.util.Printer;

/**
 * Created by yuyidong on 2017/3/7.
 */
public class LooperPrinter {
    public static void start() {
        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                if (x.startsWith(">>>>> Dispatching")) {
                    LooperMonitor.getInstance().startMonitor();
                } else if (x.startsWith("<<<<< Finished")) {
                    LooperMonitor.getInstance().removeMonitor();
                }
            }
        });
    }
}
