package com.yydcdut.note.views.setting;

import com.yydcdut.note.entity.Category;
import com.yydcdut.note.views.IView;

import java.util.List;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditCategoryView extends IView {

    void showProgressBar();

    void hideProgressBar();

    void finishActivity();

    void updateListView();

    void showSnackbar(String message);

    void showCategoryList(List<Category> categoryList);
}
