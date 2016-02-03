package com.yydcdut.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by yuyidong on 15/10/30.
 */
public class KeyBoardResizeFrameLayout extends FrameLayout {
    private OnKeyBoardShowListener mChangedListener;

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
        if (oldh == 0 && oldw == 0) {
            return;
        }
        if (h - oldh > 0) {
            if (mChangedListener != null) {
                mChangedListener.onKeyboardHide();
            }
        } else if (h - oldh < 0) {
            if (mChangedListener != null) {
                mChangedListener.onKeyboardShow();
            }
        }
    }

    public void setOnKeyboardShowListener(OnKeyBoardShowListener listener) {
        mChangedListener = listener;
    }

    public interface OnKeyBoardShowListener {
        void onKeyboardShow();

        void onKeyboardHide();
    }
}
