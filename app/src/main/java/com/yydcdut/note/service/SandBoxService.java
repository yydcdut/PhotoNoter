package com.yydcdut.note.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.TimeDecoder;
import com.yydcdut.note.utils.YLog;

import java.util.List;

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandBoxService extends Service implements Handler.Callback {

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Handler handler = new Handler(this);
        final List<SandPhoto> sandPhotoList = SandBoxDBModel.getInstance().findAll();
        if (sandPhotoList.size() > 0) {
            notification();
            NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < sandPhotoList.size(); i++) {
                        SandPhoto sandPhoto = sandPhotoList.get(i);
                        makePhoto(sandPhoto);
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
        String fileName = TimeDecoder.getTime4Photo(sandPhoto.getTime()) + ".jpg";

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
    private int deleteFromDB(SandPhoto sandPhoto) {
        return SandBoxDBModel.getInstance().delete(sandPhoto);
    }

    /**
     * 通知栏
     */
    private void notification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification notification = new Notification(R.drawable.ic_launcher,
                getResources().getString(R.string.make_photo_notification), System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR;
        Intent intent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, getResources().getString(R.string.app_name),
                getResources().getString(R.string.make_photo_notification), contentIntent);
        mNotificationManager.notify(0, notification);
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
