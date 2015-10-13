package us.pinguo.edit.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import us.pinguo.androidsdk.PGGLSurfaceView;
import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.view.IPGEditCompareGLSurfaceView;
import us.pinguo.edit.sdk.widget.ImageLoaderView;

/**
 * Created by hlf on 14-4-29.
 */
public class PGEditCompareGLSurfaceView extends RelativeLayout
        implements View.OnTouchListener, IPGEditCompareGLSurfaceView {

    private LinearLayout layout;
    private PGGLSurfaceView mPGGLSurfaceView;
    private ImageLoaderView mImageView;

    private AlphaAnimation mHideAnimator;
    private AlphaAnimation mShowAnimator;

    private boolean hasTouch = true;

    private boolean mImageViewIsHide;

    public PGEditCompareGLSurfaceView(Context context) {
        super(context);

        init(context);
    }

    public PGEditCompareGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pg_sdk_edit_compare_pgglsurfaceview, this, true);
        mPGGLSurfaceView = (PGGLSurfaceView) findViewById(R.id.glsurfaceview);
        mImageView = (ImageLoaderView) findViewById(R.id.compare_imageview);

        mHideAnimator = new AlphaAnimation(0f, 0f);
        mHideAnimator.setDuration(0);
        mHideAnimator.setFillAfter(true);
        mHideAnimator.setFillBefore(false);

        mShowAnimator = new AlphaAnimation(1f, 1f);
        mShowAnimator.setDuration(0);
        mShowAnimator.setFillAfter(true);
        mShowAnimator.setFillBefore(false);

        setOnTouchListener(this);
    }

    public PGGLSurfaceView getPGGLSurfaceView() {
        return mPGGLSurfaceView;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageViewPhotoPath(String comparePhotoPath) {
        mImageView.setImageUrl("file://" + comparePhotoPath);
    }

    public void setImageViewPhoto(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }


    public void hidePGGLSurfaceView() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(0);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(false);

        mPGGLSurfaceView.startAnimation(alphaAnimation);
    }

    //
    public void hideCompareView() {
        mImageView.setVisibility(View.INVISIBLE);
    }


    public void showCompareView() {
        mImageView.setVisibility(View.VISIBLE);
    }


    public void showPGGLSurfaceView() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(0);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(false);

        mPGGLSurfaceView.startAnimation(alphaAnimation);

    }

    public void openTouch() {
        hasTouch = true;
    }

    public void closeTouch() {
        hasTouch = false;
    }


    public void setGLSurfaceViewLayoutParam(int width, int height) {
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPGGLSurfaceView.setLayoutParams(layoutParams);


    }

    public void setImageViewLayoutParam(int width, int height) {
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(layoutParams);

    }

    public void setGlSurfaceViewDownHideTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPGGLSurfaceView.startAnimation(mHideAnimator);
                        break;

                    case MotionEvent.ACTION_UP:
                        mPGGLSurfaceView.startAnimation(mShowAnimator);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

    }

    public void setStopTouchListener() {
        setOnTouchListener(null);
    }

    public void setGlSurfaceViewDownShowTouchListener() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (hasTouch) {
                    mPGGLSurfaceView.startAnimation(mShowAnimator);
                    mImageView.setVisibility(View.GONE);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (hasTouch) {
                    mPGGLSurfaceView.startAnimation(mHideAnimator);
                    mImageView.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
        return true;

    }
}
