package com.yydcdut.note.mvp.p.note.impl;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IDetailFragPresenter;
import com.yydcdut.note.mvp.v.note.IDetailFragView;
import com.yydcdut.note.utils.FilePathUtils;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/16.
 */
public class DetailFragPresenterImpl implements IDetailFragPresenter {
    private IDetailFragView mDetailFragView;

    /* data */
    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;

    private PhotoNoteDBModel mPhotoNoteDBModel;

    @Inject
    public DetailFragPresenterImpl(PhotoNoteDBModel photoNoteDBModel) {
        mPhotoNoteDBModel = photoNoteDBModel;
    }

    @Override
    public void attachView(IView iView) {
        mDetailFragView = (IDetailFragView) iView;
        showImage();
    }

    @Override
    public void detachView() {
    }

    @Override
    public void bindData(String label, int position, int comparator) {
        mPosition = position;
        mComparator = comparator;
        mPhotoNote = mPhotoNoteDBModel.findByCategoryLabel(label, mComparator).get(mPosition);
    }

    @Override
    public void showImage() {
        int[] size = FilePathUtils.getPictureSize(mPhotoNote.getBigPhotoPathWithoutFile());
        mDetailFragView.showImage(size[0], size[1], mPhotoNote.getBigPhotoPathWithFile());
    }

    @Override
    public void jump2ZoomActivity() {
        mDetailFragView.jump2ZoomActivity(mPhotoNote.getCategoryLabel(), mPosition, mComparator);
    }


}
