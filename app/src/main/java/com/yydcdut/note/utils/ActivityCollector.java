package com.yydcdut.note.utils;

import android.content.Intent;

import com.yydcdut.note.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/8/9.
 */
public class ActivityCollector {
    private static final String TAG = ActivityCollector.class.getSimpleName();

    private static List<BaseActivity> activities = new ArrayList<>();

    public static void addActivity(BaseActivity activity) {
        YLog.i(TAG, "addActivity(" + activity.getClass().getSimpleName() + ")");
        activities.add(activity);
    }

    public static void removeActivity(BaseActivity activity) {
        YLog.i(TAG, "removeActivity(" + activity.getClass().getSimpleName() + ")");
        activities.remove(activity);
    }

    public static void reStart(BaseActivity fromActivity, Class<?>... toClass) {
        if (toClass != null) {
            for (int i = 0; i < toClass.length; i++) {
                YLog.i(TAG, "reStart(),start this activity :" + toClass.getClass().getSimpleName());
                fromActivity.startActivity(new Intent(fromActivity, toClass[i]));
            }
        }
        for (BaseActivity baseActivity : activities) {
            if (!baseActivity.isFinishing()) {
                YLog.i(TAG, "reStart(),finish this activity :" + baseActivity.getClass().getSimpleName());
                baseActivity.finish();
            }
        }
    }

}
