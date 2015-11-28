package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bus.CategoryDeleteEvent;
import com.yydcdut.note.bus.CategoryEditEvent;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.setting.IEditCategoryPresenter;
import com.yydcdut.note.mvp.v.setting.IEditCategoryView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/15.
 */
public class EditCategoryPresenterImpl implements IEditCategoryPresenter, Handler.Callback {
    private Context mContext;

    private IEditCategoryView mEditCategoryView;

    private Handler mHandler;
    private int mCurrentMessage = 0;

    /**
     * 要删除的category
     */
    private List<Integer> mDeleteCategoryIdList;
    /**
     * 要重命名的category
     */
    private Map<Integer, String> mRenameCategoryLabelMap;

    private RxCategory mRxCategory;

    @Inject
    public EditCategoryPresenterImpl(@ContextLife("Activity") Context context, RxCategory rxCategory) {
        mContext = context;
        mRxCategory = rxCategory;
    }

    @Override
    public void attachView(IView iView) {
        mHandler = new Handler(this);
        mDeleteCategoryIdList = new ArrayList<>();
        mRenameCategoryLabelMap = new HashMap<>();
        mEditCategoryView = (IEditCategoryView) iView;

        mRxCategory.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mEditCategoryView.showCategoryList(categories));
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
        mRxCategory.getAllCategories()
                .map(categories -> categories.get(index))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category1 -> {
                    mRenameCategoryLabelMap.put(category1.getId(), newLabel);
                    category1.setLabel(newLabel);
                    mEditCategoryView.updateListView();
                });
    }

    @Override
    public void deleteCategory(int index) {
        mRxCategory.getAllCategories()
                .map(categories -> categories.get(index))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category1 -> {
                    mDeleteCategoryIdList.add(category1.getId());
                    mEditCategoryView.updateListView();
                });
    }

    @Override
    public void doJob() {
        mEditCategoryView.showProgressBar();
        renameCategories();
        deleteCategories();
        mRxCategory.updateOrder().subscribe(categories -> mHandler.sendEmptyMessage(1));
    }

    /**
     * 重命名
     */
    private void renameCategories() {
        if (mRenameCategoryLabelMap.size() > 0) {
            Iterator<Map.Entry<Integer, String>> iterator = mRenameCategoryLabelMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                Integer categoryId = entry.getKey();
                String newLabel = entry.getValue();
                mRxCategory.updateLabel(categoryId, newLabel).subscribe(categories -> mHandler.sendEmptyMessage(1));
            }
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategories() {
        if (mDeleteCategoryIdList.size() > 0) {
            for (int id : mDeleteCategoryIdList) {
                mRxCategory.findByCategoryId(id)
                        .subscribe(category -> {
                            boolean isCheck = category.isCheck();
                            mRxCategory.delete(id).subscribe(categories1 -> mHandler.sendEmptyMessage(1));
                            if (isCheck) {//如果是menu中当前选中的这个
                                mRxCategory.getAllCategories()
                                        .subscribe(categories -> {
                                            if (categories.size() > 0) {
                                                for (Category category1 : categories) {
                                                    category1.setCheck(false);
                                                }
                                                categories.get(0).setCheck(true);
                                                mRxCategory.updateCategory(categories.get(0)).subscribe();
                                            } else {
                                                //todo 当所有的都没有了怎么办
                                            }
                                        });
                            }
                        });
            }
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        mCurrentMessage++;
        if (mCurrentMessage == 1 + mRenameCategoryLabelMap.size() + mDeleteCategoryIdList.size()) {
            mEditCategoryView.hideProgressBar();
            mEditCategoryView.finishActivity();
            if (mDeleteCategoryIdList.size() > 0) {
                EventBus.getDefault().post(new CategoryDeleteEvent());
            } else {
                EventBus.getDefault().post(new CategoryEditEvent());
            }
        }
        return false;
    }
}
