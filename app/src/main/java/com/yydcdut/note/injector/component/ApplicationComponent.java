package com.yydcdut.note.injector.component;

import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.module.ApplicationModule;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

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

    CategoryDBModel getCategoryDBModel();

    PhotoNoteDBModel getPhotoNoteDBModel();

    SandBoxDBModel getSandBoxDBModel();

    UserCenter getUserCenter();

    LocalStorageUtils getLocalStorageUtils();

    ThreadExecutorPool getThreadExecutorPool();
}
