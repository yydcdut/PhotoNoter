package com.yydcdut.note.views.gallery;

import com.yydcdut.note.presenters.gallery.IPhotoDetailPresenter;
import com.yydcdut.note.views.IView;

import java.util.List;

/**
 * Created by yuyidong on 16/4/5.
 */
public interface IPhotoDetailView extends IView {
    void setAdapter(List<String> adapterPathList, int initPage);

    void initAdapterData(boolean isPreviewSelected, List<String> selectedPathList);

    void setCheckBoxSelectedWithoutCallback(boolean selected);

    void setToolbarTitle(String content);

    void setMenuTitle(String content);

    void hideWidget(IPhotoDetailPresenter.OnAnimationAdapter onAnimationAdapter);

    void showWidget(IPhotoDetailPresenter.OnAnimationAdapter onAnimationAdapter);

    void showStatusBarTime();

    void hideStatusBarTime();

    int getCurrentPosition();
}
