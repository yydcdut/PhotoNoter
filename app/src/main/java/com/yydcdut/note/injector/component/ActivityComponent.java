package com.yydcdut.note.injector.component;

import android.app.Activity;
import android.content.Context;

import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.injector.PerActivity;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxSandBox;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.views.camera.impl.AdjustCameraActivity;
import com.yydcdut.note.views.camera.impl.CameraActivity;
import com.yydcdut.note.views.gallery.impl.GalleryActivity;
import com.yydcdut.note.views.gallery.impl.PhotoDetailActivity;
import com.yydcdut.note.views.home.impl.HomeActivity;
import com.yydcdut.note.views.home.impl.IntroduceActivity;
import com.yydcdut.note.views.home.impl.SplashActivity;
import com.yydcdut.note.views.login.impl.LoginActivity;
import com.yydcdut.note.views.login.impl.UserCenterActivity;
import com.yydcdut.note.views.note.impl.DetailActivity;
import com.yydcdut.note.views.note.impl.EditTextActivity;
import com.yydcdut.note.views.note.impl.MapActivity;
import com.yydcdut.note.views.note.impl.ZoomActivity;
import com.yydcdut.note.views.setting.impl.AboutAppActivity;
import com.yydcdut.note.views.setting.impl.EditCategoryActivity;
import com.yydcdut.note.views.setting.impl.FeedbackActivity;
import com.yydcdut.note.views.setting.impl.SettingActivity;

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

    RxCategory getRxCategory();

    RxPhotoNote getRxPhotoNote();

    RxSandBox getRxSandBox();

    LocalStorageUtils getLocalStorageUtils();

    void inject(HomeActivity homeActivity);

    void inject(IntroduceActivity introduceActivity);

    void inject(SplashActivity splashActivity);

    void inject(LoginActivity loginActivity);

    void inject(UserCenterActivity userCenterActivity);

    void inject(DetailActivity detailActivity);

    void inject(MapActivity mapActivity);

    void inject(EditTextActivity editTextActivity);

    void inject(ZoomActivity zoomActivity);

    void inject(AboutAppActivity aboutAppActivity);

    void inject(EditCategoryActivity editCategoryActivity);

    void inject(FeedbackActivity feedbackActivity);

    void inject(SettingActivity settingActivity);

    void inject(CameraActivity cameraActivity);

    void inject(AdjustCameraActivity adjustCameraActivity);

    void inject(GalleryActivity galleryActivity);

    void inject(PhotoDetailActivity photoDetailActivity);

}
