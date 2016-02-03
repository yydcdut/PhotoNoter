package com.yydcdut.note.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.component.DaggerServiceComponent;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.presenters.service.impl.SandBoxServicePresenterImpl;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.service.ISandBoxServiceView;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandBoxService extends Service implements ISandBoxServiceView {
    @Inject
    SandBoxServicePresenterImpl mSandBoxServicePresenter;

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build()
                .inject(this);
        mSandBoxServicePresenter.attachView(this);
    }

    /**
     * 通知栏
     */
    @Override
    public void notification() {
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
    @Override
    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(0);
        }
    }

    @Override
    public void sendBroadCast() {
        Intent intent = new Intent();
        //因为是另外个进程，所以....notify
        intent.putExtra(Const.TARGET_BROADCAST_PROCESS, true);
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        sendBroadcast(intent);//这里会进行更新处理
    }

    @Override
    public void stopService() {
        stopSelf();
    }

    @Override
    public void killProgress() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
