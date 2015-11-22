package com.yydcdut.note.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.component.DaggerServiceComponent;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.mvp.p.service.impl.InitServicePresenterImpl;

import javax.inject.Inject;

/**
 * Created by yyd on 15-4-26.
 */
public class InitService extends Service {
    @Inject
    InitServicePresenterImpl mInitServicePresenter;

    @Override
    public IBinder onBind(Intent intent) {
        return new InitBinder();
    }

    public class InitBinder extends Binder {
        public boolean isFinished() {
            return mInitServicePresenter.isFinish();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build()
                .inject(this);
        mInitServicePresenter.initContent();
        mInitServicePresenter.initCamera();
    }
}
