package com.yydcdut.note.mvp.p.service.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.service.ICameraServicePresenter;
import com.yydcdut.note.mvp.v.service.ICameraServiceView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/22.
 * * //todo  OOM
 */
public class CameraServicePresenterImpl implements ICameraServicePresenter {
    private ICameraServiceView mCameraServiceView;
    private Queue<SandPhoto> mQueue = new ArrayBlockingQueue<SandPhoto>(10);

    private boolean mGotoStop = false;
    private byte[] mObject = new byte[1];

    private RxPhotoNote mRxPhotoNote;
    private RxSandBox mRxSandBox;

    @Inject
    public CameraServicePresenterImpl(RxPhotoNote rxPhotoNote, RxSandBox rxSandBox) {
        mRxPhotoNote = rxPhotoNote;
        mRxSandBox = rxSandBox;
        new Thread(new MakePhotoRunnable()).start();
    }

    @Override
    public void attachView(IView iView) {
        mCameraServiceView = (ICameraServiceView) iView;
    }

    @Override
    public void detachView() {

    }

    @Override
    public void stopThread() {
        mGotoStop = true;
    }

    @Override
    public void add2DB(String fileName, int size, String cameraId, long time, int categoryId,
                       boolean isMirror, int ratio, int orientation,
                       String latitude, String lontitude, int whiteBalance, int flash,
                       int imageLength, int imageWidth, String make, String model) {
        SandExif sandExif = new SandExif(orientation, latitude, lontitude, whiteBalance, flash, imageLength, imageWidth, make, model);
        SandPhoto sandPhoto = new SandPhoto(SandPhoto.ID_NULL, time, cameraId, categoryId, isMirror, ratio, fileName, size, sandExif);
        mRxSandBox.saveOne(sandPhoto)
                .subscribe(sandPhoto1 -> {
                    synchronized (mObject) {
                        long id = sandPhoto1.getId();
                        sandPhoto.setId(id);
                        mQueue.offer(sandPhoto);
                        mObject.notifyAll();
                    }
                });
    }

    /**
     * 做图线程
     */
    class MakePhotoRunnable implements Runnable {

        @Override
        public void run() {
            while (!mGotoStop) {
                SandPhoto sandPhoto = mQueue.poll();
                if (sandPhoto == null) {
                    synchronized (mObject) {
                        try {
                            mObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (sandPhoto.getSize() == -1 || sandPhoto.getFileName().equals("X")) {
                        deleteFromDBAndSDCard(sandPhoto);
                        return;
                    } else {
                        makePhoto(sandPhoto);
                    }
                }
            }
        }
    }

    /**
     * 做图
     *
     * @param sandPhoto
     */
    private void makePhoto(SandPhoto sandPhoto) {
        byte[] data = getDataFromFile(sandPhoto.getFileName(), sandPhoto.getSize());
        if (data == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        String fileName = getTime4Photo(sandPhoto.getTime()) + ".jpg";
        if (FilePathUtils.savePhoto(fileName, bitmap)) {
            FilePathUtils.saveSmallPhoto(fileName, bitmap);
        }
        PhotoNote photoNote = new PhotoNote(fileName, sandPhoto.getTime(), sandPhoto.getTime(), "", "",
                sandPhoto.getTime(), sandPhoto.getTime(), sandPhoto.getCategoryId());
        photoNote.setPaletteColor(Utils.getPaletteColor(bitmap));
        mRxPhotoNote.savePhotoNote(photoNote)
                .subscribe(photoNote1 -> {
                    try {
                        setExif(photoNote, sandPhoto.getSandExif(), sandPhoto.getCameraId(), sandPhoto.isMirror());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    deleteFromDBAndSDCard(sandPhoto);
                    mCameraServiceView.sendBroadCast();
                });
        bitmap.recycle();
        System.gc();
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
            file.delete();
            return data;
        }
    }

    //todo isMirror不好做啊！
    private void setExif(PhotoNote photoNote, SandExif sandExif, String cameraId, boolean isMirror) throws IOException {
        ExifInterface exif = new ExifInterface(photoNote.getBigPhotoPathWithoutFile());
        if (cameraId.equals(Const.CAMERA_BACK)) {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
        } else {
            if (!isMirror) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
            } else {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270
                        | ExifInterface.ORIENTATION_FLIP_HORIZONTAL));
            }
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, sandExif.getLatitude());
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, sandExif.getLontitude());
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
            if (!isMirror) {
                exif2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
            } else {
                exif2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270
                        | ExifInterface.ORIENTATION_FLIP_HORIZONTAL));
            }
        }
        exif2.saveAttributes();
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
                .subscribe(integer -> {
                    new File(path).delete();
                });
    }

    /**
     * 通过时间戳给出固定格式的照片名字
     *
     * @param time
     * @return
     */
    private static String getTime4Photo(long time) {
        String s;
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
        s = format1.format(time);
        return s;
    }
}
