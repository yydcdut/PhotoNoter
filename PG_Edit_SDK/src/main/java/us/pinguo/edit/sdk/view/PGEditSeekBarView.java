package us.pinguo.edit.sdk.view;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.view.IPGEditSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditSeekBarViewListener;
import us.pinguo.edit.sdk.base.widget.AnimationAdapter;
import us.pinguo.edit.sdk.widget.PGEditSeekBar;

public class PGEditSeekBarView implements View.OnClickListener, IPGEditSeekBarView {

    private PGEditSeekbarLayout mSeekLayout;
    private View mCancelBtn;
    private View mConfirmBtn;
    private PGEditSeekBar mSeekBar;
    private IPGEditSeekBarViewListener mListener;
    private float mLastAlphaRate = 1f;

    public void initView(Activity activity) {
        mSeekLayout = (PGEditSeekbarLayout) activity.findViewById(R.id.seekbar_layout);
        mSeekBar = (PGEditSeekBar) mSeekLayout.findViewById(R.id.seek_bar);

        mCancelBtn = mSeekLayout.findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(this);

        mConfirmBtn = mSeekLayout.findViewById(R.id.confirm);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (null == mListener) {
            return;
        }

        if (mCancelBtn == v) {
            mListener.onCancelBtnClick();
            return;
        }

        if (mConfirmBtn == v) {
            mListener.onConfirmBtnClick();
            return;
        }
    }

    public void setListener(IPGEditSeekBarViewListener listener) {
        mListener = listener;
    }

    public void showValueSeekLayout(ImageView imageView, int min, int max, int def, float step, float value) {

        mSeekLayout.setVisibility(View.VISIBLE);
        mSeekLayout.showWithAnimation();

        mSeekBar.setOnSeekChangeListener(null);
        mSeekBar.reset();
        mSeekBar.setSeekLength(min, max, def, step);
        mSeekBar.setValue(value);
        if (null != imageView) {
            setAlphaForImageView(imageView, value / 100f);
        }
        mSeekBar.setOnSeekChangeListener(mOnSeekChangeListener);
    }

    public void hideWithAnimation() {
        mSeekLayout.hideWithAnimation(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mSeekLayout.setVisibility(View.GONE);
            }
        });
    }

    private PGEditSeekBar.OnSeekChangeListener mOnSeekChangeListener = new PGEditSeekBar.OnSeekChangeListener() {

        @Override
        public void onSeekChanged(float currentValue, float step) {
            if (null != mListener) {
                mListener.onSeekValueChanged(currentValue, step);
            }
        }

        @Override
        public void onSeekStopped(float currentValue, float step) {

        }
    };

    public void setAlphaForImageView(ImageView imageView, float rate) {
        mLastAlphaRate = rate;
        //X10i的机子在更改drawable的方法上会出现渲染问题，所以在X10i 这个机子上使用透明动画更改的方式
        if ("X10i".equals(Build.MODEL)) {
            AlphaAnimation animation = new AlphaAnimation(0f, rate);
            animation.setDuration(0);
            animation.setFillAfter(true);
            animation.setFillBefore(false);
            imageView.startAnimation(animation);

        } else {
            int alpha = (int) (rate * 255);
            imageView.getDrawable().setAlpha(alpha);
        }
    }

    public void confirm() {
        onClick(mConfirmBtn);
    }

    public void cancel() {
        onClick(mCancelBtn);
    }

    public boolean isSeekBarVisible() {
        return mSeekLayout.getVisibility() == View.VISIBLE;
    }
}
