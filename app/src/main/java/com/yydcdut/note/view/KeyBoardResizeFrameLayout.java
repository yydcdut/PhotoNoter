package com.yydcdut.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yydcdut.note.utils.YLog;

/**
 * Created by yuyidong on 15/10/30.
 */
public class KeyBoardResizeFrameLayout extends FrameLayout {
    private OnkeyboardShowListener mChangedListener;

    public KeyBoardResizeFrameLayout(Context context) {
        super(context);
    }

    public KeyBoardResizeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardResizeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        YLog.i("yuyidong", "w--->" + w + "   h--->" + h + "   oldw--->" + oldw + "   oldh--->" + oldh);
        if (oldh == 0 && oldw == 0) {
            return;
        }
        if (h - oldh > 0) {
            if (mChangedListener != null) {
                mChangedListener.onKeyboardShow();
            }
        } else if (h - oldh < 0) {
            if (mChangedListener != null) {
                mChangedListener.onKeyboardHide();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        YLog.i("yuyidong", "changed--->" + changed + "   left--->" + left + "   top--->" + top + "   right--->" + right + "   bottom--->" + bottom);
    }

    public void setOnKeyboardShowListener(OnkeyboardShowListener listener) {
        mChangedListener = listener;
    }

    public interface OnkeyboardShowListener {
        void onKeyboardShow();

        void onKeyboardHide();
    }
}
