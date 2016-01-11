package com.yydcdut.note.utils;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by yuyidong on 16/1/11.
 */
public class RxImageBlur {
    static {
        System.loadLibrary("blur");
    }

    public static native void blur(Bitmap bitmap, int r);

    public static Bitmap doBlur(Bitmap sentBitmap, int radius) {
        Log.i("yuyidong", "sentBitmap 111 " + sentBitmap.toString() + "   ");
        Bitmap bitmap;
        bitmap = sentBitmap;

        if (radius < 1) {
            return (null);
        }
        //Jni BitMap
        blur(bitmap, radius);
        Log.i("yuyidong", "bitmap 222 " + bitmap.toString() + "   ");

        return bitmap;
    }
}
