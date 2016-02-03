package com.yydcdut.note.views;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.component.DaggerFragmentComponent;
import com.yydcdut.note.injector.component.FragmentComponent;
import com.yydcdut.note.injector.module.FragmentModule;
import com.yydcdut.note.presenters.IPresenter;
import com.yydcdut.note.utils.PermissionUtils;

/**
 * Created by yuyidong on 15-3-17.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentComponent mFragmentComponent;

    public static final int RESULT_NOTHING = 1;
    public static final int RESULT_DATA = 2;
    public static final int RESULT_PICTURE = 3;
    public static final int RESULT_DATA_QQ = 4;
    public static final int RESULT_DATA_EVERNOTE = 5;

    public static final int REQUEST_NOTHING = 1;

    protected IPresenter mIPresenter;

    /**
     * 得到Activity传进来的值
     *
     * @param bundle
     */
    public abstract void getBundle(Bundle bundle);

    /**
     * 得到上下文
     *
     * @return
     */
    public Context getContext() {
        return getActivity();
    }

    /**
     * 初始化UI
     *
     * @param inflater
     * @return
     */
    public abstract View inflateView(LayoutInflater inflater);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflateView(inflater);
        return v;
    }

    public abstract void initInjector();

    /**
     * 初始化控件
     *
     * @param view
     */
    public abstract void initUI(View view);


    /**
     * 在监听器之前把数据准备好
     */
    public abstract void initData();

    /**
     * 初始化监听器
     */
    public abstract void initListener(View view);


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .applicationComponent(((NoteApplication) getActivity().getApplication()).getApplicationComponent())
                .build();
        initInjector();
        getBundle(getArguments());
        initUI(view);
        initData();
        initListener(view);
        super.onViewCreated(view, savedInstanceState);
    }

    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public int getThemeColor() {
        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[]{R.attr.colorPrimary};
        int indexOfAttrColor = 0;
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, colorAttr);
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
    public void onDestroy() {
        super.onDestroy();
        if (mIPresenter != null) {
            mIPresenter.detachView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mIPresenter != null) {
            PermissionUtils.permissionResult(mIPresenter, permissions, grantResults, requestCode);
        }
    }
}
