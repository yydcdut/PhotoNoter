package com.yydcdut.note.controller;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.ActivityCollector;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.LollipopUtils;
import com.yydcdut.note.utils.ThemeHelper;

/**
 * Created by yyd on 15-4-6.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int RESULT_NOTHING = 1;
    public static final int RESULT_DATA = 2;

    public static final int REQUEST_NOTHING = 1;

    /**
     * 是否要设置statusBar的颜色
     *
     * @return
     */
    public boolean setStatusColor() {
        return true;
    }

    /**
     * 设置主题
     *
     * @param setStatusColor
     */
    private void setTheme(boolean setStatusColor) {
        int index = LocalStorageUtils.getInstance().getThemeColor();

        setTheme(ThemeHelper.THEME.get(index).getStyle());
        if (setStatusColor) {
            setStatusBarColor(ThemeHelper.THEME.get(index).getStatusColor());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        if (LollipopUtils.AFTER_LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

    /**
     * 设置view
     *
     * @return
     */
    public abstract int setContentView();

    /**
     * init UI && Listener
     */
    public abstract void initUiAndListener();

    /**
     * 开启activity的动画
     */
    public void startActivityAnimation() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCollector.addActivity(this);
        super.onCreate(savedInstanceState);
        setTheme(setStatusColor());
        int layout = setContentView();
        setContentView(layout);
        initUiAndListener();
        startActivityAnimation();
    }

    /**
     * 得到actionbar大小
     *
     * @return
     */
    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    /**
     * 主题
     *
     * @return
     */
    public int getThemeColor() {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColor = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();
        return color;
    }

    /**
     * 计算坐标
     *
     * @param src
     * @param target
     * @return
     */
    public Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    public void sendDataUpdateBroadcast(boolean delete, String move, boolean sort, boolean number, boolean photo) {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_DELETE, delete);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_MOVE, move);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_SORT, sort);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_NUMBER, number);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_PHOTO, photo);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
