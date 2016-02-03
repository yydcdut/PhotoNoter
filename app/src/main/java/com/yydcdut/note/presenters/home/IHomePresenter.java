package com.yydcdut.note.presenters.home;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/19.
 */
public interface IHomePresenter extends IPresenter {
    int USER_ONE = 1;
    int USER_TWO = 2;

    void setCategoryId(int categoryId);

    int getCategoryId();

    void setCheckCategoryPosition();

    void setCheckedCategoryPosition(int position);

    void changeCategoryAfterSaving(Category category);

    void setAdapter();

    void drawerUserClick(int which);

    void drawerCloudClick();

    void updateQQInfo();

    void updateEvernoteInfo();

    void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service);

    void killCameraService();

}
