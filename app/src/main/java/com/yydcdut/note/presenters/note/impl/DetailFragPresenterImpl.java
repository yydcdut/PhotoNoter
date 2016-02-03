package com.yydcdut.note.presenters.note.impl;

import android.media.ExifInterface;

import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.note.IDetailFragPresenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.note.IDetailFragView;

import java.io.IOException;

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
                .subscribe(photoNote -> {
                    int[] size = getSize(photoNote.getBigPhotoPathWithoutFile());
                    mDetailFragView.showImage(size[0], size[1], photoNote.getSmallPhotoPathWithFile());
                });
    }

    @Override
    public void jump2ZoomActivity() {
        mDetailFragView.jump2ZoomActivity(mCategoryId, mPosition, mComparator);
    }

    private int[] getSize(String path) {
        int[] size = FilePathUtils.getPictureSize(path);
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                case ExifInterface.ORIENTATION_ROTATE_270:
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL | ExifInterface.ORIENTATION_ROTATE_270:
                    int tmp = size[1];
                    size[1] = size[0];
                    size[0] = tmp;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }


}
