package com.yydcdut.note.injector.module;

import android.content.Context;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.utils.LocalStorageUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yuyidong on 15/11/22.
 */
@Module
public class ApplicationModule {
    private NoteApplication mApplication;
    private RxCategory mRxCategory;
    private RxPhotoNote mRxPhotoNote;
    private RxSandBox mRxSandBox;
    private RxUser mRxUser;
    private LocalStorageUtils mLocalStorageUtils;

    public ApplicationModule(NoteApplication application) {
        mApplication = application;
        mLocalStorageUtils = new LocalStorageUtils(mApplication.getApplicationContext());
        mRxSandBox = new RxSandBox(mApplication.getApplicationContext());
        mRxPhotoNote = new RxPhotoNote(mApplication.getApplicationContext());
        mRxCategory = new RxCategory(mApplication.getApplicationContext());
        mRxUser = new RxUser(mApplication.getApplicationContext());
    }

    @Provides
    @Singleton
    @ContextLife("Application")
    public Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    public RxCategory provideRxCategory() {
        return mRxCategory;
    }

    @Provides
    @Singleton
    public RxPhotoNote provideRxPhotoNote() {
        return mRxPhotoNote;
    }

    @Provides
    @Singleton
    public RxSandBox provideRxSandBox() {
        return mRxSandBox;
    }

    @Provides
    @Singleton
    public RxUser provideRxUser() {
        return mRxUser;
    }

    @Provides
    @Singleton
    public LocalStorageUtils provideLocalStorageUtils() {
        return mLocalStorageUtils;
    }
}
