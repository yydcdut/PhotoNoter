package com.yydcdut.note.injector.module;

import android.content.Context;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.LocalStorageUtils;
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
    private CategoryDBModel mCategoryDBModel;
    private PhotoNoteDBModel mPhotoNoteDBModel;
    private SandBoxDBModel mSandBoxDBModel;
    private UserCenter mUserCenter;
    private LocalStorageUtils mLocalStorageUtils;
    private ThreadExecutorPool mThreadExecutorPool;

    public ApplicationModule(NoteApplication application) {
        mApplication = application;
        mThreadExecutorPool = new ThreadExecutorPool();
        mLocalStorageUtils = new LocalStorageUtils(mApplication.getApplicationContext());
        mUserCenter = new UserCenter(mApplication.getApplicationContext(), mThreadExecutorPool);
        mSandBoxDBModel = new SandBoxDBModel(mApplication.getApplicationContext());
        mPhotoNoteDBModel = new PhotoNoteDBModel(mApplication.getApplicationContext());
        mCategoryDBModel = new CategoryDBModel(mApplication.getApplicationContext(), mPhotoNoteDBModel, mThreadExecutorPool);
    }

    @Provides
    @Singleton
    @ContextLife("Application")
    public Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    public CategoryDBModel provideCategoryDBModel() {
        return mCategoryDBModel;
    }

    @Provides
    @Singleton
    public PhotoNoteDBModel providePhotoNoteDBModel() {
        return mPhotoNoteDBModel;
    }

    @Provides
    @Singleton
    public SandBoxDBModel provideSandBoxDBModel() {
        return mSandBoxDBModel;
    }

    @Provides
    @Singleton
    public UserCenter provideUserCenter() {
        return mUserCenter;
    }

    @Provides
    @Singleton
    public LocalStorageUtils provideLocalStorageUtils() {
        return mLocalStorageUtils;
    }

    @Provides
    @Singleton
    public ThreadExecutorPool provideThreadExecutorPool() {
        return mThreadExecutorPool;
    }
}
