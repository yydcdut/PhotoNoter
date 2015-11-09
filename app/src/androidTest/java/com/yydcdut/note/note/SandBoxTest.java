package com.yydcdut.note.note;

import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.yydcdut.note.model.SandBoxDBModel;

/**
 * Created by yuyidong on 15/11/6.
 */
public class SandBoxTest extends InstrumentationTestCase {

    public void testFind() {
//        SandPhoto sandPhoto = new SandPhoto(1, new byte[]{1, 2, 3}, 1l, "0", "sss", false, 1, new SandExif(0, "s", "ss", 0, 0, 0, 1, "1", "1"));
//        SandBoxDBModel.getInstance().save(sandPhoto);
        Log.i("yuyidong", SandBoxDBModel.getInstance().getAllNumber() + "    allNumber");
        Log.i("yuyidong", (Resources.getSystem().getInteger(com.android.internal.R.integer.config_cursorWindowSize) * 1024) + "  ");
        for (int i = 0; i < SandBoxDBModel.getInstance().getAllNumber(); i++) {
            Log.i("yuyidong", "1111114546546541651615315165313515");
            SandBoxDBModel.getInstance().findFirstOne();
        }
    }
}
