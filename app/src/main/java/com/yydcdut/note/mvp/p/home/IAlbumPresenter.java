package com.yydcdut.note.mvp.p.home;

import android.net.Uri;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/20.
 */
public interface IAlbumPresenter extends IPresenter {

    void bindData(String categoryLabel);

    void checkSandBox();

    void setAlbumSort(int sort);

    void saveAlbumSort();

    int getAlbumSort();

    void jump2DetailActivity(int position);

    void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service, boolean broadcast_photo);

    void sortData();

    void changeCategoryWithPhotos(String categoryLabel);

    void movePhotos2AnotherCategory();

    void changePhotosCategory(String categoryLabel);

    void deletePhotos();

    void createCategory(String newCategoryLabel);

    void savePhotoFromLocal(Uri imageUri);

    void savePhotoFromSystemCamera();

    void jump2Camera();

    boolean checkStorageEnough();
}
