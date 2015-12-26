package com.yydcdut.note.mvp.p.note.impl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IZoomPresenter;
import com.yydcdut.note.mvp.v.note.IZoomView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.UiHelper;

import java.io.IOException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import us.pinguo.edit.sdk.base.PGEditSDK;

/**
 * Created by yuyidong on 15/11/15.
 */
public class ZoomPresenterImpl implements IZoomPresenter {
    private Context mContext;
    private RxPhotoNote mRxPhotoNote;
    private Activity mActivity;
    /* 数据 */
    private int mPosition;
    private int mComparator;
    private int mCategoryId;

    private IZoomView mZoomView;

    /* 图片有没有修改过 */
    private boolean mIsChanged = false;

    @Inject
    public ZoomPresenterImpl(@ContextLife("Activity") Context context, Activity activity,
                             RxPhotoNote rxPhotoNote) {
        mContext = context;
        mRxPhotoNote = rxPhotoNote;
        mActivity = activity;
    }

    @Override
    public void attachView(IView iView) {
        mZoomView = (IZoomView) iView;
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .map(photoNote1 -> getBitmap(photoNote1.getBigPhotoPathWithoutFile(),
                        getOrientation(photoNote1.getBigPhotoPathWithoutFile())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> mZoomView.showImage(bitmap));
        PGEditSDK.instance().initSDK(mActivity.getApplication());
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
        int memoryClass = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        if (memoryClass <= 48) {
            ImageLoaderManager.clearMemoryCache();
        }
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> {
                    String path = photoNote.getBigPhotoPathWithoutFile();
                    if (!path.endsWith(".jpg")) {
                        mZoomView.showSnackBar(mContext.getResources().getString(R.string.toast_pgedit_not_support));
                    } else {
                        mZoomView.jump2PGEditActivity(path);
                    }
                });

    }

    @Override
    public void refreshImage() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .map(photoNote1 -> getBitmap(photoNote1.getBigPhotoPathWithoutFile(),
                        getOrientation(photoNote1.getBigPhotoPathWithoutFile())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> mZoomView.showImage(bitmap));
    }

    @Override
    public void saveSmallImage(final Bitmap thumbNail) {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .doOnSubscribe(() -> mZoomView.showProgressBar())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> {
                    FilePathUtils.saveSmallPhotoFromSDK(photoNote.getPhotoName(), thumbNail);
                    photoNote.setPaletteColor(UiHelper.getPaletteColor(ImageLoaderManager.loadImageSync(photoNote.getBigPhotoPathWithFile())));
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

    private Bitmap getBitmap(String path, int orientation) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int screenWidth = Evi.sScreenWidth;
        int screenHeight = Evi.sScreenHeight;
        int[] size = FilePathUtils.getPictureSize(path);
        int width = -1;
        int height = -1;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL | ExifInterface.ORIENTATION_ROTATE_270://前置+镜像
                width = size[1];
                height = size[0];
                break;
            default:
                width = size[0];
                height = size[1];
                break;
        }
        int scaleX = width / screenWidth;
        int scaleY = height / screenHeight;
        int scale = 1;
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }

        if (scaleY > scaleX && scaleX >= 1) {
            scale = scaleY;
        }
        if (scale == 1 && (width > screenWidth || height > screenHeight)) {
            scale = 2;
        }
        options.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (orientation == ExifInterface.ORIENTATION_NORMAL || orientation == ExifInterface.ORIENTATION_UNDEFINED) {
            return bitmap;
        }
        float rotate = 0;
        boolean isMirror = false;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90f;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180f;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270f;
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL | ExifInterface.ORIENTATION_ROTATE_270://前置+镜像
                isMirror = true;
                break;
            default:
                rotate = 0f;
                isMirror = false;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        if (isMirror) {
            matrix.preScale(-1.0f, 1.0f);
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        bitmap = null;
        System.gc();
        return newBitmap;

    }

    private int getOrientation(String path) {
        int orientation = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

}
