package com.yydcdut.note.presenters.service.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.presenters.service.ISandBoxServicePresenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.service.ISandBoxServiceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yuyidong on 15/11/22.
 * //todo  OOM
 */
public class SandBoxServicePresenterImpl implements ISandBoxServicePresenter {
    private static final String TAG = SandBoxServicePresenterImpl.class.getSimpleName();

    private ISandBoxServiceView mSandBoxServiceView;

    private RxPhotoNote mRxPhotoNote;
    private RxSandBox mRxSandBox;

    private Handler mHandler;

    @Inject
    public SandBoxServicePresenterImpl(RxSandBox rxSandBox, RxPhotoNote rxPhotoNote) {
        mRxPhotoNote = rxPhotoNote;
        mRxSandBox = rxSandBox;
    }

    @Override
    public void attachView(IView iView) {
        mSandBoxServiceView = (ISandBoxServiceView) iView;
        mSandBoxServiceView.notification();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                finishSandBoxService();
            }
        };

        mRxSandBox.findAll()
                .flatMap(sandPhotos -> Observable.from(sandPhotos))
                .subscribe(new Subscriber<SandPhoto>() {
                    @Override
                    public void onCompleted() {
                        mHandler.sendEmptyMessageDelayed(0, 4000);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SandPhoto sandPhoto) {
                        makePhoto(sandPhoto);
                    }
                });
    }

    /**
     * 做图
     *
     * @param sandPhoto
     */
    private void makePhoto(SandPhoto sandPhoto) {
        byte[] rowData = getDataFromFile(sandPhoto.getFileName(), sandPhoto.getSize());
        if (rowData == null) {
            return;
        }
        byte[] data;
        if (sandPhoto.getImageFormat() == ImageFormat.JPEG) {
            data = rowData;
        } else {
            data = decodeNV21(rowData,
                    sandPhoto.getSandExif().getImageWidth(), sandPhoto.getSandExif().getImageLength());
        }
        if (data == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            YLog.e(TAG, e.getMessage());
        }
        String fileName = sandPhoto.getTime() + ".jpg";
        if (FilePathUtils.savePhoto(fileName, bitmap)) {
            FilePathUtils.saveSmallPhoto(fileName, bitmap);
        }
        PhotoNote photoNote = new PhotoNote(fileName, sandPhoto.getTime(), sandPhoto.getTime(), "", "",
                sandPhoto.getTime(), sandPhoto.getTime(), sandPhoto.getCategoryId());
        photoNote.setPaletteColor(Utils.getPaletteColor(bitmap));
        mRxPhotoNote.savePhotoNote(photoNote).subscribe();
        try {
            setExif(photoNote, sandPhoto.getSandExif(), sandPhoto.getCameraId(), sandPhoto.isMirror());
        } catch (IOException e) {
            e.printStackTrace();
            YLog.e(TAG, e.getMessage());
        }
        deleteFromDBAndSDCard(sandPhoto);
//        bitmap.recycle();
        System.gc();
    }

    private byte[] decodeNV21(byte[] data, int width, int height) {
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, bos);
        byte[] bytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    //todo isMirror不好做啊！
    private void setExif(PhotoNote photoNote, SandExif sandExif, String cameraId, boolean isMirror) throws IOException {
        ExifInterface exif = new ExifInterface(photoNote.getBigPhotoPathWithoutFile());
        if (cameraId.equals(Const.CAMERA_BACK)) {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
        } else {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, sandExif.getLatitude());
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, sandExif.getLongitude());
        exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, String.valueOf(sandExif.getWhiteBalance()));
        exif.setAttribute(ExifInterface.TAG_FLASH, String.valueOf(sandExif.getFlash()));
        exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(sandExif.getImageLength()));
        exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(sandExif.getImageWidth()));
        exif.setAttribute(ExifInterface.TAG_MAKE, sandExif.getMake());
        exif.setAttribute(ExifInterface.TAG_MODEL, sandExif.getModel());
        exif.saveAttributes();

        ExifInterface exif2 = new ExifInterface(photoNote.getSmallPhotoPathWithoutFile());
        if (cameraId.equals(Const.CAMERA_BACK)) {
            exif2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
        } else {
            exif2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
        }
        exif2.saveAttributes();
    }

    private byte[] getDataFromFile(String fileName, int size) {
        boolean bool = true;
        File file = new File(FilePathUtils.getSandBoxDir() + fileName);
        byte[] data;
        if (!file.exists()) {
            return null;
        }
        data = new byte[size];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            inputStream.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            bool = false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    bool = false;
                }
            }
        }
        if (!bool) {
            return null;
        } else {
            return data;
        }
    }

    /**
     * 从数据库中删数据
     *
     * @param sandPhoto
     * @return
     */
    private void deleteFromDBAndSDCard(SandPhoto sandPhoto) {
        String path = FilePathUtils.getSandBoxDir() + sandPhoto.getFileName();
        mRxSandBox.deleteOne(sandPhoto)
                .subscribe(integer -> new File(path).delete());
    }


    @Override
    public void detachView() {

    }

    private void finishSandBoxService() {
        mSandBoxServiceView.sendBroadCast();
        mSandBoxServiceView.cancelNotification();
        mSandBoxServiceView.stopService();
        mSandBoxServiceView.killProgress();
    }
}
