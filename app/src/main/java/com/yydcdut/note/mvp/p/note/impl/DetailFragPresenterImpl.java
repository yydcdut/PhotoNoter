package com.yydcdut.note.mvp.p.note.impl;

import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IDetailFragPresenter;
import com.yydcdut.note.mvp.v.note.IDetailFragView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class DetailFragPresenterImpl implements IDetailFragPresenter {
    private IDetailFragView mDetailFragView;

    /* data */
    private int mCategoryId;
    private int mPosition;
    private int mComparator;

    private RxPhotoNote mRxPhotoNote;

    @Inject
    public DetailFragPresenterImpl(RxPhotoNote rxPhotoNote) {
        mRxPhotoNote = rxPhotoNote;
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
    public void bindData(int categoryId, int position, int comparator) {
        mCategoryId = categoryId;
        mPosition = position;
        mComparator = comparator;
    }

    @Override
    public void showImage() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> mDetailFragView.showImage(photoNote.getBigPhotoPathWithFile()));
    }

    @Override
    public void jump2ZoomActivity() {
        mDetailFragView.jump2ZoomActivity(mCategoryId, mPosition, mComparator);
    }


}
