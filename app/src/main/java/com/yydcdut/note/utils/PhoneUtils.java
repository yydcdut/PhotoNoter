package com.yydcdut.note.utils;

import android.content.pm.PackageManager;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/11/13.
 */
public class PhoneUtils {
    public static String getVersion() {
        PackageManager manager = NoteApplication.getContext().getPackageManager();
        String version;
        try {
            version = manager.getPackageInfo(NoteApplication.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = NoteApplication.getContext().getResources().getString(R.string.detail_unknown);
        }
        return version;
    }
}
