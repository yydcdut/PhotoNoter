package com.yydcdut.note.presenters.gallery;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 16/4/5.
 */
public interface IPhotoDetailPresenter extends IPresenter {
    int STATE_HIDE = 0;
    int STATE_SHOW = 1;

    void bindData(boolean isSelected, int initPage, String folderName);

    void initViewPager();

    void onPagerChanged(int position);

    void initMenu();

    void onChecked(boolean checked);

    void click2doAnimation();

    interface OnAnimationAdapter {
        void onAnimationStarted(int state);

        void onAnimationEnded(int state);
    }
}
