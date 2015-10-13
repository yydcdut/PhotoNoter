package us.pinguo.edit.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import us.pinguo.edit.sdk.R;

public class PGEditSeekbarLayout extends LinearLayout {

    private View mSeekBarParentView;
    private View mBottomView;

    public PGEditSeekbarLayout(Context context) {
        super(context);

        init();
    }

    public PGEditSeekbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    protected void init() {
        LayoutInflater.from(getContext().getApplicationContext())
                .inflate(R.layout.pg_sdk_edit_seekbar_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mSeekBarParentView = findViewById(R.id.seekbar_parent);
        mBottomView = findViewById(R.id.bottom);
    }

    public void showWithAnimation() {

        if (mSeekBarParentView != null) {
            AlphaAnimation showAlphaAnimation = new AlphaAnimation(0f, 1f);
            showAlphaAnimation.setDuration(300l);
            showAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            mSeekBarParentView.startAnimation(showAlphaAnimation);
        }

        float bottomHeight = getContext().getResources().getDimension(R.dimen.pg_sdk_edit_second_bottom_down_height);
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, bottomHeight, 0f);
        translateAnimation.setDuration(300l);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mBottomView.startAnimation(translateAnimation);
    }

    public void hideWithAnimation(Animation.AnimationListener animationListener) {
        if (mSeekBarParentView != null) {
            AlphaAnimation showAlphaAnimation = new AlphaAnimation(1f, 0f);
            showAlphaAnimation.setDuration(300l);
            showAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            showAlphaAnimation.setAnimationListener(animationListener);
            mSeekBarParentView.startAnimation(showAlphaAnimation);
        }

        float bottomHeight = getContext().getResources().getDimension(R.dimen.pg_sdk_edit_second_bottom_down_height);
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, bottomHeight);
        translateAnimation.setDuration(300l);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mBottomView.startAnimation(translateAnimation);
    }
}
