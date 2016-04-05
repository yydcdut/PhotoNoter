package com.yydcdut.note.presenters.gallery;

import android.content.Intent;

import com.yydcdut.note.bean.gallery.GalleryApp;
import com.yydcdut.note.presenters.IPresenter;

import java.util.List;

/**
 * Created by yuyidong on 16/4/5.
 */
public interface IGalleryPresenter extends IPresenter {
    List<GalleryApp> getGalleryAppList();

    void jump2SelectedDetailActivity();

    void onReturnData(int requestCode, int resultCode, Intent data);
}
