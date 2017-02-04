package com.yydcdut.note.aspect.permission;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by yuyidong on 2017/2/4.
 */
public class PermissionInstance {
    public static Context context;

    public static void init(@NonNull Application application) {
        context = application;
    }
}
