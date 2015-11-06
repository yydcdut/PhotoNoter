package com.yydcdut.note.note;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.SandBoxDBModel;

import java.util.List;

/**
 * Created by yuyidong on 15/11/6.
 */
public class SandBoxTest extends InstrumentationTestCase {

    public void testFind() {
        SandPhoto sandPhoto = new SandPhoto(1, new byte[]{1, 2, 3}, 1l, "0", "sss", false, 1, new SandExif(0, "s", "ss", 0, 0, 0, 1, "1", "1"));
        SandBoxDBModel.getInstance().save(sandPhoto);
        List<SandPhoto> sandPhotos = SandBoxDBModel.getInstance().findAll();
        Log.i("yuyidong", sandPhotos.size() + "   adscjksdhfkjhskjhfkjsh");
    }
}
