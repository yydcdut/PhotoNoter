package com.yydcdut.note.presenters.gallery.impl;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.entity.gallery.MediaFolder;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.gallery.RxGalleryPhotos;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.presenters.gallery.IMediaPhotoPresenter;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.gallery.IMediaPhotoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 16/4/5.
 */
public class MediaPhotoPresenterImpl implements IMediaPhotoPresenter {
    private Map<String, MediaFolder> mMediaFolderByNameMap;
    private List<String> mFolderNameList;
    private String mCurrentFolderName = null;

    private IMediaPhotoView mIMediaPhotoView;

    private Context mContext;

    private RxGalleryPhotos mRxGalleryPhotos;
    private SelectPhotoModel mSelectPhotoModel;

    @Inject
    public MediaPhotoPresenterImpl(@ContextLife("Activity") Context context,
                                   RxGalleryPhotos rxGalleryPhotos, SelectPhotoModel selectPhotoModel) {
        mContext = context;
        mRxGalleryPhotos = rxGalleryPhotos;
        mSelectPhotoModel = selectPhotoModel;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mIMediaPhotoView = (IMediaPhotoView) iView;
        initListNavigationData();
    }

    private void initListNavigationData() {
        mRxGalleryPhotos.findByMedia()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringMediaFolderMap -> {
                    mMediaFolderByNameMap = stringMediaFolderMap;
                    mFolderNameList = new ArrayList<>(mMediaFolderByNameMap.size());
                    for (Map.Entry<String, MediaFolder> entry : mMediaFolderByNameMap.entrySet()) {
                        mFolderNameList.add(entry.getKey());
                    }
                    mFolderNameList.remove(MediaFolder.ALL);
                    mFolderNameList.add(0, MediaFolder.ALL);
                    mCurrentFolderName = MediaFolder.ALL;
                    mIMediaPhotoView.setListNavigationAdapter(mFolderNameList);
                    mIMediaPhotoView.setMediaAdapter(mMediaFolderByNameMap.get(mCurrentFolderName), mSelectPhotoModel);
                }, (throwable -> YLog.e(throwable)));
    }

    @Override
    public void detachView() {

    }

    @Override
    public IView getIView() {
        return mIMediaPhotoView;
    }

    @Override
    public void jump2DetailPhoto(int position, boolean isPreviewSelected) {
        mIMediaPhotoView.jump2PhotoDetail(position, mCurrentFolderName, isPreviewSelected);
    }

    @Override
    public void updateListNavigation(int position) {
        mCurrentFolderName = mFolderNameList.get(position);
        mIMediaPhotoView.updateMediaFolder(mMediaFolderByNameMap.get(mCurrentFolderName));
    }

    @Override
    public void onSelected(int position, boolean isSelected) {
        String path = mMediaFolderByNameMap.get(mCurrentFolderName).getMediaPhotoList().get(position).getPath();
        if (isSelected) {
            mSelectPhotoModel.addPath(path);
        } else {
            mSelectPhotoModel.removePath(path);
        }
        if (mSelectPhotoModel.getCount() == 0) {
            mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view));
        } else {
            mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view) + "(" + mSelectPhotoModel.getCount() + ")");
        }
    }

    @Override
    public boolean onReturnData(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_CODE && resultCode == BaseActivity.CODE_RESULT_CHANGED) {
            mIMediaPhotoView.notifyDataChanged();
            if (mSelectPhotoModel.getCount() == 0) {
                mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view));
            } else {
                mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view) + "(" + mSelectPhotoModel.getCount() + ")");
            }
            return true;
        }
        return false;
    }
}
