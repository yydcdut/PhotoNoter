package com.yydcdut.note.views;

import android.annotation.TargetApi;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.component.ActivityComponent;
import com.yydcdut.note.injector.component.DaggerActivityComponent;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.presenters.ThemePresenter;
import com.yydcdut.note.utils.ActivityCollector;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.ThemeHelper;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.widget.StatusBarView;

import java.lang.reflect.Field;

/**
 * Created by yyd on 15-4-6.
 */
public abstract class BaseActivity extends AppCompatActivity implements IThemeView {
    protected ActivityComponent mActivityComponent;

    public static final int RESULT_NOTHING = 1;
    public static final int RESULT_DATA = 2;
    public static final int RESULT_PICTURE = 3;
    public static final int RESULT_DATA_QQ = 4;
    public static final int RESULT_DATA_EVERNOTE = 5;
    public static final int RESULT_DATA_USER = 6;
    public static final int RESULT_DATA_IMAGE = 7;

    public static final int REQUEST_NOTHING = 1;
    public static final int REQUEST_DATA_IMAGE = 2;

    public static final int REQUEST_CODE = 1;

    public static final int CODE_RESULT_CHANGED = 1;
    public static final int CODE_RESULT_NOT_CHANGED = -1;

    public static final String INTENT_PAGE = "page";
    public static final String INTENT_FOLDER = "folder";

    public static final String INTENT_PREVIEW_SELECTED = "preview_selected";

    private ThemePresenter mThemePresenter;
    //不能为null
    protected IPresenter mIPresenter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setActivityTheme(int index) {
        super.setTheme(ThemeHelper.THEME.get(index).getStyle());
        if (AppCompat.AFTER_LOLLIPOP) {
            //NavigationBar
            getWindow().setNavigationBarColor(AppCompat.getColor(ThemeHelper.THEME.get(index).getColorPrimary(), this));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBarTranslation(boolean translation) {
        int color = ThemeHelper.getDarkPrimaryColor(this);
        if (translation) {
            color = ThemeHelper.getPrimaryColor(this);
        }
        if (AppCompat.AFTER_LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
        }
        if (AppCompat.AFTER_KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusView = createStatusBarView(color, 255);
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            if (AppCompat.AFTER_LOLLIPOP) {
                View view = decorView.getChildAt(0);
                if (view instanceof FrameLayout) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.topMargin = getStatusBarSize();
                    view.setLayoutParams(layoutParams);
                }
            }
            decorView.addView(statusView);
            setRootView();
        }
    }

    protected StatusBarView createStatusBarView(int color, int alpha) {
        StatusBarView statusBarView = new StatusBarView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarSize());
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        return statusBarView;
    }

    /**
     * 设置根布局参数
     */
    private void setRootView() {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (rootView == null) {
            return;
        }
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
    }

    /**
     * 设置状态栏
     *
     * @param wannaSet
     * @return
     */
    public boolean setWindowStatusBar(boolean wannaSet) {
        if (AppCompat.AFTER_LOLLIPOP && wannaSet) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return true;
        }
        return false;
    }

    /**
     * 是否让BaseActivity去设置statusBar
     * 设置成透明或者沉浸状态栏
     *
     * @return
     */
    public abstract boolean setStatusBar();

    /**
     * 设置view
     *
     * @return
     */
    public abstract int setContentView();

    /**
     * 注入Injector
     */
    public abstract void initInjector();

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
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build();
        ActivityCollector.addActivity(this);
        mThemePresenter = new ThemePresenter(getApplicationContext());
        mThemePresenter.attachView(this);
        mThemePresenter.setTheme();
        boolean isSet = setWindowStatusBar(setStatusBar());
        super.onCreate(savedInstanceState);
        int layout = setContentView();
        if (isSet) {
            mThemePresenter.setStatusBar();
        }
        setContentView(layout);
        initInjector();
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
     * 得到stausBar高度
     *
     * @return
     */
    public int getStatusBarSize() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            YLog.e(e1);
        }
        return sbar;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        if (mIPresenter != null) {
            mIPresenter.detachView();
        } else {
            YLog.wtf("yuyidong", "this activity " + this.getClass().getSimpleName() + "  没有设置 IPresenter!!!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mIPresenter != null) {
            PermissionUtils.permissionResult(mIPresenter, permissions, grantResults, requestCode);
        }
    }
}
