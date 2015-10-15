package com.yydcdut.note.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.IBinder;
import android.os.RemoteException;

import com.yydcdut.note.ICameraData;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.YLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yuyidong on 15/7/17.
 */
public class CameraService extends Service {
    private static final String TAG = CameraService.class.getSimpleName();

    private Queue<SandPhoto> mQueue = new ArrayBlockingQueue<SandPhoto>(5);

    private boolean mGotoStop = false;

    private byte[] object = new byte[1];

    @Override
    public IBinder onBind(Intent intent) {
        new Thread(new MakePhotoRunnable()).start();
        return mStub;

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
                    makePhoto(sandPhoto);
                }
            }
        }
    }

    /**
     * 当Service解绑的时候停止线程，防止内存泄露
     */
    private void stop() {
        mGotoStop = true;
    }

    /**
     * 做图
     *
     * @param sandPhoto
     */
    private void makePhoto(SandPhoto sandPhoto) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(sandPhoto.getData(), 0, sandPhoto.getData().length);
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
            return;
        }
        bitmap.recycle();
        bitmap = null;
        System.gc();
        String fileName = Utils.getTime4Photo(sandPhoto.getTime()) + ".jpg";

        if (FilePathUtils.savePhoto(fileName, newBitmap)) {
            FilePathUtils.saveSmallPhoto(fileName, newBitmap);
        }

        PhotoNote photoNote = new PhotoNote(fileName, sandPhoto.getTime(), sandPhoto.getTime(), "", "", sandPhoto.getTime(), sandPhoto.getTime(), sandPhoto.getCategory());
        boolean bool = PhotoNoteDBModel.getInstance().save(photoNote);
        if (bool) {
            sendBroadcast2UpdateData();
        }
        newBitmap.recycle();
        newBitmap = null;
        System.gc();
        deleteFromDB(sandPhoto);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stop();
        return super.onUnbind(intent);
    }

    /**
     * 发送广播到外面Album去更新界面
     */
    private void sendBroadcast2UpdateData() {
        Intent intent = new Intent();
        //因为是另外个进程，所以....
        intent.putExtra(Const.TARGET_BROADCAST_PROCESS, true);
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        sendBroadcast(intent);//这里会进行更新处理
    }

    /**
     * 往数据库中添加数据
     *
     * @param sandPhoto
     * @return
     */
    private long add2DB(SandPhoto sandPhoto) {
        return SandBoxDBModel.getInstance().save(sandPhoto);
    }

    /**
     * 从数据库中删数据
     *
     * @param sandPhoto
     * @return
     */
    private int deleteFromDB(SandPhoto sandPhoto) {
        return SandBoxDBModel.getInstance().delete(sandPhoto);
    }

    /**
     * 加添到Service之后，添加到数据库中，作图，作图完成的话就从数据库中删除
     */
    ICameraData.Stub mStub = new ICameraData.Stub() {
        @Override
        public void add(String fileName, int size, String cameraId, long time, String category, boolean isMirror, int ratio) throws RemoteException {
            boolean bool = true;
            File file = new File(FilePathUtils.getPath() + fileName);
            byte[] data;
            if (!file.exists()) {
                return;
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
            file.delete();

            if (!bool) {
                return;
            }
            SandPhoto sandPhoto = new SandPhoto(SandPhoto.ID_NULL, data, time, cameraId, category, isMirror, ratio);
            long id = add2DB(sandPhoto);
            sandPhoto.setId(id);
            mQueue.offer(sandPhoto);
            synchronized (object) {
                object.notifyAll();
            }
        }
    };
}
