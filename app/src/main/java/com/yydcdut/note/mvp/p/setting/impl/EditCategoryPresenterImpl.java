package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.setting.IEditCategoryPresenter;
import com.yydcdut.note.mvp.v.setting.IEditCategoryView;
import com.yydcdut.note.utils.ThreadExecutorPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
    private List<Integer> mDeleteCategoryIdList;
    /**
     * 要重命名的category
     */
    private Map<Integer, String> mRenameCategoryLabelMap;

    private CategoryDBModel mCategoryDBModel;
    private ThreadExecutorPool mThreadExecutorPool;

    @Inject
    public EditCategoryPresenterImpl(@ContextLife("Activity") Context context, CategoryDBModel categoryDBModel,
                                     ThreadExecutorPool threadExecutorPool) {
        mContext = context;
        mCategoryDBModel = categoryDBModel;
        mThreadExecutorPool = threadExecutorPool;
    }

    @Override
    public void attachView(IView iView) {
        mHandler = new Handler(this);
        mCategoryList = mCategoryDBModel.findAll();
        mDeleteCategoryIdList = new ArrayList<>();
        mRenameCategoryLabelMap = new HashMap<>();
        mEditCategoryView = (IEditCategoryView) iView;
        mEditCategoryView.showCategoryList(mCategoryList);
    }

    @Override
    public void detachView() {
        mRenameCategoryLabelMap.clear();
        mDeleteCategoryIdList.clear();
    }

    @Override
    public void renameCategory(int index, String newLabel) {
        if (newLabel.length() == 0) {
            mEditCategoryView.showSnackbar(mContext.getResources().getString(R.string.toast_fail));
            return;
        }
        Category category = mCategoryList.get(index);
        mRenameCategoryLabelMap.put(category.getId(), newLabel);
        category.setLabel(newLabel);
        mEditCategoryView.updateListView();
    }

    @Override
    public void deleteCategory(int index) {
        Category category = mCategoryList.remove(index);
        mDeleteCategoryIdList.add(category.getId());
        mEditCategoryView.updateListView();
    }

    @Override
    public void doJob() {
        mEditCategoryView.showProgressBar();
        mThreadExecutorPool.getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                renameCategories();
                deleteCategories();
                mCategoryDBModel.updateOrder(mCategoryList);
                mHandler.sendEmptyMessage(1);
            }
        });

    }

    /**
     * 重命名
     */
    private void renameCategories() {
        if (mRenameCategoryLabelMap != null && mRenameCategoryLabelMap.size() > 0) {
            Iterator<Map.Entry<Integer, String>> iterator = mRenameCategoryLabelMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                Integer categoryId = entry.getKey();
                String newLabel = entry.getValue();
                mCategoryDBModel.refresh();
                mCategoryDBModel.updateLabel(categoryId, newLabel);
            }
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategories() {
        if (mDeleteCategoryIdList != null && mDeleteCategoryIdList.size() > 0) {
            for (int id : mDeleteCategoryIdList) {
                mCategoryDBModel.refresh();
                Category category = mCategoryDBModel.findByCategoryId(id);
                boolean isCheck = category.isCheck();
                mCategoryDBModel.delete(category);
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
