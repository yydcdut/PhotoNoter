package com.yydcdut.gallery.utils;

import android.content.Context;
import android.content.Intent;

import com.yydcdut.gallery.controller.BaseActivity;
import com.yydcdut.gallery.controller.PhotoDetailActivity;

/**
 * Created by yuyidong on 16/3/22.
 */
public class Jumper {

    public static void jump2DetailActivity(Context context, int page, String folderName) {
        Intent intent = new Intent(context, PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PAGE, page);
        intent.putExtra(BaseActivity.INTENT_FOLDER, folderName);
        context.startActivity(intent);
    }
}
