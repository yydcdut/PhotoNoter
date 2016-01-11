package com.yydcdut.note.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 16/1/11.
 */
public class RxImageBlur {
    private WeakReference<Context> mContextWeakReference;
    private static RxImageBlur mRxImageBlur;
    private int mRadius = 2;

    static {
        System.loadLibrary("blur");
    }

    private native void blur(Bitmap bitmap, int r);

    public RxImageBlur(Context context) {
        mContextWeakReference = new WeakReference<>(context);
    }

    public static RxImageBlur with(Context context) {
        mRxImageBlur = new RxImageBlur(context);
        return mRxImageBlur;
    }

    public RxImageBlur radius(int radius) {
        mRadius = radius;
        return this;
    }

    public Observable<Drawable> blur(Bitmap bitmap) {
        return Observable.just(bitmap)
                .subscribeOn(Schedulers.computation())
                .filter(bitmap3 -> mContextWeakReference.get() != null)
                .filter(bitmap4 -> bitmap4 != null)
                .map(bitmap1 -> getOverlay(bitmap1))
                .map(bitmap2 -> new BitmapDrawable(mContextWeakReference.get().getResources(), bitmap2));
    }

    private Bitmap getOverlay(Bitmap bitmap) {
        float scaleFactor = 8;
        Bitmap overlay = Bitmap.createBitmap((int) (bitmap.getWidth() / scaleFactor),
                (int) (bitmap.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        blur(overlay, mRadius);
        return overlay;
    }

}
