package com.yydcdut.note.camera.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.view.callback.OnLayoutItemClickListener;
import com.yydcdut.note.utils.Const;


/**
 * Created by yuyidong on 15/8/19.
 */
class MainLayout extends FrameLayout implements View.OnClickListener {
    public static final int LAYOUT_MAIN_PERSONAL = 100;
    public static final int LAYOUT_MAIN_PARAMS = 102;

    private static final int DIRECTION_LEFT = -1;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_NOTHING = 0;
    private int mDirection = DIRECTION_NOTHING;

    private int mCurrentSensorDegree = 0;
    private int mCurrentViewDegree = 0;

    private View mCaptureImage;
    private View mPersonalImage;
    private View mParamImage;

    private Handler mMainHandler;

    private boolean mWannaFinishThread = false;

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
        mMainHandler = new Handler();
        new Thread(mThreadRunnable).start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.btn_personal).setOnClickListener(this);
        findViewById(R.id.btn_params).setOnClickListener(this);
        mCaptureImage = findViewById(R.id.img_capture);
        mParamImage = findViewById(R.id.img_params);
        mPersonalImage = findViewById(R.id.img_personal);
        mCaptureImage.setOnClickListener(this);
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
                case R.id.btn_params:
                    mOnLayoutItemClickListener.onClick(v, LAYOUT_MAIN_PARAMS);
                    break;
                case R.id.img_capture:
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_MAIN_CAPTURE);
                    break;
            }
        }
    }

    public void onSensorRotationEvent(int degree) {
        mCurrentSensorDegree = degree;
    }

    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            mCaptureImage.setRotation(mCurrentViewDegree % 360);
            mParamImage.setRotation(mCurrentViewDegree % 360);
            mPersonalImage.setRotation(mCurrentViewDegree % 360);
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
