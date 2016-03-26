package com.yydcdut.note.presenters.note.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.note.IZoomPresenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.note.IZoomView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/15.
 */
public class ZoomPresenterImpl implements IZoomPresenter {
    private Context mContext;
    private RxPhotoNote mRxPhotoNote;
    /* 数据 */
    private int mPosition;
    private int mComparator;
    private int mCategoryId;

    private IZoomView mZoomView;

    /* 图片有没有修改过 */
    private boolean mIsChanged = false;

    @Inject
    public ZoomPresenterImpl(@ContextLife("Activity") Context context, RxPhotoNote rxPhotoNote) {
        mContext = context;
        mRxPhotoNote = rxPhotoNote;
    }

    @Override
    public void attachView(IView iView) {
        mZoomView = (IZoomView) iView;
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> mZoomView.showImage(photoNote.getBigPhotoPathWithFile()));

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
    public void jump2PGEditActivity() {
        mZoomView.showSnackBar(mContext.getResources().getString(R.string.function_offoline));
//        int memoryClass = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
//        if (memoryClass <= 48) {
//            ImageLoaderManager.clearMemoryCache();
//        }
//        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
//                .map(photoNoteList -> photoNoteList.get(mPosition))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(photoNote -> {
//                    String path = photoNote.getBigPhotoPathWithoutFile();
//                    if (!path.endsWith(".jpg")) {
//                        mZoomView.showSnackBar(mContext.getResources().getString(R.string.toast_pgedit_not_support));
//                    } else {
//                        mZoomView.jump2PGEditActivity(path);
//                    }
//                });

    }

    @Override
    public void refreshImage() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> mZoomView.showImage(photoNote.getBigPhotoPathWithFile()));
    }

    @Override
    public void saveSmallImage(final Bitmap thumbNail) {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .doOnSubscribe(() -> mZoomView.showProgressBar())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> {
                    FilePathUtils.saveSmallPhotoFromSDK(photoNote.getPhotoName(), thumbNail);
                    photoNote.setPaletteColor(Utils.getPaletteColor(ImageLoaderManager.loadImageSync(photoNote.getBigPhotoPathWithFile())));
                    mRxPhotoNote.updatePhotoNote(photoNote)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(photoNoteList -> {
                                sendBroadcast();
                                mZoomView.hideProgressBar();
                                mIsChanged = true;
                            });
                });
    }

    @Override
    public void finishActivity() {
        mZoomView.finishActivity(mIsChanged);
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_PHOTO, true);
        mContext.sendBroadcast(intent);
    }
}
