package com.yydcdut.note.camera.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.view.callback.OnLayoutItemClickListener;
import com.yydcdut.note.utils.Const;


/**
 * Created by yuyidong on 15/8/19.
 */
class MainLayout extends FrameLayout implements View.OnClickListener {
    public static final int LAYOUT_MAIN_PERSONAL = 100;
    public static final int LAYOUT_MAIN_PARAMS = 102;

    private ImageView mCaptureImage;
    private ImageView mPersonalImage;
    private ImageView mParamImage;

    private int mBeforeDegree = -1;


    private OnLayoutItemClickListener mOnLayoutItemClickListener;

    public MainLayout(Context context) {
        this(context, null);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_main, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.btn_personal).setOnClickListener(this);
        findViewById(R.id.btn_capture).setOnClickListener(this);
        findViewById(R.id.btn_params).setOnClickListener(this);
        mCaptureImage = (ImageView) findViewById(R.id.img_capture);
        mParamImage = (ImageView) findViewById(R.id.img_params);
        mPersonalImage = (ImageView) findViewById(R.id.img_personal);
    }

    public void setOnLayoutItemClickListener(OnLayoutItemClickListener onLayoutItemClickListener) {
        mOnLayoutItemClickListener = onLayoutItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnLayoutItemClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_personal:
                    mOnLayoutItemClickListener.onClick(v, LAYOUT_MAIN_PERSONAL);
                    break;
                case R.id.btn_capture:
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_MAIN_CAPTURE);
                    break;
                case R.id.btn_params:
                    mOnLayoutItemClickListener.onClick(v, LAYOUT_MAIN_PARAMS);
                    break;
            }
        }
    }

    public void onSensorRotationEvent(int degree) {
        if (mBeforeDegree == -1) {
            mBeforeDegree = degree;
        }
        new RotationAsyncTask(mCaptureImage, mParamImage, mPersonalImage).execute(mBeforeDegree, degree);
        mBeforeDegree = degree;
    }

    class RotationAsyncTask extends AsyncTask<Integer, Integer, Void> {
        private View[] mViews;

        public RotationAsyncTask(View... views) {
            mViews = views;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            for (View view : mViews) {
                view.setRotation(values[0] % 360);
            }
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if (params[0] == 0 && params[1] == 270) {
                params[0] = 360;
            }
            if (params[0] == 270 && params[1] == 0) {
                params[1] = 360;
            }
            if (params[0] == params[1]) {
                publishProgress(params);
            }
            while (params[0] < params[1]) {
                params[0]++;
                publishProgress(params);
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (params[0] > params[1]) {
                params[0]--;
                publishProgress(params);
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


}
