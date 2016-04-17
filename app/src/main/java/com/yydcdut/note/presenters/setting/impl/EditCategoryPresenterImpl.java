package com.yydcdut.note.presenters.setting.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bus.CategoryDeleteEvent;
import com.yydcdut.note.bus.CategoryEditEvent;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.compare.ComparatorFactory;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.setting.IEditCategoryPresenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.setting.IEditCategoryView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/11/15.
 */
public class EditCategoryPresenterImpl implements IEditCategoryPresenter, Handler.Callback {
    private static final int MESSAGE_RENAME_NONE = -1;
    private static final int MESSAGE_DELETE_NONE = -2;
    private static final int MESSAGE_FINISH = -3;

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
    private RxPhotoNote mRxPhotoNote;

    @Inject
    public EditCategoryPresenterImpl(@ContextLife("Activity") Context context, RxCategory rxCategory,
                                     RxPhotoNote rxPhotoNote) {
        mContext = context;
        mRxCategory = rxCategory;
        mRxPhotoNote = rxPhotoNote;
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
        mRxCategory.refreshCategories().subscribe();
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
                .map(categories -> {
                    Category category = categories.get(index);
                    categories.remove(category);
                    return category;
                })
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
    }

    private void sortCategories() {
        mRxCategory.updateOrder().subscribe(categories -> mHandler.sendEmptyMessage(1));
    }

    /**
     * 重命名
     */
    private void renameCategories() {
        if (mRenameCategoryLabelMap.size() > 0) {
            mRxCategory.refreshCategories()
                    .subscribe(categories1 -> {
                        Iterator<Map.Entry<Integer, String>> iterator = mRenameCategoryLabelMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, String> entry = iterator.next();
                            Integer categoryId = entry.getKey();
                            String newLabel = entry.getValue();
                            mRxCategory.updateLabel(categoryId, newLabel)
                                    .subscribe(categories -> mHandler.sendEmptyMessage(1));
                        }
                    });
        } else {
            mHandler.sendEmptyMessage(MESSAGE_RENAME_NONE);
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategories() {
        if (mDeleteCategoryIdList.size() > 0) {
            mRxCategory.refreshCategories()
                    .observeOn(Schedulers.io())
                    .subscribe(categories -> {
                        for (int id : mDeleteCategoryIdList) {
                            mRxPhotoNote.findByCategoryId(id, ComparatorFactory.FACTORY_NOT_SORT)
                                    .subscribe(photoNoteList -> {
                                        for (PhotoNote photoNote : photoNoteList) {
                                            FilePathUtils.deleteAllFiles(photoNote.getPhotoName());
                                        }
                                        mRxPhotoNote.deletePhotoNotes(photoNoteList, id).subscribe();
                                    });
                            mRxCategory.delete(id).subscribe(categories2 -> mHandler.sendEmptyMessage(1));
                        }
                    });
        } else {
            mHandler.sendEmptyMessage(MESSAGE_DELETE_NONE);
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_RENAME_NONE:
                deleteCategories();
                return false;
            case MESSAGE_DELETE_NONE:
                sortCategories();
                return false;
            case MESSAGE_FINISH:
                if (mDeleteCategoryIdList.size() > 0) {
                    EventBus.getDefault().post(new CategoryDeleteEvent());
                } else {
                    EventBus.getDefault().post(new CategoryEditEvent());
                }
                mEditCategoryView.hideProgressBar();
                mEditCategoryView.finishActivity();
                return false;
            default:
                mCurrentMessage++;
                break;
        }
        if (mCurrentMessage == mRenameCategoryLabelMap.size()) {
            deleteCategories();
        } else if (mCurrentMessage == mRenameCategoryLabelMap.size() + mDeleteCategoryIdList.size()) {
            sortCategories();
        } else if (mCurrentMessage == mRenameCategoryLabelMap.size() + mDeleteCategoryIdList.size() + 1) {
            mRxCategory.getAllCategories()
                    .subscribe(new Subscriber<List<Category>>() {
                        @Override
                        public void onCompleted() {
                            mHandler.sendEmptyMessage(MESSAGE_FINISH);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(List<Category> categories) {
                            //todo 删除图片,删除PhotoNote
                            boolean checked = false;
                            for (Category category : categories) {
                                checked |= category.isCheck();
                            }
                            if (!checked && categories.size() > 0) {
                                categories.get(0).setCheck(true);
                                mRxCategory.updateCategory(categories.get(0)).subscribe();
                            }
                            //todo 当所有的都没有了怎么办
                        }
                    });
        }
        return false;
    }
}
