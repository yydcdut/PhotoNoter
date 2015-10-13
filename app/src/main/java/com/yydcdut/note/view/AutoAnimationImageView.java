package com.yydcdut.note.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15-4-28.
 */
public class AutoAnimationImageView extends ImageView {
    private static final float DURATION = 1000f;

    private float mFinishLeft;
    private float mFinishTop;
    private float mFinishRight;
    private float mFinishBottom;
    private float mFinishWidth;
    private float mFinishHeight;

    private float mChangeLeft;
    private float mChangeTop;
    private float mChangeRight;
    private float mChangeBottom;
    private float mChangeWidth;
    private float mChangeHeight;

    private OnAutoAnimationListener mListener;


    public AutoAnimationImageView(Context context, OnAutoAnimationListener listener) {
        super(context);
        mListener = listener;
    }

    public AutoAnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFinish(int left, int top, int right, int bottom, int width, int height) {
        mFinishLeft = (left);
        mFinishTop = (top);
        mFinishRight = (right);
        mFinishBottom = (bottom);
        mFinishWidth = (width);
        mFinishHeight = (height);
        MoveAsyncTask asyncTask = new MoveAsyncTask();
        asyncTask.execute(Integer.valueOf((int) DURATION));
    }

    public void setBegin(int left, int top, int right, int bottom, int width, int height) {
        mChangeLeft = left;
        mChangeTop = top;
        mChangeRight = right;
        mChangeBottom = bottom;
        mChangeWidth = width;
        mChangeHeight = height;
        layout((int) mChangeLeft, (int) mChangeTop, (int) mChangeRight, (int) mChangeBottom);
        setMeasuredDimension(width, height);
    }


    class MoveAsyncTask extends AsyncTask<Integer, Float, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            for (int i = 1; i <= params[0].intValue(); i++) {
                float left = mChangeLeft + (mFinishLeft - mChangeLeft) * i / params[0].intValue();
                float top = mChangeTop + (mFinishTop - mChangeTop) * i / params[0].intValue();
                float right = mChangeRight + (mFinishRight - mChangeRight) * i / params[0].intValue();
                float bottom = mChangeBottom + (mFinishBottom - mChangeBottom) * i / params[0].intValue();
                float width = mChangeWidth + (mFinishWidth - mChangeWidth) * i / params[0].intValue();
                float height = mChangeHeight + (mFinishHeight - mChangeHeight) * i / params[0].intValue();
                publishProgress(left, top, right, bottom, width, height);
                try {
                    Thread.sleep(0, (int) (DURATION));//因为上面的算法会有一定的延时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            layout(values[0].intValue(), values[1].intValue(), values[2].intValue(), values[3].intValue());
            setMeasuredDimension(values[4].intValue(), values[5].intValue());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mListener != null) {
                mListener.onFinish();
            }
        }
    }


    public interface OnAutoAnimationListener {
        public void onFinish();
    }

}
