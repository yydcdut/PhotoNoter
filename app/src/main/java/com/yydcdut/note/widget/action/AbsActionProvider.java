package com.yydcdut.note.widget.action;

import android.content.Context;
import android.support.v4.view.ActionProvider;

/**
 * Created by yuyidong on 16/1/12.
 */
public abstract class AbsActionProvider extends ActionProvider {
    protected Context mContext;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public AbsActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    protected OnActionProviderClickListener mOnActionProviderClickListener;

    public void setOnActionProviderClickListener(OnActionProviderClickListener onActionProviderClickListener) {
        mOnActionProviderClickListener = onActionProviderClickListener;
    }

    public static interface OnActionProviderClickListener {
        void onActionClick(ActionProvider actionProvider);
    }
}
