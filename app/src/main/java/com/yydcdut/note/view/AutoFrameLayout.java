package com.yydcdut.note.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yuyidong on 15-4-29.
 */
public class AutoFrameLayout extends FrameLayout {
    private static final float DURATION = 1000f;
    private View mInstance = this;
    private AutoAnimationImageView mView;
    private int mLastLeft;
    private int mLastTop;
    private int mLastRight;
    private int mLastBottom;
    private int mLastWidth;
    private int mLastHeight;
    private Context mContext;
    private int mMaxAlpha = 136;

    public AutoFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setVisibility(GONE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mView.setBegin(mLastLeft, mLastTop, mLastRight, mLastBottom, mLastWidth, mLastHeight);
    }

    public void setBegin(int left, int top, int right, int bottom, int width, int height, AutoAnimationImageView.OnAutoAnimationListener listener) {
        mView = new AutoAnimationImageView(mContext, listener);
        mView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mView);
        mLastLeft = left;
        mLastTop = top;
        mLastRight = right;
        mLastBottom = bottom;
        mLastWidth = width;
        mLastHeight = height;
        mView.setBegin(left, top, right, bottom, width, height);
    }

    public void setFinish(int left, int top, int right, int bottom, int width, int height) {
        mView.setFinish(left, top, right, bottom, width, height);
//        BgAsyncTask bgAsyncTask = new BgAsyncTask();
//        bgAsyncTask.execute((int) DURATION);
    }

    /**
     * 显示图片，只当有imageloader加载成功之后才显示这个layout
     *
     * @param path
     */
    public void setImagePathAndShow(String path) {
        setVisibility(GONE);
        ImageLoader.getInstance().displayImage(path, mView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }


    class BgAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            for (int i = 1; i <= params[0].intValue(); i++) {
                int currentAlpha = mMaxAlpha * i / params[0].intValue();
                int color = Color.argb(currentAlpha, 0, 0, 0);
                publishProgress(color);
                try {
                    Thread.sleep(0, (int) (DURATION));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mInstance.setBackgroundColor(values[0]);
        }

    }

}
