package com.yydcdut.note.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yydcdut.note.utils.YLog;

/**
 * Created by yuyidong on 16/3/24.
 */
public class FixViewPager extends ViewPager {
    public FixViewPager(Context context) {
        super(context);
    }

    public FixViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            YLog.e(e);
            return false;
        }
    }
}
