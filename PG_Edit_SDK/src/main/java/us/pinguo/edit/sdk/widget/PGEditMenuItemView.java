package us.pinguo.edit.sdk.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.view.IMenuItemView;

/**
 * Created by pinguo on 14-5-23.
 */
public class PGEditMenuItemView extends RelativeLayout
        implements View.OnClickListener, View.OnLongClickListener, IMenuItemView {

    protected Drawable mImageViewDrawable;
    protected String mTextViewText;

    protected ImageView mIconImageView;
    protected TextView mNameTextView;

    protected OnClickListener mOnCompositeClick;
    protected OnClickListener mOnShowScrollViewClick;
    protected OnClickListener mOnDeleteViewClick;
    protected OnLongClickListener mOnLongClickListener;

    protected ImageView mScrollView;
    protected View mDownLineView;
    protected ImageView mDeleteView;

    private View mDividerView;

    public PGEditMenuItemView(Context context) {
        super(context);

        init(context);
    }

    public PGEditMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (mImageViewDrawable != null) {
            mIconImageView.setImageDrawable(mImageViewDrawable);
        }

        if (mTextViewText != null) {
            mNameTextView.setText(mTextViewText);
        }
    }

    protected void init(Context context) {

        LayoutInflater.from(context).inflate(getLayoutResources(context), this, true);

        mIconImageView = (ImageView) findViewById(R.id.icon);
        mNameTextView = (TextView) findViewById(R.id.name);

        mScrollView = (ImageView) findViewById(R.id.effect_scroll);
        setOnClickListener(this);

        mDownLineView = findViewById(R.id.down_line);
        mDividerView = findViewById(R.id.divider);

        mDeleteView = (ImageView) findViewById(R.id.delete_view);

        if (null != mDeleteView) {
            mDeleteView.setOnClickListener(this);
        }

        setOnLongClickListener(this);

    }

    protected int getLayoutResources(Context context) {
        return R.layout.menu_item;
    }

    public void setIconForImageUrl(String imageUrl) {
        ImageLoaderView imageLoaderView = (ImageLoaderView) mIconImageView;
        imageLoaderView.getOptionsBuilder().displayer(new FadeInBitmapDisplayer(400));
        imageLoaderView.setImageUrl(imageUrl);
    }

    public void setIcon(int id) {
        ImageLoaderView imageLoaderView = (ImageLoaderView) mIconImageView;
        imageLoaderView.setImageResource(id);
    }

    public void setIcon(Drawable icon) {
        if (null != mIconImageView) {
            mIconImageView.setImageDrawable(icon);
        }
    }

    public void setNameText(String str) {
        mNameTextView.setText(str);
    }

    public void setNameTextColor(String color) {
        mNameTextView.setTextColor(Color.parseColor(color));
    }

    public void setScrollViewBgColor(String color) {
        if (null == color || "".equals(color)) {
            return;
        }

        if (null != mScrollView) {
            if (color.toLowerCase().startsWith("0x")) {
                String value = color.toLowerCase().replace("0x", "#b2");
                mScrollView.setBackgroundColor(Color.parseColor(value));
            } else {
                if (color.length() <= 7) {
                    char prefix = color.charAt(0);
                    String colorValue = color.substring(1, color.length());
                    String newColor = String.valueOf(prefix) + "b2" + colorValue;
                    mScrollView.setBackgroundColor(Color.parseColor(newColor));
                } else {
                    String alpha = color.substring(0, 3);
                    String newColor = color.replace(alpha, "#b2");
                    mScrollView.setBackgroundColor(Color.parseColor(newColor));
                }
            }
        }
    }

    public void setScrollViewDrawable(int drawableId) {
        if (null != mScrollView) {
            mScrollView.setImageResource(drawableId);
        }
    }

    public void setNameBgColor(String color) {
        if (null == color || "".equals(color)) {
            return;
        }

        if (color.toLowerCase().startsWith("0x")) {
            String value = color.toLowerCase().replace("0x", "#e5");
            mNameTextView.setBackgroundColor(Color.parseColor(value));
        } else {
            mNameTextView.setBackgroundColor(Color.parseColor(color));
        }
    }

    public void setOnCompositeClick(OnClickListener onClick) {
        this.mOnCompositeClick = onClick;
    }

    public void setOnShowScrollViewClick(OnClickListener onClick) {
        this.mOnShowScrollViewClick = onClick;
    }

    public void setOnItemLongClickListener(OnLongClickListener onClick) {
        this.mOnLongClickListener = onClick;
    }

    public void setOnDeleteViewClickListener(OnClickListener onClick) {
        this.mOnDeleteViewClick = onClick;
    }

    @Override
    public void onClick(View v) {

        if (mDeleteView == v) {

            clearAnimation();
            deleteViewWithAnimation(v);

            return;
        }

        if (null != mScrollView && mScrollView.getVisibility() == View.VISIBLE) {
            mOnShowScrollViewClick.onClick(v);

        } else {
            if (null != mScrollView && mOnShowScrollViewClick != null) {
                showScrollView();
            }

            if (mOnCompositeClick != null) {
                mOnCompositeClick.onClick(v);
            }
        }
    }

    public void showScrollView() {

        if (null == mScrollView) {
            return;
        }

        AlphaAnimation animation = new AlphaAnimation(0, 1.0f);
        animation.setDuration(400);

        mScrollView.startAnimation(animation);
        mScrollView.setVisibility(View.VISIBLE);
    }

    public void hideScrollView() {
        if (null != mScrollView) {
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0);
            animation.setDuration(400);
            mScrollView.startAnimation(animation);
            mScrollView.setVisibility(View.GONE);
        }
    }

    public OnClickListener getOnClickListener() {
        return this;
    }

    public void enableDivider(boolean enable) {
        if (enable) {
            if (null != mDividerView) {
                mDividerView.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (null != mDividerView) {
            mDividerView.setVisibility(View.GONE);
        }
    }

    public void enableTitle(boolean enable) {
        if (enable) {
            mNameTextView.setVisibility(View.VISIBLE);
            return;
        }

        mNameTextView.setVisibility(View.GONE);
    }

    public void hideDownLine() {
        if (null != mDownLineView) {
            mDownLineView.setVisibility(View.INVISIBLE);
        }
    }

    public void showDownLine() {
        if (null != mDownLineView) {
            mDownLineView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        if (null != mDeleteView) {
            mDeleteView.setTag(tag);
        }
    }

    @Override
    public void setValue(String s) {

    }

    @Override
    public void hideValue() {

    }

    @Override
    public boolean onLongClick(View v) {
        if (null == mOnLongClickListener) {
            return false;
        }

        return mOnLongClickListener.onLongClick(v);
    }

    public void showDeleteView() {
        if (null != mDeleteView) {
            mDeleteView.setVisibility(View.VISIBLE);
        }
    }

    public void hideDeleteView() {
        if (null != mDeleteView) {
            mDeleteView.setVisibility(View.GONE);
        }
    }

    public void startRotateAnimation() {

    }

    private void deleteViewWithAnimation(final View v) {

        final ViewGroup parent = (ViewGroup) getParent();
        final int index = parent.indexOfChild(this);
        final int moveDis = getLayoutParams().width;
        float deleteViewMoveWidth = 0.25f * getLayoutParams().width;
        float deleteViewMoveHeight = 0.25f * getLayoutParams().height;

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.5f, 1f, 0.5f);
        scaleAnimation.setDuration(200l);
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, deleteViewMoveWidth, 0f, deleteViewMoveHeight);
        translateAnimation.setDuration(200l);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                parent.post(new Runnable() {
                    @Override
                    public void run() {
                        PGEditMenuItemView.this.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animationSet);

        if (index == parent.getChildCount() - 1) {
            parent.post(new Runnable() {
                @Override
                public void run() {
                    parent.removeView(PGEditMenuItemView.this);
                    if (null != mOnDeleteViewClick) {
                        mOnDeleteViewClick.onClick(v);
                    }
                }
            });

            return;
        }

        for (int i = index + 1; i < parent.getChildCount(); i++) {
            final View childView = parent.getChildAt(i);
            childView.clearAnimation();
            TranslateAnimation leftTranslateAnimation = new TranslateAnimation(0f, -moveDis, 0f, 0f);
            leftTranslateAnimation.setDuration(300l);
            if (i == index + 1) {
                leftTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        parent.post(new Runnable() {
                            @Override
                            public void run() {
                                parent.removeView(PGEditMenuItemView.this);
                                if (null != mOnDeleteViewClick) {
                                    mOnDeleteViewClick.onClick(v);
                                }

                                ((PGEditMenuItemView) childView).startRotateAnimation();
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            } else {
                leftTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        parent.post(new Runnable() {
                            @Override
                            public void run() {
                                ((PGEditMenuItemView) childView).startRotateAnimation();
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            childView.startAnimation(leftTranslateAnimation);

        }
    }

    public void setDownLineColor(String color) {
        if (null == color || "".equals(color)) {
            return;
        }

        if (null != mDownLineView) {
            if (color.toLowerCase().startsWith("0x")) {
                String value = color.toLowerCase().replace("0x", "#e5");
                mDownLineView.setBackgroundColor(Color.parseColor(value));
            } else {
                mDownLineView.setBackgroundColor(Color.parseColor(color));
            }
        }
    }
}
