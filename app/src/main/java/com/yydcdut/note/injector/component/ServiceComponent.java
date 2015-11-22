package com.yydcdut.note.injector.component;

import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerService;
import com.yydcdut.note.injector.module.ServiceModule;
import com.yydcdut.note.service.CameraService;
import com.yydcdut.note.service.CheckService;
import com.yydcdut.note.service.InitService;
import com.yydcdut.note.service.SandBoxService;

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

    void inject(InitService initService);

    void inject(CameraService cameraService);

    void inject(CheckService checkService);

    void inject(SandBoxService sandBoxService);
}
