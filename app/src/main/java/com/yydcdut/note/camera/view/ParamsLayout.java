package com.yydcdut.note.camera.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.view.callback.OnLayoutItemClickListener;


/**
 * Created by yuyidong on 15/8/19.
 */
class ParamsLayout extends FrameLayout implements View.OnClickListener {
    private static final int DIRECTION_LEFT = -1;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_NOTHING = 0;
    private int mDirection = DIRECTION_NOTHING;

    private int mCurrentSensorDegree = 0;
    private int mCurrentViewDegree = 0;

    private int[] mCameraIcons;
    private int mCurrentCameraIndex = 0;
    private int[] mCameraArray;
    private ImageView mCameraImage;

    private int[] mFlashIcons;
    private int mCurrentFlashIndex = 0;
    private int[] mFlashArray;
    private ImageView mFlashImage;

    private int[] mLocationIcons;
    private int mCurrentLocationIndex = 0;
    private int[] mLocationArray;
    private ImageView mLocationImage;

    private int[] mWBIcons;
    private int mCurrentWBIndex = 0;
    private int[] mWBArray;
    private ImageView mWBImage;

    private int[] mGridIcons;
    private int mCurrentGridIndex = 0;
    private int[] mGridArray;
    private ImageView mGridImage;

    private int[] mTorchIcons;
    private int mCurrentTorchIndex = 0;
    private int[] mTorchArray;
    private ImageView mTorchImage;

    private int[] mSoundIcons;
    private int mCurrentSoundIndex = 0;
    private int[] mSoundArray;
    private ImageView mSoundImage;

    private TextView mZoomText;

    private OnLayoutItemClickListener mOnLayoutItemClickListener;

    private Handler mMainHandler;

    private boolean mWannaFinishThread = false;

    public ParamsLayout(Context context) {
        this(context, null);
    }

    public ParamsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParamsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_params, this, true);
        mMainHandler = new Handler();
        new Thread(mThreadRunnable).start();
    }

    public void setSupportedCameraIds(int[] icons, int[] array, int current) {
        mCurrentCameraIndex = current;
        mCameraIcons = icons;
        mCameraArray = array;
        mCameraImage.setImageResource(mCameraIcons[mCurrentCameraIndex % mCameraIcons.length]);
    }

    public void setSupportedFlash(int[] icons, int[] array, int current) {
        mCurrentFlashIndex = current;
        mFlashIcons = icons;
        mFlashArray = array;
        mFlashImage.setImageResource(mFlashIcons[mCurrentFlashIndex % mFlashIcons.length]);
    }

    public void setSupportedLocation(int[] icons, int[] array, int current) {
        mCurrentLocationIndex = current;
        mLocationIcons = icons;
        mLocationArray = array;
        mLocationImage.setImageResource(mLocationIcons[mCurrentLocationIndex % mLocationIcons.length]);
    }

    public void setSupportedWB(int[] icons, int[] array, int current) {
        mCurrentWBIndex = current;
        mWBIcons = icons;
        mWBArray = array;
        mWBImage.setImageResource(mWBIcons[mCurrentWBIndex % mWBIcons.length]);
    }

    public void setSupportedGrid(int[] icons, int[] array, int current) {
        mCurrentGridIndex = current;
        mGridIcons = icons;
        mGridArray = array;
        mGridImage.setImageResource(mGridIcons[mCurrentGridIndex % mGridIcons.length]);
    }

    public void setSupportedTorch(int[] icons, int[] array, int current) {
        mCurrentTorchIndex = current;
        mTorchIcons = icons;
        mTorchArray = array;
        mTorchImage.setImageResource(mTorchIcons[mCurrentTorchIndex % mTorchIcons.length]);
    }

    public void setSupportedSound(int[] icons, int[] array, int current) {
        mCurrentSoundIndex = current;
        mSoundIcons = icons;
        mSoundArray = array;
        mSoundImage.setImageResource(mSoundIcons[mCurrentSoundIndex % mSoundIcons.length]);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.btn_down).setOnClickListener(this);

        mCameraImage = (ImageView) findViewById(R.id.img_camera_id);
        findViewById(R.id.btn_camera_id).setOnClickListener(this);

        mFlashImage = (ImageView) findViewById(R.id.img_flash);
        findViewById(R.id.btn_flash).setOnClickListener(this);

        mLocationImage = (ImageView) findViewById(R.id.img_location);
        findViewById(R.id.btn_location).setOnClickListener(this);

        mWBImage = (ImageView) findViewById(R.id.img_wb);
        findViewById(R.id.btn_wb).setOnClickListener(this);

        mGridImage = (ImageView) findViewById(R.id.img_grid);
        findViewById(R.id.btn_grid).setOnClickListener(this);

        mTorchImage = (ImageView) findViewById(R.id.img_torch);
        findViewById(R.id.btn_torch).setOnClickListener(this);

        mSoundImage = (ImageView) findViewById(R.id.img_sound);
        findViewById(R.id.btn_sound).setOnClickListener(this);

        mZoomText = (TextView) findViewById(R.id.txt_zoom);
    }

    public void setOnLayoutItemClickListener(OnLayoutItemClickListener onLayoutItemClickListener) {
        mOnLayoutItemClickListener = onLayoutItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnLayoutItemClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_down:
                    mOnLayoutItemClickListener.onClick(v, MenuLayout.LAYOUT_DOWN);
                    break;
                case R.id.btn_camera_id:
                    mCurrentCameraIndex++;
                    mCameraImage.setImageResource(mCameraIcons[mCurrentCameraIndex % mCameraIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mCameraArray[mCurrentCameraIndex % mCameraArray.length]);
                    break;
                case R.id.btn_flash:
                    mCurrentFlashIndex++;
                    mFlashImage.setImageResource(mFlashIcons[mCurrentFlashIndex % mFlashIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mFlashArray[mCurrentFlashIndex % mFlashIcons.length]);
                    break;
                case R.id.btn_location:
                    mCurrentLocationIndex++;
                    mLocationImage.setImageResource(mLocationIcons[mCurrentLocationIndex % mLocationIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mLocationArray[mCurrentLocationIndex % mLocationIcons.length]);
                    break;
                case R.id.btn_wb:
                    mCurrentWBIndex++;
                    mWBImage.setImageResource(mWBIcons[mCurrentWBIndex % mWBIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mWBArray[mCurrentWBIndex % mWBIcons.length]);
                    break;
                case R.id.btn_grid:
                    mCurrentGridIndex++;
                    mGridImage.setImageResource(mGridIcons[mCurrentGridIndex % mGridIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mGridArray[mCurrentGridIndex % mGridIcons.length]);
                    break;
                case R.id.btn_torch:
                    mCurrentTorchIndex++;
                    mTorchImage.setImageResource(mTorchIcons[mCurrentTorchIndex % mTorchIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mTorchArray[mCurrentTorchIndex % mTorchIcons.length]);
                    break;
                case R.id.btn_sound:
                    mCurrentSoundIndex++;
                    mSoundImage.setImageResource(mSoundIcons[mCurrentSoundIndex % mSoundIcons.length]);
                    mOnLayoutItemClickListener.onClick(v, mSoundArray[mCurrentSoundIndex % mSoundIcons.length]);
                    break;

            }
        }
    }

    public void setZoomText(String zoom) {
        mZoomText.setText(zoom + "%");
    }

    public void resetFlashAndTorch(int flashIcon, int torchIcon) {
        for (int i = 0; i < mFlashIcons.length; i++) {
            if (flashIcon == mFlashIcons[i]) {
                mCurrentFlashIndex = i;
            }
        }
        for (int i = 0; i < mTorchIcons.length; i++) {
            if (torchIcon == mTorchIcons[i]) {
                mCurrentTorchIndex = i;
            }
        }
        mFlashImage.setImageResource(mFlashIcons[mCurrentFlashIndex % mFlashIcons.length]);
        mTorchImage.setImageResource(mTorchIcons[mCurrentTorchIndex % mTorchIcons.length]);
    }

    public void onSensorRotationEvent(int degree) {
        mCurrentSensorDegree = degree;
    }

    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            mCameraImage.setRotation(mCurrentViewDegree % 360);
            mFlashImage.setRotation(mCurrentViewDegree % 360);
            mLocationImage.setRotation(mCurrentViewDegree % 360);
            mWBImage.setRotation(mCurrentViewDegree % 360);
            mGridImage.setRotation(mCurrentViewDegree % 360);
            mTorchImage.setRotation(mCurrentViewDegree % 360);
            mSoundImage.setRotation(mCurrentViewDegree % 360);
            mZoomText.setRotation(mCurrentViewDegree % 360);
            findViewById(R.id.img_zoom).setRotation(mCurrentViewDegree % 360);
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
