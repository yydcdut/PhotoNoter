package com.yydcdut.note.utils.performance;

import android.os.Build;
import android.view.Choreographer;

/**
 * Created by yuyidong on 2017/3/7.
 */
public class ChoreographerPrinter {

    public static void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    if (LogMonitor.getInstance().hasMonitor()) {
                        LogMonitor.getInstance().removeMonitor();
                    }
                    LogMonitor.getInstance().startMonitor();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Choreographer.getInstance().postFrameCallback(this);
                    }
                }
            });
        }
    }
}
