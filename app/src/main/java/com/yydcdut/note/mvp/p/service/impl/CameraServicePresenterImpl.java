package com.yydcdut.note.mvp.p.service.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.service.ICameraServicePresenter;
import com.yydcdut.note.mvp.v.service.ICameraServiceView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.TimeDecoder;
import com.yydcdut.note.utils.UiHelper;
import com.yydcdut.note.utils.YLog;

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
 */
public class CameraServicePresenterImpl implements ICameraServicePresenter {
    private ICameraServiceView mCameraServiceView;
    private Queue<SandPhoto> mQueue = new ArrayBlockingQueue<SandPhoto>(10);

    private boolean mGotoStop = false;

    private byte[] object = new byte[1];

    private PhotoNoteDBModel mPhotoNoteDBModel;
    private SandBoxDBModel mSandBoxDBModel;

    @Inject
    public CameraServicePresenterImpl(PhotoNoteDBModel photoNoteDBModel, SandBoxDBModel sandBoxDBModel) {
        mPhotoNoteDBModel = photoNoteDBModel;
        mSandBoxDBModel = sandBoxDBModel;
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
        long id = mSandBoxDBModel.save(sandPhoto);
        sandPhoto.setId(id);
        mQueue.offer(sandPhoto);
        synchronized (object) {
            object.notifyAll();
        }
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
                    synchronized (object) {
                        try {
                            object.wait();
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
        Bitmap newBitmap;
        Matrix matrix = new Matrix();
        if (sandPhoto.getCameraId().equals(Const.CAMERA_BACK)) {
            matrix.setRotate(90f);
        } else {
            matrix.setRotate(270f);
            if (sandPhoto.isMirror()) {
                matrix.preScale(-1.0f, 1.0f);
            }
        }
        //裁图需要的
        int beginWidth = 0;
        int beginHeight = 0;
        int width;
        int height;
        if (sandPhoto.getRatio() == Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1) {
            width = bitmap.getHeight();
            height = bitmap.getHeight();
            beginWidth = bitmap.getWidth() - bitmap.getHeight();
        } else {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        try {
            newBitmap = Bitmap.createBitmap(bitmap, beginWidth, beginHeight, width, height, matrix, true);
        } catch (Exception e) {
            YLog.e("yuyidong", "maybe oom--->" + e.getMessage());
            //todo 打个log
            return;
        }
        bitmap.recycle();
        bitmap = null;
        System.gc();
        String fileName = TimeDecoder.getTime4Photo(sandPhoto.getTime()) + ".jpg";

        if (FilePathUtils.savePhoto(fileName, newBitmap)) {
            FilePathUtils.saveSmallPhoto(fileName, newBitmap);
        }

        PhotoNote photoNote = new PhotoNote(fileName, sandPhoto.getTime(), sandPhoto.getTime(), "", "",
                sandPhoto.getTime(), sandPhoto.getTime(), sandPhoto.getCategoryId());
        photoNote.setPaletteColor(UiHelper.getPaletteColor(newBitmap));
        boolean bool = mPhotoNoteDBModel.save(photoNote);
        if (bool) {
            mCameraServiceView.sendBroadCast();
        }
        newBitmap.recycle();
        newBitmap = null;
        System.gc();
        try {
            setExif(photoNote, sandPhoto.getSandExif());
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteFromDBAndSDCard(sandPhoto);
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

    private void setExif(PhotoNote photoNote, SandExif sandExif) throws IOException {
        ExifInterface exif = new ExifInterface(photoNote.getBigPhotoPathWithoutFile());
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(sandExif.getOrientation()));
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, sandExif.getLatitude());
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, sandExif.getLontitude());
        exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, String.valueOf(sandExif.getWhiteBalance()));
        exif.setAttribute(ExifInterface.TAG_FLASH, String.valueOf(sandExif.getFlash()));
        exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(sandExif.getImageLength()));
        exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(sandExif.getImageWidth()));
        exif.setAttribute(ExifInterface.TAG_MAKE, sandExif.getMake());
        exif.setAttribute(ExifInterface.TAG_MODEL, sandExif.getModel());
        exif.saveAttributes();
    }

    /**
     * 从数据库中删数据
     *
     * @param sandPhoto
     * @return
     */
    private void deleteFromDBAndSDCard(SandPhoto sandPhoto) {
        mSandBoxDBModel.delete(sandPhoto);
        new File(FilePathUtils.getSandBoxDir() + sandPhoto.getFileName()).delete();
    }

}
