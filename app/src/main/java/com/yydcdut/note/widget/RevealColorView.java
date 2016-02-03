package com.yydcdut.note.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yydcdut.note.utils.AppCompat;

public class RevealColorView extends ViewGroup {

    public static final int ANIMATION_REVEAL = 0;
    public static final int ANIMATION_HIDE = 2;

    private static final float SCALE = 8f;
    /**
     * 圆那个view
     */
    private View inkView;
    /**
     * 颜色
     * 其实作用是来判断现在是否是打开的还是关闭
     */
    private int inkColor;
    /**
     * 圆
     */
    private ShapeDrawable circle;
    /**
     * 动画
     */
    private ViewPropertyAnimator animator;

    public RevealColorView(Context context) {
        this(context, null);
    }

    public RevealColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //eclipse
        if (isInEditMode()) {
            return;
        }
        //new出View来
        inkView = new View(context);
        //加到这个layout里面去
        addView(inkView);
        //那个圆
        circle = new ShapeDrawable(new OvalShape());
        //设置background进去
        AppCompat.setBackgroundDrawable(inkView, circle);
        //隐藏
        inkView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        inkView.layout(left, top, left + inkView.getMeasuredWidth(), top + inkView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        final float circleSize = (float) Math.sqrt(width * width + height * height) * 2f;
        final int size = (int) (circleSize / SCALE);
        final int sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        inkView.measure(sizeSpec, sizeSpec);
    }

    /**
     * 打开
     *
     * @param x           当前点击的x坐标
     * @param y           当前点击的y坐标
     * @param color       颜色
     * @param startRadius 开始的圆角度
     * @param duration    时间
     * @param listener    监听器
     */
    public void reveal(final int x, final int y, final int color, final int startRadius, long duration, final Animator.AnimatorListener listener) {
        //通过颜色来判断现在是否是打开的还是关闭
        if (color == inkColor) {
            return;
        }
        inkColor = color;

        if (animator != null) {
            animator.cancel();
        }
        //给圆颜色
        circle.getPaint().setColor(color);
        //让view显示
        inkView.setVisibility(View.VISIBLE);
        //起始的scale
        final float startScale = startRadius * 2f / inkView.getHeight();
        //最终的scale
        final float finalScale = calculateScale(x, y) * SCALE;

        prepareView(inkView, x, y, startScale);
        //开始动画
        animator = inkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundColor(color);
                inkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(animator, ANIMATION_REVEAL);
        animator.start();
    }

    public void hide(final int x, final int y, final int color, final int endRadius, final long duration, final Animator.AnimatorListener listener) {
        inkColor = Color.TRANSPARENT;

        if (animator != null) {
            animator.cancel();
        }

        inkView.setVisibility(View.VISIBLE);
        setBackgroundColor(color);

        final float startScale = calculateScale(x, y) * SCALE;
        final float finalScale = endRadius * SCALE / inkView.getWidth();

        prepareView(inkView, x, y, startScale);

        animator = inkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(animator, ANIMATION_HIDE);
        animator.start();
    }

    /**
     * 动画
     *
     * @param animator
     * @param type
     * @return
     */
    public ViewPropertyAnimator prepareAnimator(ViewPropertyAnimator animator, int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animator.withLayer();
        }
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }

    private void prepareView(View view, int x, int y, float scale) {
        final int centerX = (view.getWidth() / 2);
        final int centerY = (view.getHeight() / 2);
        view.setTranslationX(x - centerX);
        view.setTranslationY(y - centerY);
        view.setPivotX(centerX);
        view.setPivotY(centerY);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    /**
     * calculates the required scale of the ink-view to fill the whole view
     *
     * @param x circle center x
     * @param y circle center y
     * @return
     */
    private float calculateScale(int x, int y) {
        final float centerX = getWidth() / 2f;
        final float centerY = getHeight() / 2f;
        final float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        final float deltaX = centerX - x;
        final float deltaY = centerY - y;
        final float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        final float scale = 0.5f + (distance / maxDistance) * 0.5f;
        return scale;
    }
}