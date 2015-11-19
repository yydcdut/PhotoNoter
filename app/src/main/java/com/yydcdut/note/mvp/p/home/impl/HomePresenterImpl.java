package com.yydcdut.note.mvp.p.home.impl;

import android.os.Handler;
import android.os.Looper;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.observer.CategoryChangedObserver;
import com.yydcdut.note.model.observer.PhotoNoteChangedObserver;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.IHomePresenter;
import com.yydcdut.note.mvp.v.home.IHomeView;

import java.util.List;

/**
 * Created by yuyidong on 15/11/19.
 */
public class HomePresenterImpl implements IHomePresenter,
        PhotoNoteChangedObserver, CategoryChangedObserver {
    private IHomeView mHomeView;
    private List<Category> mListData;
    /**
     * 当前的category的label
     */
    private String mCategoryLabel;

    private Handler mMainHandler;

    @Override
    public void attachView(IView iView) {
        mHomeView = (IHomeView) iView;
        mListData = CategoryDBModel.getInstance().findAll();
        mMainHandler = new Handler(Looper.getMainLooper());
        PhotoNoteDBModel.getInstance().addObserver(this);
        CategoryDBModel.getInstance().addObserver(this);
    }

    @Override
    public void detachView() {
        PhotoNoteDBModel.getInstance().removeObserver(this);
        CategoryDBModel.getInstance().removeObserver(this);
    }

    @Override
    public void setCategory(String categoryLabel) {
        mCategoryLabel = categoryLabel;
    }

    @Override
    public String getCategory() {
        return mCategoryLabel;
    }

    @Override
    public int getCheckCategoryPosition() {
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).isCheck()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setCheckedCategoryPosition(int position) {
        CategoryDBModel.getInstance().setCategoryMenuPosition(mListData.get(position));
        mHomeView.notifyCategoryDataChanged();
        mCategoryLabel = mListData.get(position).getLabel();
        mHomeView.changeFragment(mCategoryLabel);
    }

    @Override
    public void setAdapter() {
        mHomeView.setCategoryList(mListData);
    }

    @Override
    public void drawerUserClick(int which) {
        switch (which) {
            case USER_ONE:
                if (UserCenter.getInstance().isLoginQQ()) {
                    mHomeView.jump2UserCenterActivity();
                } else {
                    mHomeView.jump2LoginActivity();
                }
                break;
            case USER_TWO:
                if (UserCenter.getInstance().isLoginEvernote()) {
                    mHomeView.jump2UserCenterActivity();
                } else {
                    mHomeView.jump2LoginActivity();
                }
                break;
        }
    }

    @Override
    public void drawerCloudClick() {
        mHomeView.cloudSyncAnimation();
    }

    @Override
    public void updateQQInfo() {
        if (UserCenter.getInstance().isLoginQQ()) {
            IUser qqUser = UserCenter.getInstance().getQQ();
            mHomeView.updateQQInfo(true, qqUser.getName(), qqUser.getImagePath());
        } else {
            mHomeView.updateQQInfo(false, null, null);
        }
    }

    @Override
    public void updateEvernoteInfo() {
        if (UserCenter.getInstance().isLoginEvernote()) {
            mHomeView.updateEvernoteInfo(true);
        } else {
            mHomeView.updateEvernoteInfo(false);
        }
    }

    @Override
    public void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service) {
        //有时候categoryLabel为null，感觉原因是activity被回收了，但是一直解决不掉，所以迫不得已的解决办法
        if (mCategoryLabel == null) {
            List<Category> categoryList = CategoryDBModel.getInstance().findAll();
            for (Category category : categoryList) {
                if (category.isCheck()) {
                    mCategoryLabel = category.getLabel();
                }
            }
        }

        //从另外个进程过来的数据
        if (broadcast_process) {
            int number = PhotoNoteDBModel.getInstance().findByCategoryLabelByForce(mCategoryLabel, -1).size();
            Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
            category.setPhotosNumber(number);
            CategoryDBModel.getInstance().update(category);
            mHomeView.updateCategoryList(CategoryDBModel.getInstance().findAll());
        }

        //从Service中来
        if (broadcast_service) {
            mHomeView.updateCategoryList(CategoryDBModel.getInstance().findAll());
        }
    }

    @Override
    public void onUpdate(final int CRUD) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (CRUD) {
                    case CategoryChangedObserver.OBSERVER_CATEGORY_DELETE:
                        mListData = CategoryDBModel.getInstance().findAll();
                        String beforeLabel = mCategoryLabel;
                        for (Category category : mListData) {
                            if (category.isCheck()) {
                                mCategoryLabel = category.getLabel();
                                break;
                            }
                        }
                        mHomeView.updateCategoryList(CategoryDBModel.getInstance().findAll());
                        if (!mCategoryLabel.equals(beforeLabel)) {
                            mHomeView.changePhotos4Category(mCategoryLabel);
                        }
                        break;
                    case CategoryChangedObserver.OBSERVER_CATEGORY_MOVE:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_CREATE:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_RENAME:
                    case CategoryChangedObserver.OBSERVER_CATEGORY_SORT:
                        mHomeView.updateCategoryList(CategoryDBModel.getInstance().findAll());
                        break;
                }
            }
        });
    }

    @Override
    public void onUpdate(final int CRUD, String categoryLabel) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (CRUD) {
                    case PhotoNoteChangedObserver.OBSERVER_PHOTONOTE_DELETE:
                    case PhotoNoteChangedObserver.OBSERVER_PHOTONOTE_CREATE:
                        int number = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, -1).size();
                        Category category = CategoryDBModel.getInstance().findByCategoryLabel(mCategoryLabel);
                        if (category.getPhotosNumber() != number) {
                            category.setPhotosNumber(number);
                            CategoryDBModel.getInstance().update(category);
                            mHomeView.updateCategoryList(CategoryDBModel.getInstance().findAll());
                        }
                        break;
                }
            }
        });
    }
}
