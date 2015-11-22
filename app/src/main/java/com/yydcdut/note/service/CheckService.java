package com.yydcdut.note.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.component.DaggerServiceComponent;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.mvp.p.service.impl.CheckServicePresenterImpl;
import com.yydcdut.note.mvp.v.service.ICheckServiceView;
import com.yydcdut.note.utils.Const;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/7/17.
 * todo 改为IntentService
 */
public class CheckService extends Service implements ICheckServiceView {
    @Inject
    CheckServicePresenterImpl mCheckServicePresenter;

    @Nullable
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
        mCheckServicePresenter.attachView(this);
        mCheckServicePresenter.check();
    }

    @Override
    public void stopService() {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_SERVICE, true);
        sendBroadcast(intent);
        stopSelf();
    }
}
