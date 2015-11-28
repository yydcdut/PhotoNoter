package com.yydcdut.note.mvp.p.home.impl;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.bus.CategoryCreateEvent;
import com.yydcdut.note.bus.CategoryDeleteEvent;
import com.yydcdut.note.bus.CategoryMoveEvent;
import com.yydcdut.note.bus.CategoryRenameEvent;
import com.yydcdut.note.bus.CategorySortEvent;
import com.yydcdut.note.bus.CategoryUpdateEvent;
import com.yydcdut.note.bus.PhotoNoteCreateEvent;
import com.yydcdut.note.bus.PhotoNoteDeleteEvent;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.home.IHomePresenter;
import com.yydcdut.note.mvp.v.home.IHomeView;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by yuyidong on 15/11/19.
 */
public class HomePresenterImpl implements IHomePresenter {
    private IHomeView mHomeView;
    private List<Category> mListData;
    /**
     * 当前的category的Id
     */
    private int mCategoryId = -1;

    private CategoryDBModel mCategoryDBModel;
    private PhotoNoteDBModel mPhotoNoteDBModel;
    private UserCenter mUserCenter;

    @Inject
    public HomePresenterImpl(CategoryDBModel categoryDBModel, PhotoNoteDBModel photoNoteDBModel,
                             UserCenter userCenter) {
        mCategoryDBModel = categoryDBModel;
        mPhotoNoteDBModel = photoNoteDBModel;
        mUserCenter = userCenter;
    }

    @Override
    public void attachView(IView iView) {
        mHomeView = (IHomeView) iView;
        mListData = mCategoryDBModel.findAll();
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        EventBus.getDefault().unregister(this);
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    @Override
    public int getCategoryId() {
        return mCategoryId;
    }

    @Override
    public int getCheckCategoryPosition() {
        List<Category> categoryList = mCategoryDBModel.findAll();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).isCheck()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setCheckedCategoryPosition(int position) {
        mCategoryDBModel.setCategoryMenuPosition(mListData.get(position));
        mHomeView.notifyCategoryDataChanged();
        mCategoryId = mListData.get(position).getId();
        mHomeView.changeFragment(mCategoryId);
    }

    @Override
    public void changeCategoryAfterSaving(Category category) {
        mCategoryDBModel.setCategoryMenuPosition(category);
        mListData = mCategoryDBModel.refresh();
        mHomeView.notifyCategoryDataChanged();
        mCategoryId = category.getId();
        mHomeView.changePhotos4Category(mCategoryId);
    }

    @Override
    public void setAdapter() {
        mHomeView.setCategoryList(mListData);
    }

    @Override
    public void drawerUserClick(int which) {
        switch (which) {
            case USER_ONE:
                if (mUserCenter.isLoginQQ()) {
                    mHomeView.jump2UserCenterActivity();
                } else {
                    mHomeView.jump2LoginActivity();
                }
                break;
            case USER_TWO:
                if (mUserCenter.isLoginEvernote()) {
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
        if (mUserCenter.isLoginQQ()) {
            IUser qqUser = mUserCenter.getQQ();
            mHomeView.updateQQInfo(true, qqUser.getName(), qqUser.getImagePath());
        } else {
            mHomeView.updateQQInfo(false, null, null);
        }
    }

    @Override
    public void updateEvernoteInfo() {
        if (mUserCenter.isLoginEvernote()) {
            mHomeView.updateEvernoteInfo(true);
        } else {
            mHomeView.updateEvernoteInfo(false);
        }
    }

    @Override
    public void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service) {
        //有时候categoryLabel为null，感觉原因是activity被回收了，但是一直解决不掉，所以迫不得已的解决办法
        if (mCategoryId == -1) {
            List<Category> categoryList = mCategoryDBModel.findAll();
            for (Category category : categoryList) {
                if (category.isCheck()) {
                    mCategoryId = category.getId();
                }
            }
        }

        //从另外个进程过来的数据
        if (broadcast_process) {
            int number = mPhotoNoteDBModel.findByCategoryLabelByForce(mCategoryId, -1).size();
            Category category = mCategoryDBModel.findByCategoryId(mCategoryId);
            category.setPhotosNumber(number);
            mCategoryDBModel.update(category);
            mHomeView.updateCategoryList(mCategoryDBModel.findAll());
        }

        //从Service中来
        if (broadcast_service) {
            mHomeView.updateCategoryList(mCategoryDBModel.findAll());
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryCreateEvent(CategoryCreateEvent categoryCreateEvent) {
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryUpdateEvent(CategoryUpdateEvent categoryUpdateEvent) {
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryMoveEvent(CategoryMoveEvent categoryMoveEvent) {
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryRenameEvent(CategoryRenameEvent categoryRenameEvent) {
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategorySortEvent(CategorySortEvent categorySortEvent) {
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onCategoryDeleteEvent(CategoryDeleteEvent categoryDeleteEvent) {
        mListData = mCategoryDBModel.findAll();
        int beforeCategoryId = mCategoryId;
        for (Category category : mListData) {
            if (category.isCheck()) {
                mCategoryId = category.getId();
                break;
            }
        }
        mHomeView.updateCategoryList(mCategoryDBModel.findAll());
        if (mCategoryId != beforeCategoryId) {
            mHomeView.changePhotos4Category(mCategoryId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPhotoNoteCreateEvent(PhotoNoteCreateEvent photoNoteCreateEvent) {
        int number = mPhotoNoteDBModel.findByCategoryId(mCategoryId, -1).size();
        Category category = mCategoryDBModel.findByCategoryId(mCategoryId);
        if (category.getPhotosNumber() != number) {
            category.setPhotosNumber(number);
            mCategoryDBModel.update(category);
            EventBus.getDefault().post(new CategoryUpdateEvent());
            mHomeView.updateCategoryList(mCategoryDBModel.findAll());
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPhotoNoteDeleteEvent(PhotoNoteDeleteEvent photoNoteDeleteEvent) {
        int number = mPhotoNoteDBModel.findByCategoryId(mCategoryId, -1).size();
        Category category = mCategoryDBModel.findByCategoryId(mCategoryId);
        if (category.getPhotosNumber() != number) {
            category.setPhotosNumber(number);
            mCategoryDBModel.update(category);
            EventBus.getDefault().post(new CategoryUpdateEvent());
            mHomeView.updateCategoryList(mCategoryDBModel.findAll());
        }
    }

}
