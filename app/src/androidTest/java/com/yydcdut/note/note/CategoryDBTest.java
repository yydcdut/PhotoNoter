package com.yydcdut.note.note;

import android.test.InstrumentationTestCase;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.model.dao.CategoryDB;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.utils.YLog;

import java.util.List;

import rx.Subscriber;

/**
 * Created by yuyidong on 15/10/15.
 */
public class CategoryDBTest extends InstrumentationTestCase {
    RxCategory mRxCategory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mRxCategory = new RxCategory(this.getInstrumentation().getTargetContext());
    }

    public void testFind() {
        mRxCategory.getAllCategories()
                .subscribe((categories -> {
                    for (Category category : categories) {
                        YLog.i("yuyidong", category.toString());
                    }
                    YLog.i("yuyidong", categories.size() + "     categories.size()");
                }));
    }

    public void testRefresh() {
        mRxCategory.refreshCategories()
                .subscribe((categories -> {
                    for (Category category : categories) {
                        YLog.i("yuyidong", category.toString());
                    }
                    YLog.i("yuyidong", categories.size() + "     categories.size()");
                }));
    }

    public void testSetCategoryMenuPosition() {
        mRxCategory.getAllCategories()
                .subscribe((categories -> {
                    for (Category category : categories) {
                        YLog.i("yuyidong", category.toString());
                    }
                    YLog.i("yuyidong", categories.size() + "     categories.size()");
                    Category category = categories.get(0);
                    category.setCheck(true);
                    mRxCategory.setCategoryMenuPosition(category.getId())
                            .subscribe(new Subscriber<List<Category>>() {
                                @Override
                                public void onCompleted() {
                                    YLog.i("yuyidong", "onCompleted");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    YLog.i("yuyidong", "onError" + e.getMessage());
                                }

                                @Override
                                public void onNext(List<Category> n) {
                                    YLog.i("yuyidong", "n.size()--->" + n.size());
                                    for (Category category : n) {
                                        YLog.i("yuyidong", "n--->" + category.toString());
                                    }
                                }
                            });
                }));
    }

    public void testSaveCategory() {
        mRxCategory.saveCategory("887799", 0, 4, true)
                .subscribe(new Subscriber<List<Category>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError,,,e--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        YLog.i("yuyidong", "categories.size()--->" + categories.size());
                        for (Category category : categories) {
                            YLog.i("yuyidong", category.toString());
                        }
                    }
                });
    }

    public void testUpdateLabel() {
        mRxCategory.updateLabel(1, "7777")
                .subscribe(new Subscriber<List<Category>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError,,,e--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        YLog.i("yuyidong", "categories.size()--->" + categories.size());
                        for (Category category : categories) {
                            YLog.i("yuyidong", category.toString());
                        }
                    }
                });
    }

    public void testUpdateCategory() {
        CategoryDB categoryDB = new CategoryDB(this.getInstrumentation().getTargetContext());
        List<Category> categoryList = categoryDB.findAll();
        Category category = categoryList.get(0);
        category.setLabel("789456");
        mRxCategory.updateCategory(category)
                .subscribe(new Subscriber<List<Category>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError,,,e--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        YLog.i("yuyidong", "categories.size()--->" + categories.size());
                        for (Category category : categories) {
                            YLog.i("yuyidong", category.toString());
                        }
                    }
                });
    }

    public void testUpdateChangeCategory() {
        mRxCategory.updateChangeCategory(1, 2, 5)
                .subscribe(new Subscriber<List<Category>>() {
                    @Override
                    public void onCompleted() {
                        YLog.i("yuyidong", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        YLog.i("yuyidong", "onError,,,e--->" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        YLog.i("yuyidong", "categories.size()--->" + categories.size());
                        for (Category category : categories) {
                            YLog.i("yuyidong", category.toString());
                        }
                    }
                });
    }
}
