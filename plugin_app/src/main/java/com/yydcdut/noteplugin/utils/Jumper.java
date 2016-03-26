package com.yydcdut.noteplugin.utils;

import android.app.Activity;
import android.content.Intent;

import com.yydcdut.noteplugin.controller.BaseActivity;
import com.yydcdut.noteplugin.controller.PhotoDetailActivity;

/**
 * Created by yuyidong on 16/3/22.
 */
public class Jumper {

    public static void jump2DetailActivityAll(Activity activity, int page, String folderName) {
        Intent intent = new Intent(activity, PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PAGE, page);
        intent.putExtra(BaseActivity.INTENT_FOLDER, folderName);
        intent.putExtra(BaseActivity.INTENT_PREVIEW_SELECTED, false);
        activity.startActivityForResult(intent, BaseActivity.REQUEST_CODE);
    }

    public static void jump2DetailActivityPreviewSelected(Activity activity) {
        Intent intent = new Intent(activity, PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PREVIEW_SELECTED, true);
        activity.startActivityForResult(intent, BaseActivity.REQUEST_CODE);
    }
}
