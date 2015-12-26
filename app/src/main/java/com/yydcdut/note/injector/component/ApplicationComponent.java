package com.yydcdut.note.injector.component;

import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.module.ApplicationModule;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.utils.LocalStorageUtils;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ContextLife("Application")
    Context getContext();

    LocalStorageUtils getLocalStorageUtils();

    RxCategory getRxCategory();

    RxPhotoNote getRxPhotoNote();

    RxSandBox getRxSandBox();
}
