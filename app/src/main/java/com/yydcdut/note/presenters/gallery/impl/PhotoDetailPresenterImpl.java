package com.yydcdut.note.presenters.gallery.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.gallery.MediaPhoto;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.gallery.PhotoModel;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.presenters.gallery.IPhotoDetailPresenter;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.gallery.IPhotoDetailView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by yuyidong on 16/4/5.
 */
public class PhotoDetailPresenterImpl implements IPhotoDetailPresenter,
        IPhotoDetailPresenter.OnAnimationAdapter {
    /* 当前的widget是否在显示 */
    private boolean isWidgetShowed = true;
    /* 当前动画是否在进行 */
    private boolean isAnimationDoing = false;
    /* 是不是浏览选中照片的模式 */
    private boolean isPreviewSelected = false;
    private int mInitPage = 0;
    private String mFolderName;

    private List<String> mAdapterPathList;

    private Context mContext;

    private IPhotoDetailView mPhotoDetailView;

    @Inject
    public PhotoDetailPresenterImpl(@ContextLife("Activity") Context context) {
        mContext = context;
    }

    @Override
    public void bindData(boolean isSelected, int initPage, String folderName) {
        isPreviewSelected = isSelected;
        mInitPage = initPage;
        mFolderName = folderName;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mPhotoDetailView = (IPhotoDetailView) iView;
    }

    @Override
    public void initViewPager() {
        List<String> selectedPathList = null;
        if (isPreviewSelected) {
            mAdapterPathList = new ArrayList<>(SelectPhotoModel.getInstance().getCount());
            for (int i = 0; i < SelectPhotoModel.getInstance().getCount(); i++) {
                mAdapterPathList.add(SelectPhotoModel.getInstance().get(i));
            }
        } else {
            List<MediaPhoto> mediaPhotoList = PhotoModel.getInstance().findByMedia(mContext).get(mFolderName).getMediaPhotoList();
            mAdapterPathList = new ArrayList<>(mediaPhotoList.size());
            for (MediaPhoto mediaPhoto : mediaPhotoList) {
                mAdapterPathList.add(mediaPhoto.getPath());
            }
            selectedPathList = new ArrayList<>(SelectPhotoModel.getInstance().getCount());
            for (int i = 0; i < SelectPhotoModel.getInstance().getCount(); i++) {
                selectedPathList.add(SelectPhotoModel.getInstance().get(i));
            }
        }
        mPhotoDetailView.setAdapter(mAdapterPathList, mInitPage);
        mPhotoDetailView.initAdapterData(isPreviewSelected, selectedPathList);
    }

    @Override
    public void onPagerChanged(int position) {
        String path = mAdapterPathList.get(position);
        if (SelectPhotoModel.getInstance().contains(path)) {
            mPhotoDetailView.setCheckBoxSelectedWithoutCallback(true);
        } else {
            mPhotoDetailView.setCheckBoxSelectedWithoutCallback(false);
        }
        mPhotoDetailView.setToolbarTitle((mPhotoDetailView.getCurrentPosition() + 1) + "/" + mAdapterPathList.size());
    }

    @Override
    public void initMenu() {
        updateMenu(SelectPhotoModel.getInstance().getCount());
    }

    @Override
    public void onChecked(boolean checked) {
        String path = mAdapterPathList.get(mPhotoDetailView.getCurrentPosition());
        if (checked && !SelectPhotoModel.getInstance().contains(path)) {
            SelectPhotoModel.getInstance().addPath(path);
        } else if (!checked) {
            SelectPhotoModel.getInstance().removePath(path);
        }
        updateMenu(SelectPhotoModel.getInstance().getCount());
    }

    private void updateMenu(int number) {
        if (number == 0) {
            mPhotoDetailView.setMenuTitle(mContext.getResources().getString(R.string.action_finish));
        } else {
            mPhotoDetailView.setMenuTitle(mContext.getResources().getString(R.string.action_finish) + "(" + number + ")");
        }
    }

    @Override
    public void detachView() {

    }


    @Override
    public void click2doAnimation() {
        if (isAnimationDoing) {
            return;
        }
        if (isWidgetShowed) {
            mPhotoDetailView.hideWidget(this);
        } else {
            mPhotoDetailView.showWidget(this);
        }
    }

    @Override
    public void onAnimationStarted(int state) {
        isAnimationDoing = true;
    }

    @Override
    public void onAnimationEnded(int state) {
        switch (state) {
            case STATE_HIDE:
                isWidgetShowed = false;
                mPhotoDetailView.hideStatusBarTime();
                break;
            case STATE_SHOW:
                isWidgetShowed = true;
                mPhotoDetailView.showStatusBarTime();
                break;
        }
        isAnimationDoing = false;
    }
}
