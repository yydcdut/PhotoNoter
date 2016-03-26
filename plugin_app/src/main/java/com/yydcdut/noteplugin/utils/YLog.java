package com.yydcdut.noteplugin.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Created by yyd on 15-3-30.
 */
public class YLog {
    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDEBUG(boolean DEBUG) {
        YLog.DEBUG = DEBUG;
    }

    private static boolean DEBUG = true;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, buildMessage(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, buildMessage(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, buildMessage(msg));
        }
    }

    public static void wtf(String tag, String msg) {
        if (DEBUG) {
            Log.wtf(tag, buildMessage(msg));
        }
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(YLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }


        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");


//        return String.format(Locale.US, "[%d] %s: %s",
//                Thread.currentThread().getId(), caller, msg);
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), stringBuilder.toString(), msg);
    }
}
