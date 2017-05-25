package com.yydcdut.note.aspect.permission;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by yuyidong on 2017/5/25.
 */
public class RequestType {
    private Activity mActivity;
    private Fragment mFragment;

    public RequestType(Fragment fragment) {
        mFragment = fragment;
    }

    public RequestType(Activity activity) {
        mActivity = activity;
    }

    public boolean isActivity() {
        return mActivity != null;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }
}
