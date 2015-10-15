package com.yydcdut.note.note;

import android.test.InstrumentationTestCase;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.utils.YLog;

import java.util.List;

/**
 * Created by yuyidong on 15/10/15.
 */
public class CategoryDBTest extends InstrumentationTestCase {

    public void testFind() {
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        for (Category category : categoryList) {
            YLog.i("yuyidong", category.toString());
        }
    }
}
