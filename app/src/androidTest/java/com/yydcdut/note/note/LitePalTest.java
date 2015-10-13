package com.yydcdut.note.note;

import android.test.InstrumentationTestCase;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.utils.YLog;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by yuyidong on 15/7/22.
 */
public class LitePalTest extends InstrumentationTestCase {

    public void test() {
        Category category = new Category("111", 0, 1, false);
        category.save();
        Category category1 = new Category("222", 1, 2, false);
        category1.save();
        List<Category> categoryList = DataSupport.findAll(Category.class);
        for (Category item : categoryList) {
            YLog.i("yuyidong", item.getId() + "  " + item.getLabel() + "  " + item.getId() + "  " + item.isCheck());
            item.setCheck(false);
            item.save();
        }
    }

    public void testEasyCamera() {
        SandPhoto sandPhoto = new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, System.currentTimeMillis(), "1", "1", false, 1);
        long id = SandBoxDBModel.getInstance().save(sandPhoto);
        YLog.i("yuyidong", "id---->" + id);
        List<SandPhoto> sandPhotoList = SandBoxDBModel.getInstance().findAll();
        for (SandPhoto sandPhoto1 : sandPhotoList) {
            YLog.i("yuyidong", "easyPhoto--->" + sandPhoto1.toString());
            int rows = SandBoxDBModel.getInstance().delete(sandPhoto1);
            YLog.i("yuyidong", "rows--->" + rows);
        }
    }

}
