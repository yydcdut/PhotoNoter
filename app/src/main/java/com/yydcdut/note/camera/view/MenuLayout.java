package com.yydcdut.note.camera.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.camera.view.callback.OnLayoutItemClickListener;
import com.yydcdut.note.utils.Const;

/**
 * Created by yuyidong on 15/8/19.
 */
public class MenuLayout extends RelativeLayout implements OnLayoutItemClickListener {
    private static final int LAYOUT_PARAMS = 4399;
    private static final int LAYOUT_PERSONAL = 4400;
    private static final int LAYOUT_NOTHING = -101;

    private int mWhich = LAYOUT_NOTHING;

    private OnLayoutItemClickListener mOnLayoutItemClickListener;

    public static final int LAYOUT_DOWN = -100;

    private MainLayout mMainLayout;
    private ParamsLayout mParamsLayout;
    private PersonalLayout mPersonalLayout;
    private View mCoverView;

    public MenuLayout(Context context) {
        super(context);
    }

    public MenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMainLayout = (MainLayout) findViewById(R.id.layout_camera_main);
        mMainLayout.setOnLayoutItemClickListener(this);
        mParamsLayout = (ParamsLayout) findViewById(R.id.layout_camera_params);
        mParamsLayout.setVisibility(GONE);
        mParamsLayout.setOnLayoutItemClickListener(this);
        mPersonalLayout = (PersonalLayout) findViewById(R.id.layout_camera_personal);
        mPersonalLayout.setOnLayoutItemClickListener(this);
        mPersonalLayout.setVisibility(GONE);
        mCoverView = findViewById(R.id.view_cover);
    }

    public void setOnLayoutItemClickListener(OnLayoutItemClickListener onLayoutItemClickListener) {
        mOnLayoutItemClickListener = onLayoutItemClickListener;
    }

    public void dispatchRotationEvent(int degree) {
        mMainLayout.onSensorRotationEvent(degree);
        mPersonalLayout.onSensorRotationEvent(degree);
        mParamsLayout.onSensorRotationEvent(degree);
    }


    @Override
    public void onClick(View v, int item) {
        switch (item) {
            case MainLayout.LAYOUT_MAIN_PARAMS:
                mWhich = LAYOUT_PARAMS;
                mParamsLayout.setVisibility(VISIBLE);
                mParamsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_in));
                mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
                mMainLayout.setVisibility(GONE);
                break;
            case MainLayout.LAYOUT_MAIN_PERSONAL:
                mWhich = LAYOUT_PERSONAL;
                mPersonalLayout.setVisibility(VISIBLE);
                mPersonalLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_in));
                mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
                mMainLayout.setVisibility(GONE);
                break;
            case MenuLayout.LAYOUT_DOWN:
                switch (mWhich) {
                    case LAYOUT_PARAMS:
                        mParamsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_out));
                        mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
                        mParamsLayout.setVisibility(GONE);
                        break;
                    case LAYOUT_PERSONAL:
                        mPersonalLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_out));
                        mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
                        mPersonalLayout.setVisibility(GONE);
                        break;
                }
                mWhich = LAYOUT_NOTHING;
                mMainLayout.setVisibility(VISIBLE);
                break;
            case Const.LAYOUT_PERSONAL_RATIO_1_1:
                //先转为4：3，然后再cover上去
                if (mOnLayoutItemClickListener != null) {
                    mOnLayoutItemClickListener.onClick(v, Const.LAYOUT_PERSONAL_RATIO_4_3);
                }
                mCoverView.setVisibility(VISIBLE);
                if (mOnLayoutItemClickListener != null) {
                    mOnLayoutItemClickListener.onClick(v, item);
                }
                break;
            default:
                if (mOnLayoutItemClickListener != null) {
                    mOnLayoutItemClickListener.onClick(v, item);
                }
                break;
        }
    }

    public void setRatio11() {
        mCoverView.setVisibility(VISIBLE);
        setBg(getResources().getColor(android.R.color.white));
    }

    public void setRatio43() {
        mCoverView.setVisibility(GONE);
        setBg(getResources().getColor(android.R.color.white));
    }

    public void setRatioFull() {
        mCoverView.setVisibility(GONE);
        setBg(getResources().getColor(android.R.color.transparent));
    }


    private void setBg(int color) {
        mParamsLayout.findViewById(R.id.layout_params).setBackgroundColor(color);
        mMainLayout.findViewById(R.id.layout_main).setBackgroundColor(color);
        mPersonalLayout.findViewById(R.id.layout_personal).setBackgroundColor(color);
    }

    public void setCaptureImage(Drawable drawable) {
        ((ImageView) mMainLayout.findViewById(R.id.img_capture)).setImageDrawable(drawable);
    }

    public void resetPersonal() {
        mPersonalLayout.reset();
    }

    public void setSupportedCameraIds(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedCameraIds(icons, array, current);
    }

    public void setSupportedFlash(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedFlash(icons, array, current);
    }

    public void setSupportedLocation(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedLocation(icons, array, current);
    }

    public void setSupportedWB(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedWB(icons, array, current);
    }

    public void setSupportedGrid(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedGrid(icons, array, current);
    }

    public void setSupportedTorch(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedTorch(icons, array, current);
    }

    public void setSupportedSound(int[] icons, int[] array, int current) {
        if (icons != null && array != null && icons.length != array.length) {
            throw new IllegalArgumentException("必须得相同");
        }
        mParamsLayout.setSupportedSound(icons, array, current);
    }

    public void setSizeUI(int current) {
        mPersonalLayout.setSizeUI(current);
    }

    public void setTimerUI(int current) {
        mPersonalLayout.setTimerUI(current);
    }

    public void setZoomText(String zoom) {
        mParamsLayout.setZoomText(zoom);
    }

    public void resetFlashAndTorch(int flashIcon, int torchIcon) {
        mParamsLayout.resetFlashAndTorch(flashIcon, torchIcon);
    }

    public boolean isOtherLayoutShowing() {
        return mWhich != LAYOUT_NOTHING;
    }

    public void closeOtherLayout() {
        switch (mWhich) {
            case LAYOUT_PARAMS:
                mParamsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_out));
                mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
                mParamsLayout.setVisibility(GONE);
                break;
            case LAYOUT_PERSONAL:
                mPersonalLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_out));
                mMainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
                mPersonalLayout.setVisibility(GONE);
                break;
        }
        mWhich = LAYOUT_NOTHING;
        mMainLayout.setVisibility(VISIBLE);
    }

}
