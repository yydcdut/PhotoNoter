package com.yydcdut.note.mvp.p.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.IEditCategoryPresenter;
import com.yydcdut.note.mvp.v.IEditCategoryView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/11/15.
 */
public class EditCategoryPresenterImpl implements IEditCategoryPresenter, Handler.Callback {
    private Context mContext;

    private IEditCategoryView mEditCategoryView;

    private List<Category> mCategoryList;

    private Handler mHandler;

    /**
     * 要删除的category
     */
    private List<String> mDeleteCategoryLabelList;
    /**
     * 要重命名的category
     */
    private Map<String, String> mRenameCategoryLabelMap;

    @Override
    public void attachView(IView iView) {
        mContext = NoteApplication.getContext();
        mHandler = new Handler(this);
        mCategoryList = CategoryDBModel.getInstance().findAll();
        mDeleteCategoryLabelList = new ArrayList<>();
        mRenameCategoryLabelMap = new HashMap<>();
        mEditCategoryView = (IEditCategoryView) iView;
        mEditCategoryView.showCategoryList(mCategoryList);
    }

    @Override
    public void detachView() {
        mRenameCategoryLabelMap.clear();
        mDeleteCategoryLabelList.clear();
    }

    @Override
    public void renameCategory(int index, String newLabel) {
        if (newLabel.length() == 0) {
            mEditCategoryView.showSnackbar(mContext.getResources().getString(R.string.toast_fail));
            return;
        }
        Category category = mCategoryList.get(index);
        mRenameCategoryLabelMap.put(category.getLabel(), newLabel);
        category.setLabel(newLabel);
        mEditCategoryView.updateListView();
    }

    @Override
    public void deleteCategory(int index) {
        Category category = mCategoryList.remove(index);
        String label = category.getLabel();
        mDeleteCategoryLabelList.add(label);
        mEditCategoryView.updateListView();
    }

    @Override
    public void doJob() {
        mEditCategoryView.showProgressBar();
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                renameCategories();
                deleteCategories();
                CategoryDBModel.getInstance().updateOrder(mCategoryList);
                mHandler.sendEmptyMessage(1);
            }
        });

    }

    /**
     * 重命名
     */
    private void renameCategories() {
        if (mRenameCategoryLabelMap != null && mRenameCategoryLabelMap.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = mRenameCategoryLabelMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String originalLabel = entry.getKey();
                String newLabel = entry.getValue();
                CategoryDBModel.getInstance().refresh();
                CategoryDBModel.getInstance().updateLabel(originalLabel, newLabel);
            }
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategories() {
        if (mDeleteCategoryLabelList != null && mDeleteCategoryLabelList.size() > 0) {
            for (String label : mDeleteCategoryLabelList) {
                CategoryDBModel.getInstance().refresh();
                Category category = CategoryDBModel.getInstance().findByCategoryLabel(label);
                boolean isCheck = category.isCheck();
                CategoryDBModel.getInstance().delete(category);
                if (isCheck) {//如果是menu中当前选中的这个
                    resetAllCategoriesCheck();
                    if (mCategoryList.size() > 0) {
                        Category newCategory = mCategoryList.get(0);
                        newCategory.setCheck(true);
                    } else {
                        //todo 当所有的都没有了怎么办
                    }
                }
            }
        }
    }

    /**
     * 取消所有的check
     */
    private void resetAllCategoriesCheck() {
        for (Category category : mCategoryList) {
            category.setCheck(false);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        mEditCategoryView.hideProgressBar();
        mEditCategoryView.finishActivity();
        return false;
    }
}
