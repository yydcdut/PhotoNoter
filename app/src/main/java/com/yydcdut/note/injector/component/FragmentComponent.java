package com.yydcdut.note.injector.component;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerFragment;
import com.yydcdut.note.injector.module.FragmentModule;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.views.gallery.impl.MediaPhotoFragment;
import com.yydcdut.note.views.home.impl.AlbumFragment;
import com.yydcdut.note.views.login.impl.UserDetailFragment;
import com.yydcdut.note.views.note.impl.DetailFragment;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerFragment
@Component(modules = FragmentModule.class, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    @ContextLife("Application")
    Context getContext();

    @ContextLife("Activity")
    Context getActivityContext();

    Activity getActivity();

    RxCategory getRxCategory();

    RxPhotoNote getRxPhotoNote();

    RxSandBox getRxSandBox();

    LocalStorageUtils getLocalStorageUtils();

    void inject(AlbumFragment albumFragment);

    void inject(UserDetailFragment userDetailFragment);

    void inject(DetailFragment detailFragment);

    void inject(MediaPhotoFragment mediaPhotoFragment);
}
