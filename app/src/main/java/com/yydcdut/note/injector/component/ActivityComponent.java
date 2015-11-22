package com.yydcdut.note.injector.component;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerActivity;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.mvp.v.home.impl.HomeActivity;
import com.yydcdut.note.mvp.v.home.impl.IntroduceActivity;
import com.yydcdut.note.mvp.v.home.impl.SplashActivity;
import com.yydcdut.note.mvp.v.login.impl.LoginActivity;
import com.yydcdut.note.mvp.v.login.impl.UserCenterActivity;
import com.yydcdut.note.mvp.v.note.impl.DetailActivity;
import com.yydcdut.note.mvp.v.note.impl.EditTextActivity;
import com.yydcdut.note.mvp.v.note.impl.ZoomActivity;
import com.yydcdut.note.mvp.v.setting.impl.AboutAppActivity;
import com.yydcdut.note.mvp.v.setting.impl.EditCategoryActivity;
import com.yydcdut.note.mvp.v.setting.impl.FeedbackActivity;
import com.yydcdut.note.mvp.v.setting.impl.SettingActivity;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {

    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    CategoryDBModel getCategoryDBModel();

    PhotoNoteDBModel getPhotoNoteDBModel();

    SandBoxDBModel getSandBoxDBModel();

    UserCenter getUserCenter();

    LocalStorageUtils getLocalStorageUtils();

    ThreadExecutorPool getThreadExecutorPool();

    void inject(HomeActivity homeActivity);

    void inject(IntroduceActivity introduceActivity);

    void inject(SplashActivity splashActivity);

    void inject(LoginActivity loginActivity);

    void inject(UserCenterActivity userCenterActivity);

    void inject(DetailActivity detailActivity);

    void inject(EditTextActivity editTextActivity);

    void inject(ZoomActivity zoomActivity);

    void inject(AboutAppActivity aboutAppActivity);

    void inject(EditCategoryActivity editCategoryActivity);

    void inject(FeedbackActivity feedbackActivity);

    void inject(SettingActivity settingActivity);
}
