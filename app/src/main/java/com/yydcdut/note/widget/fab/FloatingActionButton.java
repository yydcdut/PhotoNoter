package com.yydcdut.note.widget.fab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yydcdut.note.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FloatingActionButton extends ImageButton {

    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SIZE_NORMAL, SIZE_MINI})
    public @interface FAB_SIZE {
    }

    /**
     * 正常的颜色
     */
    int mColorNormal;
    /**
     * 按压的颜色
     */
    int mColorPressed;
    /**
     * 当enable为false的时候的颜色
     */
    int mColorDisabled;
    /**
     * 标题
     */
    String mTitle;
    /**
     * icon Res资源
     */
    @DrawableRes
    private int mIcon;
    /**
     * icon为drawable的
     */
    private Drawable mIconDrawable;
    private int mSize;
    /**
     * 圆的大小
     * 大的56dip，小的40dip
     */
    private float mCircleSize;
    /**
     * 阴影的半径
     */
    private float mShadowRadius;
    /**
     * 阴影的偏移
     */
    private float mShadowOffset;
    /**
     * 最终drawable的大小，算上阴影那些
     */
    private int mDrawableSize;
    /**
     * 是否描边
     */
    boolean mStrokeVisible;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attributeSet
     */
    void init(Context context, AttributeSet attributeSet) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, 0, 0);
        mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal, getColor(android.R.color.holo_blue_dark));
        mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed, getColor(android.R.color.holo_blue_light));
        mColorDisabled = attr.getColor(R.styleable.FloatingActionButton_fab_colorDisabled, getColor(android.R.color.darker_gray));
        mSize = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
        mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0);
        mTitle = attr.getString(R.styleable.FloatingActionButton_fab_title);
        mStrokeVisible = attr.getBoolean(R.styleable.FloatingActionButton_fab_stroke_visible, true);
        attr.recycle();

        updateCircleSize();//通过xml的值判定出圆的大小
        mShadowRadius = getDimension(R.dimen.fab_shadow_radius);//拿到阴影半径
        mShadowOffset = getDimension(R.dimen.fab_shadow_offset);//拿到阴影偏移量
        updateDrawableSize();//计算出最后的drawable的大小

        updateBackground();
    }

    /**
     * 计算出最后的drawable的大小
     */
    private void updateDrawableSize() {
        mDrawableSize = (int) (mCircleSize + 2 * mShadowRadius);
    }

    /**
     * 通过xml的值判定出圆的大小
     */
    private void updateCircleSize() {
        mCircleSize = getDimension(mSize == SIZE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
    }

    /**
     * 设置大小，值只能是SIZE_MINI或SIZE_NORMAL
     *
     * @param size
     */
    public void setSize(@FAB_SIZE int size) {
        if (size != SIZE_MINI && size != SIZE_NORMAL) {
            throw new IllegalArgumentException("Use @FAB_SIZE constants only!");
        }

        if (mSize != size) {
            mSize = size;
            updateCircleSize();
            updateDrawableSize();
            updateBackground();
        }
    }

    /**
     * 得到button的大小
     *
     * @return
     */
    @FAB_SIZE
    public int getSize() {
        return mSize;
    }

    /**
     * 设置icon
     *
     * @param icon
     */
    public void setIcon(@DrawableRes int icon) {
        if (mIcon != icon) {
            mIcon = icon;
            mIconDrawable = null;
            updateBackground();
        }
    }

    /**
     * 设置icon
     *
     * @param iconDrawable
     */
    public void setIconDrawable(@NonNull Drawable iconDrawable) {
        if (mIconDrawable != iconDrawable) {
            mIcon = 0;
            mIconDrawable = iconDrawable;
            updateBackground();
        }
    }

    /**
     * @return the current Color for normal state.
     */
    public int getColorNormal() {
        return mColorNormal;
    }

    /**
     * 设置normal的时候的RES颜色
     *
     * @param colorNormal
     */
    public void setColorNormalResId(@ColorRes int colorNormal) {
        setColorNormal(getColor(colorNormal));
    }

    /**
     * 设置normal的时候的颜色
     *
     * @param color
     */
    public void setColorNormal(int color) {
        if (mColorNormal != color) {
            mColorNormal = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for pressed state.
     */
    public int getColorPressed() {
        return mColorPressed;
    }

    /**
     * 设置按压的Res颜色
     *
     * @param colorPressed
     */
    public void setColorPressedResId(@ColorRes int colorPressed) {
        setColorPressed(getColor(colorPressed));
    }

    /**
     * 设置按压的颜色
     *
     * @param color
     */
    public void setColorPressed(int color) {
        if (mColorPressed != color) {
            mColorPressed = color;
            updateBackground();
        }
    }

    /**
     * @return the current color for disabled state.
     */
    public int getColorDisabled() {
        return mColorDisabled;
    }

    /**
     * 设置enable为false的时候的颜色，为RES的
     *
     * @param colorDisabled
     */
    public void setColorDisabledResId(@ColorRes int colorDisabled) {
        setColorDisabled(getColor(colorDisabled));
    }

    /**
     * 设置enable为false的时候的颜色
     *
     * @param color
     */
    public void setColorDisabled(int color) {
        if (mColorDisabled != color) {
            mColorDisabled = color;
            updateBackground();
        }
    }

    /**
     * 设置描边是否可见
     *
     * @param visible
     */
    public void setStrokeVisible(boolean visible) {
        if (mStrokeVisible != visible) {
            mStrokeVisible = visible;
            updateBackground();
        }
    }

    /**
     * 描边是否可见
     *
     * @return
     */
    public boolean isStrokeVisible() {
        return mStrokeVisible;
    }

    /**
     * 得到颜色
     *
     * @param id
     * @return
     */
    int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    /**
     * 得到尺寸
     *
     * @param id
     * @return
     */
    float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
        TextView label = getLabelView();
        if (label != null) {
            label.setText(title);
        }
    }

    /**
     * 得到title的view
     *
     * @return
     */
    TextView getLabelView() {
        return (TextView) getTag(R.id.fab_label);
    }

    /**
     * 得到title的文字
     *
     * @return
     */
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置圆大小
        setMeasuredDimension(mDrawableSize, mDrawableSize);
    }

    /**
     * 更新背景
     */
    void updateBackground() {
        final float strokeWidth = getDimension(R.dimen.fab_stroke_width);
        final float halfStrokeWidth = strokeWidth / 2f;
        //LayerDrawable，叠层显示
        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{
                        getResources().getDrawable(mSize == SIZE_NORMAL ? R.drawable.fab_bg_normal : R.drawable.fab_bg_mini),//最下面那层
                        createFillDrawable(strokeWidth),
                        createOuterStrokeDrawable(strokeWidth),
                        getIconDrawable()
                });
        //icon的偏移
        int iconOffset = (int) (mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2;

        int circleInsetHorizontal = (int) (mShadowRadius);//相当于left的偏移，因为真正的Drawable是加了阴影
        int circleInsetTop = (int) (mShadowRadius - mShadowOffset);//
        int circleInsetBottom = (int) (mShadowRadius + mShadowOffset);//
        //setLayerInset这里的right和bottom是减去的意思
        layerDrawable.setLayerInset(1,
                circleInsetHorizontal,
                circleInsetTop,
                circleInsetHorizontal,
                circleInsetBottom);

        layerDrawable.setLayerInset(2,
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetTop - halfStrokeWidth),
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetBottom - halfStrokeWidth));

        layerDrawable.setLayerInset(3,
                circleInsetHorizontal + iconOffset,
                circleInsetTop + iconOffset,
                circleInsetHorizontal + iconOffset,
                circleInsetBottom + iconOffset);
        //设置到background中
        setBackgroundCompat(layerDrawable);
    }

    /**
     * icon的drawable
     * 如果没有设置icon的话返回一个透明的drawable
     *
     * @return
     */
    Drawable getIconDrawable() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        } else if (mIcon != 0) {
            return getResources().getDrawable(mIcon);
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    /**
     * 通过不同的按压情况那些来显示drawable
     * 主要是内层的drawable
     *
     * @param strokeWidth
     * @return
     */
    private StateListDrawable createFillDrawable(float strokeWidth) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, createCircleDrawable(mColorDisabled, strokeWidth));
        drawable.addState(new int[]{android.R.attr.state_pressed}, createCircleDrawable(mColorPressed, strokeWidth));
        drawable.addState(new int[]{}, createCircleDrawable(mColorNormal, strokeWidth));
        return drawable;
    }

    /**
     * 绘制一个drawable出来
     *
     * @param color
     * @param strokeWidth
     * @return
     */
    private Drawable createCircleDrawable(int color, float strokeWidth) {
        int alpha = Color.alpha(color);//拿到alpha
        int opaqueColor = opaque(color);//拿到rgb
        //圆
        ShapeDrawable fillDrawable = new ShapeDrawable(new OvalShape());
        //笔
        final Paint paint = fillDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(opaqueColor);

        Drawable[] layers = {
                fillDrawable,
                createInnerStrokesDrawable(opaqueColor, strokeWidth)
        };
        //将Drawable[] layers 合成 LayerDrawable drawable
        LayerDrawable drawable = (alpha == 255 || !mStrokeVisible)
                ? new LayerDrawable(layers) : new TranslucentLayerDrawable(alpha, layers);
        //一半的描边宽度
        int halfStrokeWidth = (int) (strokeWidth / 2f);
        //描边的drawable
        drawable.setLayerInset(1, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth);
        //返回这个drawable
        return drawable;
    }

    /**
     * 半透明LayerDrawable
     */
    private static class TranslucentLayerDrawable extends LayerDrawable {
        private final int mAlpha;

        public TranslucentLayerDrawable(int alpha, Drawable... layers) {
            super(layers);
            mAlpha = alpha;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();//获得边界
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, mAlpha, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 绘制外层的drawable
     * 就是画边框
     *
     * @param strokeWidth
     * @return
     */
    private Drawable createOuterStrokeDrawable(float strokeWidth) {
        //圆
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        //笔
        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);//锯齿
        paint.setStrokeWidth(strokeWidth);//描边宽度
        paint.setStyle(Style.STROKE);//描边style，就是中空
        paint.setColor(Color.BLACK);//颜色黑色，用来画边框
        paint.setAlpha(opacityToAlpha(0.02f));//透明度

        return shapeDrawable;
    }

    /**
     * 处理透明度
     *
     * @param opacity
     * @return
     */
    private int opacityToAlpha(float opacity) {
        return (int) (255f * opacity);
    }

    /**
     * 计算出这个rgb值的暗的值??
     *
     * @param argb
     * @return
     */
    private int darkenColor(int argb) {
        //factor给的是0.9
        return adjustColorBrightness(argb, 0.9f);
    }

    /**
     * 计算出这个rgb值的亮的值??
     *
     * @param argb
     * @return
     */
    private int lightenColor(int argb) {
        return adjustColorBrightness(argb, 1.1f);
    }

    /**
     * 这尼玛是什么算法。。。。
     * http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
     * rgb转hsv
     * http://www.rapidtables.com/convert/color/hsv-to-rgb.htm
     * hsv转rgb
     *
     * @param argb
     * @param factor
     * @return
     */
    private int adjustColorBrightness(int argb, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(argb, hsv);//Convert the argb color to its HSV components.

        hsv[2] = Math.min(hsv[2] * factor, 1f);

        return Color.HSVToColor(Color.alpha(argb), hsv);
    }

    /**
     * 通过ARGB算出一半透明的ARGB
     *
     * @param argb
     * @return
     */
    private int halfTransparent(int argb) {
        return Color.argb(
                Color.alpha(argb) / 2,
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    /**
     * 从ARGB中拿出RGB
     *
     * @param argb
     * @return
     */
    private int opaque(int argb) {
        return Color.rgb(
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    /**
     * 创建内层drawable
     *
     * @param color
     * @param strokeWidth
     * @return
     */
    private Drawable createInnerStrokesDrawable(final int color, float strokeWidth) {
        //不描边的话直接返回一个颜色为透明的drawable
        if (!mStrokeVisible) {
            return new ColorDrawable(Color.TRANSPARENT);
        }
        //圆 drawable
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        final int bottomStrokeColor = darkenColor(color);//这个暗颜色
        final int bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor);//比bottomStrokeColor透明一半
        final int topStrokeColor = lightenColor(color);//这个亮颜色
        final int topStrokeColorHalfTransparent = halfTransparent(topStrokeColor);//比topStrokeColor透明一半

        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);//锯齿
        paint.setStrokeWidth(strokeWidth);//描边宽度
        paint.setStyle(Style.STROKE);//描边
        //draws a linear gradient
        shapeDrawable.setShaderFactory(new ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[]{topStrokeColor, topStrokeColorHalfTransparent, color, bottomStrokeColorHalfTransparent, bottomStrokeColor},
                        new float[]{0f, 0.2f, 0.5f, 0.8f, 1f},
                        TileMode.CLAMP
                );
            }
        });

        return shapeDrawable;
    }

    /**
     * 设置background
     *
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    /**
     * 设置title的可见性
     *
     * @param visibility
     */
    @Override
    public void setVisibility(int visibility) {
        TextView label = getLabelView();
        if (label != null) {
            label.setVisibility(visibility);
        }

        super.setVisibility(visibility);
    }
}
