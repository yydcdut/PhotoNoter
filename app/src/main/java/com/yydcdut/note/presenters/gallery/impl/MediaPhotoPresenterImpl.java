package com.yydcdut.note.presenters.gallery.impl;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.gallery.MediaFolder;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.gallery.PhotoModel;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.presenters.gallery.IMediaPhotoPresenter;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.gallery.IMediaPhotoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by yuyidong on 16/4/5.
 */
public class MediaPhotoPresenterImpl implements IMediaPhotoPresenter {
    private Map<String, MediaFolder> mMediaFolderByNameMap;
    private List<String> mFolderNameList;
    private String mCurrentFolderName = null;

    private IMediaPhotoView mIMediaPhotoView;

    private Context mContext;

    @Inject
    public MediaPhotoPresenterImpl(@ContextLife("Activity") Context context) {
        mContext = context;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mIMediaPhotoView = (IMediaPhotoView) iView;
        initListNavigationData();
        initMediaData();
    }

    private void initListNavigationData() {
        mMediaFolderByNameMap = PhotoModel.getInstance().findByMedia(mContext);
        mFolderNameList = new ArrayList<>(mMediaFolderByNameMap.size());
        for (Map.Entry<String, MediaFolder> entry : mMediaFolderByNameMap.entrySet()) {
            mFolderNameList.add(entry.getKey());
        }
        mFolderNameList.remove(MediaFolder.ALL);
        mFolderNameList.add(0, MediaFolder.ALL);
        mCurrentFolderName = MediaFolder.ALL;
        mIMediaPhotoView.setListNavigationAdapter(mFolderNameList);
    }

    private void initMediaData() {
        mIMediaPhotoView.setMediaAdapter(mMediaFolderByNameMap.get(mCurrentFolderName));
    }

    @Override
    public void detachView() {

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
            SelectPhotoModel.getInstance().addPath(path);
        } else {
            SelectPhotoModel.getInstance().removePath(path);
        }
        if (SelectPhotoModel.getInstance().getCount() == 0) {
            mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view));
        } else {
            mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
        }
    }

    @Override
    public boolean onReturnData(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_CODE && resultCode == BaseActivity.CODE_RESULT_CHANGED) {
            mIMediaPhotoView.notifyDataChanged();
            if (SelectPhotoModel.getInstance().getCount() == 0) {
                mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view));
            } else {
                mIMediaPhotoView.setMenuTitle(mContext.getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
            }
            return true;
        }
        return false;
    }
}
