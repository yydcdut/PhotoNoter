package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 16/2/8.
 */
public class CameraTopView extends LinearLayout implements View.OnClickListener,
        AnimationTopLayout.OnAnimationHalfFinishListener {
    private AnimationTopLayout[] mItemLayoutView;
    private ImageView[] mItemImageView;

    private int[] mFlashDrawableRes;
    private int mFlashState;

    private int[] mTimerDrawableRes;
    private int mTimerState;

    private int[] mGridDrawableRes;
    private int mGridState;

    private int[] mCameraIdDrawableRes;
    private int mCameraIdState;

    public CameraTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_camera_top, this, true);
        mItemLayoutView = new AnimationTopLayout[5];
        mItemImageView = new ImageView[5];
    }

    /**
     * 初始化Timer，grid的Drawable
     */
    private void initSomeState() {
        mTimerDrawableRes = new int[]{
                R.drawable.ic_timer_off_white_24dp,
                R.drawable.ic_timer_3_white_24dp,
                R.drawable.ic_timer_10_white_24dp};
        mGridDrawableRes = new int[]{
                R.drawable.ic_grid_off_white_24dp,
                R.drawable.ic_grid_on_white_24dp};
        mItemLayoutView[2].setOnAnimationHalfFinishListener(this);
        mItemLayoutView[3].setOnAnimationHalfFinishListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mItemLayoutView[0] = (AnimationTopLayout) findViewById(R.id.layout_back);
        mItemLayoutView[1] = (AnimationTopLayout) findViewById(R.id.layout_flash);
        mItemLayoutView[2] = (AnimationTopLayout) findViewById(R.id.layout_timer);
        mItemLayoutView[3] = (AnimationTopLayout) findViewById(R.id.layout_grid);
        mItemLayoutView[4] = (AnimationTopLayout) findViewById(R.id.layout_camera_id);
        mItemImageView[0] = (ImageView) findViewById(R.id.btn_back);
        mItemImageView[1] = (ImageView) findViewById(R.id.btn_flash);
        mItemImageView[2] = (ImageView) findViewById(R.id.btn_timer);
        mItemImageView[3] = (ImageView) findViewById(R.id.btn_grid);
        mItemImageView[4] = (ImageView) findViewById(R.id.btn_camera_id);
        for (View view : mItemImageView) {
            view.setOnClickListener(this);
        }
        initSomeState();
    }

    /**
     * 适配5.0之后statusBar那部分
     *
     * @param margin
     */
    public void setItemsMarginTop(int margin) {
        for (View v : mItemLayoutView) {
            LinearLayout.LayoutParams l = (LayoutParams) v.getLayoutParams();
            l.topMargin = margin;
        }
    }

    public void initItemState(int currentFlash, int[] flashDrawableRes,
                              int currentTimer,
                              int currentGrid,
                              int currentCameraId, int[] cameraIdDrawableRes) {
        mFlashState = currentFlash;
        mFlashDrawableRes = flashDrawableRes;
        mTimerState = currentTimer;
        mGridState = currentGrid;
        mCameraIdDrawableRes = cameraIdDrawableRes;
        mCameraIdState = currentCameraId;
        if (mFlashDrawableRes.length > 1) {
            mItemLayoutView[1].setOnAnimationHalfFinishListener(this);
        }
        if (mCameraIdDrawableRes.length > 1) {
            mItemLayoutView[4].setOnAnimationHalfFinishListener(this);
        }
        mItemImageView[1].setImageResource(mFlashDrawableRes[mFlashState % mFlashDrawableRes.length]);
        mItemImageView[2].setImageResource(mTimerDrawableRes[mTimerState % mTimerDrawableRes.length]);
        mItemImageView[3].setImageResource(mGridDrawableRes[mGridState % mGridDrawableRes.length]);
        mItemImageView[4].setImageResource(mCameraIdDrawableRes[mCameraIdState % mCameraIdDrawableRes.length]);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onBackClick(v);
                }
                break;
            case R.id.btn_flash:
                if (mFlashDrawableRes.length > 1) {
                    mItemLayoutView[1].doAnimation();
                }
                break;
            case R.id.btn_timer:
                mItemLayoutView[2].doAnimation();
                break;
            case R.id.btn_grid:
                mItemLayoutView[3].doAnimation();
                break;
            case R.id.btn_camera_id:
                if (mCameraIdDrawableRes.length > 1) {
                    mItemLayoutView[4].doAnimation();
                }
                break;
        }
    }

    @Override
    public void onHalf(View v) {
        switch (v.getId()) {
            case R.id.btn_flash:
                mFlashState++;
                mItemImageView[1].setImageResource(mFlashDrawableRes[mFlashState % mFlashDrawableRes.length]);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onFlashClick(v);
                }
                break;
            case R.id.btn_timer:
                mTimerState++;
                mItemImageView[2].setImageResource(mTimerDrawableRes[mTimerState % mTimerDrawableRes.length]);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onTimerClick(v);
                }
                break;
            case R.id.btn_grid:
                mGridState++;
                mItemImageView[3].setImageResource(mGridDrawableRes[mGridState % mGridDrawableRes.length]);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onGridClick(v);
                }
                break;
            case R.id.btn_camera_id:
                mCameraIdState++;
                mItemImageView[4].setImageResource(mCameraIdDrawableRes[mCameraIdState % mCameraIdDrawableRes.length]);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onCameraIdClick(v);
                }
                break;
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onBackClick(View view);

        void onFlashClick(View view);

        void onTimerClick(View view);

        void onGridClick(View view);

        void onCameraIdClick(View view);
    }
}
