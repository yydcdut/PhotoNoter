package com.yydcdut.note.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;

import com.yydcdut.note.ICameraData;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.component.DaggerServiceComponent;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.presenters.service.impl.CameraServicePresenterImpl;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.service.ICameraServiceView;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/7/17.
 */
public class CameraService extends Service implements ICameraServiceView {
    private static final String TAG = CameraService.class.getSimpleName();

    @Inject
    CameraServicePresenterImpl mCameraServicePresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build()
                .inject(this);
        mCameraServicePresenter.attachView(this);
        registerReceiver(mKillSelfReceiver, new IntentFilter(Const.BROADCAST_CAMERA_SERVICE_KILL));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mCameraServicePresenter.stopThread();
        return true;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mKillSelfReceiver);
        super.onDestroy();
    }

    /**
     * 加添到Service之后，添加到数据库中，作图，作图完成的话就从数据库中删除
     */
    ICameraData.Stub mStub = new ICameraData.Stub() {
        @Override
        public void add(String fileName, int size, String cameraId, long time, int categoryId,
                        boolean isMirror, int ratio, int orientation,
                        String latitude, String lontitude, int whiteBalance, int flash,
                        int imageLength, int imageWidth, String make, String model, int imageFormat) throws RemoteException {
            mCameraServicePresenter.add2DB(fileName, size, cameraId, time, categoryId,
                    isMirror, ratio, orientation, latitude, lontitude, whiteBalance, flash,
                    imageLength, imageWidth, make, model, imageFormat);
        }
    };

    @Override
    public void sendBroadCast() {
        /*
         * 发送广播到外面Album去更新界面
         */
        Intent intent = new Intent();
        //因为是另外个进程，所以....
        intent.putExtra(Const.TARGET_BROADCAST_PROCESS, true);
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        sendBroadcast(intent);//这里会进行更新处理
    }

    /**
     * 因为unBind返回的是true
     * 所以退出程序的是Service不一定关闭了
     * 所以写个广播来判断
     */
    private BroadcastReceiver mKillSelfReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CameraService.this.stopSelf();
        }
    };
}
