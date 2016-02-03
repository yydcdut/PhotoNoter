package com.yydcdut.note.widget.fab2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yydcdut.note.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuyidong on 15/10/28.
 */
public class FloatingMenuLayout extends CoordinatorLayout implements View.OnClickListener {
    private RotationFloatingActionButton mRotationFloatingActionButton;
    private Map<Integer, int[]> mLocationMap;
    private int[] mMenuLocation;
    private boolean mIsOpened = false;

    private int mLabelStyle;

    private OnFloatingActionsMenuUpdateListener mListener;

    public FloatingMenuLayout(Context context) {
        this(context, null);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CustomFloatingActionMenu, 0, 0);
        mLabelStyle = attr.getResourceId(R.styleable.CustomFloatingActionMenu_menu_label_style, 0);
        attr.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLocationMap = new HashMap<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof RotationFloatingActionButton) {
                mRotationFloatingActionButton = (RotationFloatingActionButton) view;
                mRotationFloatingActionButton.setOnClickListener(this);
            } else {
                view.setVisibility(INVISIBLE);
            }
        }
    }

    private void getInfo() {
        if (mLocationMap == null) {
            throw new NullPointerException("");
        }
        if (mLocationMap.size() != 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int top = view.getTop();
            int left = view.getLeft();
            int bottom = view.getBottom();
            int right = view.getRight();
            if (view instanceof RotationFloatingActionButton) {
                mRotationFloatingActionButton = (RotationFloatingActionButton) view;
                mRotationFloatingActionButton.setOnClickListener(this);
                mMenuLocation = new int[]{left, top, right, bottom};
            } else if (view instanceof ScrollFloatingActionButton) {
                mLocationMap.put(i, new int[]{left, top, right, bottom});
                view.setVisibility(GONE);
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) view;
                if (!TextUtils.isEmpty(btn.getTitle())) {
                    TextView textView = new TextView(getContext());
                    if (mLabelStyle != 0) {
                        //todo 这里没有作用！！
                        textView.setTextAppearance(getContext(), mLabelStyle);
                    }
                    textView.setTextColor(getResources().getColor(R.color.fab_white));
                    textView.setBackgroundResource(R.drawable.fab_label_background);
                    textView.setText(btn.getTitle());
                    btn.setTag(textView);
                    textView.setTag(new Rect(0, 0, 0, 0));
                    textView.setVisibility(INVISIBLE);
                    addView(textView);
                }
                btn.initLocation(mLocationMap.get(i), mMenuLocation);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        getInfo();
    }


    @Override
    public void onClick(View v) {
        if (v == mRotationFloatingActionButton) {
            if (mIsOpened) {
                mRotationFloatingActionButton.closeAnimation();
                mIsOpened = false;
                scrollClose();
            } else {
                mRotationFloatingActionButton.openAnimation();
                mIsOpened = true;
                scrollOpen();
            }
        }
    }

    public boolean isOpen() {
        return mIsOpened;
    }

    public void close() {
        if (!mIsOpened) {
            return;
        }
        mRotationFloatingActionButton.closeAnimation();
        mIsOpened = false;
        scrollClose();
    }

    public void open() {
        if (mIsOpened) {
            return;
        }
        mRotationFloatingActionButton.openAnimation();
        mIsOpened = true;
        scrollOpen();
    }


    private void scrollOpen() {
        if (mListener != null) {
            mListener.onMenuExpanded();
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) getChildAt(i);
                btn.setVisibility(VISIBLE);
                if (btn.getTag() != null) {
                    ((TextView) btn.getTag()).setVisibility(VISIBLE);
                }
                btn.scroll2OpenSmooth();
            }
        }
    }

    private void scrollClose() {
        if (mListener != null) {
            mListener.onMenuCollapsed();
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ScrollFloatingActionButton) {
                ScrollFloatingActionButton btn = (ScrollFloatingActionButton) getChildAt(i);
                btn.setVisibility(VISIBLE);
                if (btn.getTag() != null) {
                    ((TextView) btn.getTag()).setVisibility(VISIBLE);
                }
                btn.scroll2CloseSmooth();
            }
        }
    }

    public RotationFloatingActionButton getRotationFloatingActionButton() {
        return mRotationFloatingActionButton;
    }

    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener listener) {
        mListener = listener;
    }

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }

    public void setMenuClickable(boolean clickable) {
        mRotationFloatingActionButton.setClickable(clickable);
    }
}
