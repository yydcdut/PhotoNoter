package us.pinguo.edit.sdk.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.view.IPGEditThreeSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditThreeSeekBarViewListener;
import us.pinguo.edit.sdk.base.widget.AnimationAdapter;
import us.pinguo.edit.sdk.widget.PGEditSeekBar;

public class PGEditThreeSeekBarView implements View.OnClickListener, IPGEditThreeSeekBarView {

    private PGEditThreeSeekbarLayout mThreeSeekbarLayout;
    private View mCancelBtn;
    private View mConfirmBtn;
    private PGEditSeekBar mSeekBar;
    private IPGEditThreeSeekBarViewListener mListener;
    private String[] mTextNameArray;

    public void initView(Activity activity) {
        mThreeSeekbarLayout = (PGEditThreeSeekbarLayout) activity.findViewById(R.id.three_seekbar_layout);

        mCancelBtn = mThreeSeekbarLayout.findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(this);

        mConfirmBtn = mThreeSeekbarLayout.findViewById(R.id.confirm);
        mConfirmBtn.setOnClickListener(this);

        mSeekBar = (PGEditSeekBar) mThreeSeekbarLayout.findViewById(R.id.seek_bar);

        mTextNameArray = getTextNameArray(activity);
        mThreeSeekbarLayout.setTextName(mTextNameArray[0], mTextNameArray[1], mTextNameArray[2]);

        mThreeSeekbarLayout.setTextClickListener(mTextOnClickListener, mTextNameArray[0], mTextNameArray[1], mTextNameArray[2]);
    }

    public String[] getTextNameArray(Context context) {
        return new String[3];
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

    public void setListener(IPGEditThreeSeekBarViewListener listener) {
        mListener = listener;
    }

    public void confirm() {
        onClick(mConfirmBtn);
    }

    public void cancel() {
        onClick(mCancelBtn);
    }

    public void initFirstSeekBar(int min, int max, int def, float step, float value) {
        mSeekBar.setOnSeekChangeListener(null);
        mSeekBar.reset();
        mSeekBar.setSeekLength(min, max, def, step);
        mSeekBar.setValue(value);
        mSeekBar.setOnSeekChangeListener(mOnFirstSeekChangeListener);
    }

    public void initSecondSeekBar(int min, int max, int def, float step, float value) {
        mSeekBar.setOnSeekChangeListener(null);
        mSeekBar.reset();
        mSeekBar.setSeekLength(min, max, def, step);
        mSeekBar.setValue(value);
        mSeekBar.setOnSeekChangeListener(mOnSecondSeekChangeListener);
    }

    public void initThirdSeekBar(int min, int max, int def, float step, float value) {
        mSeekBar.setOnSeekChangeListener(null);
        mSeekBar.reset();
        mSeekBar.setSeekLength(min, max, def, step);
        mSeekBar.setValue(value);
        mSeekBar.setOnSeekChangeListener(mOnThirdSeekChangeListener);
    }

    public void hideWithAnimation() {

        mThreeSeekbarLayout.hideWithAnimation(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mThreeSeekbarLayout.setVisibility(View.GONE);
            }
        });
    }

    public void showSeekLayout() {
        mThreeSeekbarLayout.setVisibility(View.VISIBLE);
        mThreeSeekbarLayout.showWithAnimation();
    }

    public void selectFirstText() {
        mThreeSeekbarLayout.selectedFirstText();
    }

    public void setLineColor(String backgroundColor) {
        mSeekBar.setLineColor(backgroundColor);
    }

    public boolean isSeekBarVisible() {
        return mThreeSeekbarLayout.getVisibility() == View.VISIBLE;
    }

    private View mLastSelectedView;
    private View.OnClickListener mTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mLastSelectedView != null) {
                mLastSelectedView.setSelected(false);
            }
            mLastSelectedView = view;
            view.setSelected(true);

            if (null != mListener) {
                String tag = (String) view.getTag();
                if (tag.equals(mTextNameArray[0])) {
                    mListener.onTextFirstClick(tag);
                } else if (tag.equals(mTextNameArray[1])) {
                    mListener.onTextSecondClick(tag);
                } else if (tag.equals(mTextNameArray[2])) {
                    mListener.onTextThirdClick(tag);
                }
            }
        }
    };

    private PGEditSeekBar.OnSeekChangeListener mOnFirstSeekChangeListener = new PGEditSeekBar.OnSeekChangeListener() {

        @Override
        public void onSeekChanged(float currentValue, float step) {
            if (null != mListener) {
                mListener.onFirstSeekValueChanged(currentValue, step);
            }
        }

        @Override
        public void onSeekStopped(float currentValue, float step) {

        }
    };

    private PGEditSeekBar.OnSeekChangeListener mOnSecondSeekChangeListener = new PGEditSeekBar.OnSeekChangeListener() {

        @Override
        public void onSeekChanged(float currentValue, float step) {
            if (null != mListener) {
                mListener.onSecondSeekValueChanged(currentValue, step);
            }
        }

        @Override
        public void onSeekStopped(float currentValue, float step) {

        }
    };

    private PGEditSeekBar.OnSeekChangeListener mOnThirdSeekChangeListener = new PGEditSeekBar.OnSeekChangeListener() {

        @Override
        public void onSeekChanged(float currentValue, float step) {
            if (null != mListener) {
                mListener.onThirdSeekValueChanged(currentValue, step);
            }
        }

        @Override
        public void onSeekStopped(float currentValue, float step) {

        }
    };
}
