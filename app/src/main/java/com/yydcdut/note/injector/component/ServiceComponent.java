package com.yydcdut.note.injector.component;

import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerService;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.service.CameraService;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.service.InitService;
import com.yydcdut.note.service.SandBoxService;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerService
@Component(dependencies = ApplicationComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {

    @ContextLife("Service")
    Context getServiceContext();

    @ContextLife("Application")
    Context getApplicationContext();

    RxCategory getRxCategory();

    RxPhotoNote getRxPhotoNote();

    RxSandBox getRxSandBox();

    UserCenter getUserCenter();

    LocalStorageUtils getLocalStorageUtils();

    ThreadExecutorPool getThreadExecutorPool();

    void inject(InitService initService);

    void inject(CameraService cameraService);

    void inject(CheckService checkService);

    void inject(SandBoxService sandBoxService);
}
