package com.yydcdut.note.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
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

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandBoxService extends Service implements Handler.Callback {

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return new SandBoxBinder();
    }

    public class SandBoxBinder extends Binder {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Handler handler = new Handler(this);
        if (SandBoxDBModel.getInstance().getAllNumber() > 0) {
            notification();
            NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {
                    int total = SandBoxDBModel.getInstance().getAllNumber();
                    for (int i = 0; i < total; i++) {
                        SandPhoto sandPhoto = SandBoxDBModel.getInstance().findFirstOne();
                        if (sandPhoto.getSize() == -1 || sandPhoto.getFileName().equals("X")) {
                            deleteFromDBAndSDCard(sandPhoto);
                        } else {
                            makePhoto(sandPhoto);
                        }
                    }
                    handler.sendEmptyMessage(0);
                }
            });
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        sendBroadcast2UpdateData();
        cancelNotification();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }.start();
        stopSelf();
        return false;
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
                sandPhoto.getTime(), sandPhoto.getTime(), sandPhoto.getCategory());
        photoNote.setPaletteColor(UiHelper.getPaletteColor(newBitmap));
        boolean bool = PhotoNoteDBModel.getInstance().save(photoNote);
        if (bool) {
            sendBroadcast2UpdateData();
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
     * 发送广播到外面Album去更新界面
     */
    private void sendBroadcast2UpdateData() {
        Intent intent = new Intent();
        //因为是另外个进程，所以....notify
        intent.putExtra(Const.TARGET_BROADCAST_PROCESS, true);
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        sendBroadcast(intent);//这里会进行更新处理
    }

    /**
     * 从数据库中删数据
     *
     * @param sandPhoto
     * @return
     */
    private void deleteFromDBAndSDCard(SandPhoto sandPhoto) {
        SandBoxDBModel.getInstance().delete(sandPhoto);
        new File(FilePathUtils.getSandBoxDir() + sandPhoto.getFileName()).delete();
    }

    /**
     * 通知栏
     */
    private void notification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.make_photo_notification_title))
                    .setContentText(getResources().getString(R.string.make_photo_notification))
                    .setTicker(getResources().getString(R.string.make_photo_notification_title))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setSmallIcon(R.drawable.ic_launcher);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(0, notification);
        }
    }

    /**
     * 取消通知栏
     */
    private void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(0);
        }
    }
}
