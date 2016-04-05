package com.yydcdut.note.presenters.gallery;

import android.content.Intent;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 16/4/5.
 */
public interface IMediaPhotoPresenter extends IPresenter {
    void jump2DetailPhoto(int position, boolean isPreviewSelected);

    void updateListNavigation(int position);

    void onSelected(int position, boolean isSelected);

    boolean onReturnData(int requestCode, int resultCode, Intent data);

}
