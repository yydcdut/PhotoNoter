package com.yydcdut.note.widget.fab2.snack;

import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.yydcdut.note.widget.fab2.FloatingMenuLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/29.
 */
public class SnackHelper {
    //    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    private Snackbar mSnackBar;

    public SnackHelper(Snackbar snackBar) {
        mSnackBar = snackBar;
    }

    public static SnackHelper make(View view, CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        SnackHelper snackHelper = new SnackHelper(snackbar);
        return snackHelper;
    }

    public static SnackHelper make(View view, int resId, int duration) {
        return SnackHelper.make(view, view.getResources().getText(resId), duration);
    }

    public SnackHelper setAction(@StringRes int resId, View.OnClickListener listener) {
        mSnackBar.setAction(resId, listener);
        return this;
    }

    public SnackHelper setAction(CharSequence text, final View.OnClickListener listener) {
        mSnackBar.setAction(text, listener);
        return this;
    }

    public SnackHelper setActionTextColor(ColorStateList colors) {
        mSnackBar.setActionTextColor(colors);
        return this;
    }

    public SnackHelper setActionTextColor(int color) {
        mSnackBar.setActionTextColor(color);
        return this;
    }

    public SnackHelper setText(CharSequence message) {
        mSnackBar.setText(message);
        return this;
    }

    public SnackHelper setText(@StringRes int resId) {
        mSnackBar.setText(resId);
        return this;
    }

    public SnackHelper setDuration(int duration) {
        mSnackBar.setDuration(duration);
        return this;
    }

    public int getDuration() {
        return mSnackBar.getDuration();
    }

    public View getView() {
        return mSnackBar.getView();
    }

    public void show() {
        mSnackBar.show();
    }

    public void show(final FloatingMenuLayout floatingMenuLayout) {
        final List<View> viewList = new ArrayList<View>();
        floatingMenuLayout.close();
        for (int i = 0; i < floatingMenuLayout.getChildCount(); i++) {
            if (floatingMenuLayout.getChildAt(i) instanceof TextView) {
                viewList.add(floatingMenuLayout.getChildAt(i));
            }
        }
        for (int i = 0; i < viewList.size(); i++) {
            floatingMenuLayout.removeView(viewList.get(i));
        }
        floatingMenuLayout.getRotationFloatingActionButton().setClickable(false);
        mSnackBar.show();
        long delay = (mSnackBar.getDuration() == Snackbar.LENGTH_SHORT) ? 2000 : 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < viewList.size(); i++) {
                    View view = viewList.get(i);
                    Rect rect = (Rect) view.getTag();
                    view.layout(rect.left, rect.top, rect.right, rect.bottom);
                    view.requestLayout();
                    view.invalidate();
                    floatingMenuLayout.addView(view);
                }
                floatingMenuLayout.getRotationFloatingActionButton().setClickable(true);
            }
        }, delay);
    }

    public void dismiss() {
        mSnackBar.dismiss();
    }
}
