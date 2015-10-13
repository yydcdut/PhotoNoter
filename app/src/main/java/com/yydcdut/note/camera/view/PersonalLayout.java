package com.yydcdut.note.camera.view;

import android.content.Context;
import android.content.res.TypedArray;
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
class PersonalLayout extends FrameLayout implements View.OnClickListener {

    private int mBeforeDegree = -1;

    private OnLayoutItemClickListener mOnLayoutItemClickListener;

    private TypedArray mTimerIcons;
    private int mCurrentTimerIndex = 0;
    private int[] mTimerArray;

    private ImageView m11RatioView;
    private ImageView m43RatioView;
    private ImageView mFullRatioView;
    private ImageView mTimerView;


    public PersonalLayout(Context context) {
        this(context, null);
    }

    public PersonalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_personal, this, true);
        mTimerIcons = context.getResources().obtainTypedArray(R.array.camera_timer);
        mTimerArray = new int[]{Const.LAYOUT_PERSONAL_TIMER_0, Const.LAYOUT_PERSONAL_TIMER_3,
                Const.LAYOUT_PERSONAL_TIMER_5, Const.LAYOUT_PERSONAL_TIMER_10, Const.LAYOUT_PERSONAL_TIMER_15};
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.btn_ratio_1_1).setOnClickListener(this);
        findViewById(R.id.btn_ratio_4_3).setOnClickListener(this);
        findViewById(R.id.btn_ratio_full).setOnClickListener(this);
        findViewById(R.id.btn_timer).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        m11RatioView = (ImageView) findViewById(R.id.img_ratio_1_1);
        m43RatioView = (ImageView) findViewById(R.id.img_ratio_4_3);
        mFullRatioView = (ImageView) findViewById(R.id.img_ratio_full);
        mTimerView = (ImageView) findViewById(R.id.img_timer);
    }

    public void setOnLayoutItemClickListener(OnLayoutItemClickListener onLayoutItemClickListener) {
        mOnLayoutItemClickListener = onLayoutItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnLayoutItemClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_ratio_1_1:
                    clearRatio();
                    m11RatioView.setImageResource(R.drawable.ic_camera_ratio_1_1_checked);
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_PERSONAL_RATIO_1_1);
                    break;
                case R.id.btn_ratio_4_3:
                    clearRatio();
                    m43RatioView.setImageResource(R.drawable.ic_camera_ratio_4_3_checked);
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_PERSONAL_RATIO_4_3);
                    break;
                case R.id.btn_ratio_full:
                    clearRatio();
                    mFullRatioView.setImageResource(R.drawable.ic_camera_ratio_full_checked);
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_PERSONAL_RATIO_FULL);
                    break;
                case R.id.btn_timer:
                    mCurrentTimerIndex++;
                    setTimerImage();
                    mOnLayoutItemClickListener.onClick(v, mTimerArray[mCurrentTimerIndex % mTimerIcons.length()]);
                    break;
                case R.id.btn_down:
                    mOnLayoutItemClickListener.onClick(v, MenuLayout.LAYOUT_DOWN);
                    break;
            }
        }
    }

    private void clearRatio() {
        m11RatioView.setImageResource(R.drawable.ic_camera_ratio_1_1_unchecked);
        m43RatioView.setImageResource(R.drawable.ic_camera_ratio_4_3_unchecked);
        mFullRatioView.setImageResource(R.drawable.ic_camera_ratio_full_unchecked);
    }

    private void setTimerImage() {
        mTimerView.setImageDrawable(mTimerIcons.getDrawable(mCurrentTimerIndex % mTimerIcons.length()));
    }

    public void reset() {
        clearRatio();
        m43RatioView.setImageResource(R.drawable.ic_camera_ratio_4_3_checked);
        mCurrentTimerIndex = 0;
        mTimerView.setImageResource(R.drawable.ic_delay_0);
    }

    public void setSizeUI(int current) {
        clearRatio();
        switch (current) {
            case Const.LAYOUT_PERSONAL_RATIO_1_1:
                m11RatioView.setImageResource(R.drawable.ic_camera_ratio_1_1_checked);
                break;
            case Const.LAYOUT_PERSONAL_RATIO_4_3:
                m43RatioView.setImageResource(R.drawable.ic_camera_ratio_4_3_checked);
                break;
            case Const.LAYOUT_PERSONAL_RATIO_FULL:
                mFullRatioView.setImageResource(R.drawable.ic_camera_ratio_full_checked);
                break;
        }
    }

    public void setTimerUI(int current) {
        mCurrentTimerIndex = current;
    }

    public void onSensorRotationEvent(int degree) {
        if (mBeforeDegree == -1) {
            mBeforeDegree = degree;
        }
        new RotationAsyncTask(m11RatioView, m43RatioView, mFullRatioView, mTimerView).execute(mBeforeDegree, degree);
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
