package com.yydcdut.note.camera.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
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
    private static final int DIRECTION_LEFT = -1;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_NOTHING = 0;
    private int mDirection = DIRECTION_NOTHING;

    private int mCurrentSensorDegree = 0;
    private int mCurrentViewDegree = 0;

    private OnLayoutItemClickListener mOnLayoutItemClickListener;

    private TypedArray mTimerIcons;
    private int mCurrentTimerIndex = 0;
    private int[] mTimerArray;

    private ImageView m11RatioView;
    private ImageView m43RatioView;
    private ImageView mFullRatioView;
    private ImageView mTimerView;

    private Handler mMainHandler;

    private boolean mWannaFinishThread = false;

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
        mMainHandler = new Handler();
        new Thread(mThreadRunnable).start();
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
        mCurrentSensorDegree = degree;
    }

    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            m11RatioView.setRotation(mCurrentViewDegree % 360);
            m43RatioView.setRotation(mCurrentViewDegree % 360);
            mFullRatioView.setRotation(mCurrentViewDegree % 360);
            mTimerView.setRotation(mCurrentViewDegree % 360);
        }
    };

    private Runnable mThreadRunnable = new Runnable() {
        @Override
        public void run() {
            while (!mWannaFinishThread) {
                if (mDirection == DIRECTION_NOTHING) {
                    if ((mCurrentViewDegree < 5 || mCurrentViewDegree > 355) && mCurrentSensorDegree == 270) {
                        mDirection = DIRECTION_RIGHT;
                    } else if ((mCurrentViewDegree > 265 && mCurrentViewDegree < 275) && mCurrentSensorDegree == 0) {
                        mDirection = DIRECTION_LEFT;
                    } else if (mCurrentViewDegree < mCurrentSensorDegree) {
                        mDirection = DIRECTION_LEFT;
                    } else if (mCurrentViewDegree > mCurrentSensorDegree) {
                        mDirection = DIRECTION_RIGHT;
                    }
                }
                switch (mDirection) {
                    case DIRECTION_LEFT:
                        mCurrentViewDegree++;
                        break;
                    case DIRECTION_RIGHT:
                        mCurrentViewDegree--;
                        break;
                    case DIRECTION_NOTHING:
                    default:
                        break;
                }
                if (mCurrentViewDegree < 0) {
                    mCurrentViewDegree = 359;
                } else if (mCurrentViewDegree > 360) {
                    mCurrentViewDegree = 0;
                }
                if (mCurrentSensorDegree == mCurrentViewDegree) {
                    mDirection = DIRECTION_NOTHING;
                }
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMainHandler.post(mMainRunnable);
            }

        }
    };

    @Override
    protected void onDetachedFromWindow() {
        mWannaFinishThread = true;
        super.onDetachedFromWindow();
    }
}
