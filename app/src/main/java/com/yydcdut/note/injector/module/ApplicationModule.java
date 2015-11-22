package com.yydcdut.note.injector.module;

import android.content.Context;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.ThreadExecutorPool;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yuyidong on 15/11/22.
 */
@Module
public class ApplicationModule {
    private NoteApplication mApplication;

    public ApplicationModule(NoteApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    @ContextLife("Application")
    public Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    public ThreadExecutorPool provideThreadExecutorPool() {
        return new ThreadExecutorPool();
    }

    @Provides
    @Singleton
    public CategoryDBModel provideCategoryDBModel(CategoryDBModel categoryDBModel) {
        return categoryDBModel;
    }

    @Provides
    @Singleton
    public PhotoNoteDBModel providePhotoNoteDBModel(PhotoNoteDBModel photoNoteDBModel) {
        return photoNoteDBModel;
    }
}
