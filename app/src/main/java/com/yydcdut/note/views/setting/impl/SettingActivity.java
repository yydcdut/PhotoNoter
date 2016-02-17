package com.yydcdut.note.views.setting.impl;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.setting.IFeedbackPresenter;
import com.yydcdut.note.presenters.setting.ISettingPresenter;
import com.yydcdut.note.presenters.setting.impl.SettingPresenterImpl;
import com.yydcdut.note.utils.ActivityCollector;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.camera.param.Size;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.camera.impl.AdjustCameraActivity;
import com.yydcdut.note.views.home.impl.HomeActivity;
import com.yydcdut.note.views.setting.ISettingView;
import com.yydcdut.note.widget.ColorChooserDialog;
import com.yydcdut.note.widget.RoundedImageView;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15-4-1.
 */
public class SettingActivity extends BaseActivity implements ISettingView, View.OnClickListener {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private Map<String, View> mViewMap;

    @Bind(R.id.scroll_setting)
    View mScrollView;
    @Bind(R.id.layout_scroll_linear)
    LinearLayout mScrollLinear;
    @Bind(R.id.layout_toolbar)
    View mToolbarLayout;

    @Inject
    SettingPresenterImpl mSettingPresenter;

    private boolean mIsHiding = false;

    private AlertDialog mFontDialog;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mSettingPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mViewMap = new HashMap<>();
        initToolBarUI();
        mSettingPresenter.attachView(this);
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_setting));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        AppCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    @Override
    public void initPreferenceSetting() {
        //cardView
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_setting_card_top, null);
        LinearLayout linearLayout = (LinearLayout) cardView.findViewById(R.id.layout_setting_linear);
        mScrollLinear.addView(cardView);
        View divider = LayoutInflater.from(this).inflate(R.layout.item_setting_divider, null);
        mScrollLinear.addView(divider);

        //title
        View labelView = LayoutInflater.from(this).inflate(R.layout.item_setting_label, null);
        ((TextView) labelView.findViewById(R.id.txt_setting_title)).setText(getResources().getString(R.string.preference_setting));
        linearLayout.addView(labelView);

        View themeView = getItemView();
        setClick(themeView);
        setTag(themeView, ISettingPresenter.TAG_THEME);
        setData(themeView, R.drawable.ic_color_lens_gray_24dp, R.string.theme);
        linearLayout.addView(themeView);

        View statusBarView = getItemView();
        setClick(statusBarView);
        setTag(statusBarView, ISettingPresenter.TAG_STATUS_BAR);
        setData(statusBarView, R.drawable.ic_settings_cell_black_24dp, R.string.status_bar);
        linearLayout.addView(statusBarView);

//        View floatingView = getItemView();
//        setClick(floatingView);
//        setTag(floatingView, ISettingPresenter.TAG_FLOATING);
//        setData(floatingView, R.drawable.ic_stars_gray_24dp, R.string.floationg_action_button_style);
//        linearLayout.addView(floatingView);

        View viewSort = getItemView();
        setClick(viewSort);
        setTag(viewSort, ISettingPresenter.TAG_FONT);
        setData(viewSort, R.drawable.ic_format_color_text_grey_24dp, R.string.font);
        linearLayout.addView(viewSort);

        View viewFont = getItemView();
        setClick(viewFont);
        setTag(viewFont, ISettingPresenter.TAG_CATEGORY);
        setData(viewFont, R.drawable.ic_format_list_numbered_grey_24dp, R.string.edit_category);
        cancelDivider(viewFont);
        linearLayout.addView(viewFont);
    }

    @Override
    public void setStatusBarClickable(boolean clickable) {
        View statusBarView = mViewMap.get(ISettingPresenter.TAG_STATUS_BAR);
        if (!clickable) {
            ((TextView) statusBarView.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            statusBarView.findViewById(R.id.layout_ripple_setting).setOnClickListener(null);
        }
    }

    @Override
    public void initAccountSetting() {
        //cardView
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_setting_card, null);
        LinearLayout linearLayout = (LinearLayout) cardView.findViewById(R.id.layout_setting_linear);
        mScrollLinear.addView(cardView);
        View divider = LayoutInflater.from(this).inflate(R.layout.item_setting_divider, null);
        mScrollLinear.addView(divider);
        //title
        View labelView = LayoutInflater.from(this).inflate(R.layout.item_setting_label, null);
        ((TextView) labelView.findViewById(R.id.txt_setting_title)).setText(getResources().getString(R.string.account_setting));
        linearLayout.addView(labelView);
        //QQ
        View accountView = LayoutInflater.from(this).inflate(R.layout.item_setting_account, null);
        mViewMap.put(ISettingPresenter.TAG_QQ, accountView);
        RoundedImageView imageView1 = (RoundedImageView) accountView.findViewById(R.id.img_item_setting_logo);
        imageView1.setImageResource(R.drawable.ic_person_info_qq);
        RoundedImageView imageUser1 = (RoundedImageView) accountView.findViewById(R.id.img_item_setting_user);
        imageUser1.setImageResource(R.drawable.ic_no_user);
        TextView textName = (TextView) accountView.findViewById(R.id.txt_item_setting_user_name);
        textName.setText(getResources().getString(R.string.not_login));
        linearLayout.addView(accountView);
        //evernote
        View accountView2 = LayoutInflater.from(this).inflate(R.layout.item_setting_account, null);
        mViewMap.put(ISettingPresenter.TAG_EVERNOTE, accountView2);
        RoundedImageView imageView2 = (RoundedImageView) accountView2.findViewById(R.id.img_item_setting_logo);
        imageView2.setImageResource(R.drawable.ic_evernote_fab);
        TextView textName2 = (TextView) accountView2.findViewById(R.id.txt_item_setting_user_name);
        RoundedImageView imageUser2 = (RoundedImageView) accountView2.findViewById(R.id.img_item_setting_user);
        imageUser2.setImageResource(R.drawable.ic_no_user);
        textName2.setText(getResources().getString(R.string.not_login));
        linearLayout.addView(accountView2);
        //cloud
        View pgView = LayoutInflater.from(this).inflate(R.layout.item_setting_pb, null);
        ProgressBar pg = (ProgressBar) pgView.findViewById(R.id.pg_setting);
        pg.setProgress(0);
        TextView usedView = (TextView) pgView.findViewById(R.id.txt_setting_clound_use);
        linearLayout.addView(pgView);
    }

    @Override
    public void initQQ(boolean isLogin, String name, String imagePath) {
        View accountView = mViewMap.get(ISettingPresenter.TAG_QQ);
        TextView textName1 = (TextView) accountView.findViewById(R.id.txt_item_setting_user_name);
        textName1.setText(isLogin ? name : getResources().getString(R.string.not_login));
        RoundedImageView imageUser1 = (RoundedImageView) accountView.findViewById(R.id.img_item_setting_user);
        if (isLogin) {
            ImageLoaderManager.displayImage(imagePath, imageUser1);
        }
    }

    @Override
    public void initEvernote(boolean isLogin, String name) {
        View accountView2 = mViewMap.get(ISettingPresenter.TAG_EVERNOTE);
        TextView textName2 = (TextView) accountView2.findViewById(R.id.txt_item_setting_user_name);
        RoundedImageView imageUser2 = (RoundedImageView) accountView2.findViewById(R.id.img_item_setting_user);
        imageUser2.setImageResource(R.drawable.ic_no_user);
        textName2.setText(getResources().getString(R.string.not_login));
        if (isLogin) {
            imageUser2.setImageResource(R.drawable.ic_evernote_color);
            textName2.setText(name);
        }
    }

    @Override
    public void initCameraSetting(boolean isSystem, int cameraNumbers) {
        //cardView
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_setting_card, null);
        LinearLayout linearLayout = (LinearLayout) cardView.findViewById(R.id.layout_setting_linear);
        mScrollLinear.addView(cardView);
        View divider = LayoutInflater.from(this).inflate(R.layout.item_setting_divider, null);
        mScrollLinear.addView(divider);
        //title
        View labelView = LayoutInflater.from(this).inflate(R.layout.item_setting_label, null);
        ((TextView) labelView.findViewById(R.id.txt_setting_title)).setText(getResources().getString(R.string.camera_setting));
        linearLayout.addView(labelView);

        View viewCapture = getItemCheckView();
        setClick(viewCapture);
        setTag(viewCapture, ISettingPresenter.TAG_CAMERA_SYSTEM);
        setData(viewCapture, R.drawable.ic_photo_camera_grey_24dp, R.string.camera_system);
        initLocalData(viewCapture, mSettingPresenter.getCameraSystem());
        linearLayout.addView(viewCapture);

        final View camera2View = getItemCheckView();
        setClick(camera2View);
        setTag(camera2View, ISettingPresenter.TAG_CAMERA2);
        setData(camera2View, R.drawable.ic_camera_grey_24dp, R.string.camera_5_0);
        initLocalData(camera2View, mSettingPresenter.getCameraAndroidLollipop());
        linearLayout.addView(camera2View);

        final View viewSize = getItemView();
        setClick(viewSize);
        setTag(viewSize, ISettingPresenter.TAG_CAMERA_SIZE);
        setData(viewSize, R.drawable.ic_crop_original_grey_24dp, R.string.picture_size);
        linearLayout.addView(viewSize);

        final View viewSave = getItemCheckView();
        setClick(viewSave);
        setTag(viewSave, ISettingPresenter.TAG_CAMERA_SAVE);
        setData(viewSave, R.drawable.ic_tune_gray_24dp, R.string.camera_save);
        initLocalData(viewSave, mSettingPresenter.getCameraSaveSetting());
        linearLayout.addView(viewSave);

        final View viewMirror = getItemCheckView();
        setClick(viewMirror);
        setTag(viewMirror, ISettingPresenter.TAG_CAMERA_MIRROR);
        setData(viewMirror, R.drawable.ic_compare_gray_24dp, R.string.camera_mirror);
        initLocalData(viewMirror, mSettingPresenter.getCameraMirrorOpen());
        linearLayout.addView(viewMirror);
//        if (cameraNumbers < 2) {
        ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
//        }

        final View viewFix = getItemView();
        setClick(viewFix);
        setTag(viewFix, ISettingPresenter.TAG_CAMERA_FIX);
        setData(viewFix, R.drawable.ic_style_grey_24dp, R.string.camera_fix);
        cancelDivider(viewFix);
        linearLayout.addView(viewFix);

        if (isSystem) {
            ((TextView) camera2View.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
//            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray,this));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
        } else {
            ((TextView) camera2View.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
//            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray,this));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
//            if (mSettingPresenter.getCameraNumber() < 2) {
//                ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray));
//            }
        }

        if (cameraNumbers == 0) {
            ((TextView) viewCapture.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
//        ((TextView) cameraViwq.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray,this));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            viewCapture.setOnClickListener(null);
//        cameraViwe.setOnClickListener(null);
            viewSize.setOnClickListener(null);
            viewSave.setOnClickListener(null);
            viewMirror.setOnClickListener(null);
            viewFix.setOnClickListener(null);
        }

    }

    @Override
    public void setCameraSettingClickable(boolean isSystem, int cameraNumbers) {
        View viewCapture = mViewMap.get(ISettingPresenter.TAG_CAMERA_SYSTEM);
        CheckBox checkBox = (CheckBox) viewCapture.findViewById(R.id.cb_setting);
        checkBox.setChecked(isSystem);
        if (isSystem) {
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA2).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_SIZE).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_SAVE).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
//            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_MIRROR).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray,this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_FIX).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));
        } else {
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA2).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_SIZE).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_SAVE).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
//            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_MIRROR).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray,this));
            ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_FIX).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_gray, this));
//            if (cameraNumbers < 2) {
//                ((TextView) mViewMap.get(ISettingPresenter.TAG_CAMERA_MIRROR).findViewById(R.id.txt_setting)).setTextColor(AppCompat.getColor(R.color.txt_alpha_gray,this));
//            }
        }
    }

    @Override
    public void initSyncSetting(boolean syncAuto, boolean wifi) {
        //cardView
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_setting_card, null);
        LinearLayout linearLayout = (LinearLayout) cardView.findViewById(R.id.layout_setting_linear);
        mScrollLinear.addView(cardView);
        View divider = LayoutInflater.from(this).inflate(R.layout.item_setting_divider, null);
        mScrollLinear.addView(divider);
        //title
        View labelView = LayoutInflater.from(this).inflate(R.layout.item_setting_label, null);
        ((TextView) labelView.findViewById(R.id.txt_setting_title)).setText(getResources().getString(R.string.sync_setting));
        linearLayout.addView(labelView);

        View viewAuto = getItemCheckView();
        setClick(viewAuto);
        setTag(viewAuto, ISettingPresenter.TAG_SYNC_AUTO);
        setData(viewAuto, R.drawable.ic_cloud_circle_grey_24dp, R.string.sync_auto);
        initLocalData(viewAuto, syncAuto);
        linearLayout.addView(viewAuto);
        TextView textView1 = (TextView) viewAuto.findViewById(R.id.txt_setting);
        textView1.setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));

        View viewWifi = getItemCheckView();
        setClick(viewWifi);
        setTag(viewWifi, ISettingPresenter.TAG_SYNC_WIFI);
        setData(viewWifi, R.drawable.ic_network_wifi_grey_24dp, R.string.sync_wifi);
        initLocalData(viewWifi, wifi);
        cancelDivider(viewWifi);
        linearLayout.addView(viewWifi);
        TextView textView2 = (TextView) viewWifi.findViewById(R.id.txt_setting);
        textView2.setTextColor(AppCompat.getColor(R.color.txt_alpha_gray, this));

    }

    @Override
    public void initAboutSetting() {
        //cardView
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_setting_card, null);
        LinearLayout linearLayout = (LinearLayout) cardView.findViewById(R.id.layout_setting_linear);
        mScrollLinear.addView(cardView);
        View divider = LayoutInflater.from(this).inflate(R.layout.item_setting_divider, null);
        mScrollLinear.addView(divider);
        //title
        View labelView = LayoutInflater.from(this).inflate(R.layout.item_setting_label, null);
        ((TextView) labelView.findViewById(R.id.txt_setting_title)).setText(getResources().getString(R.string.about_setting));
        linearLayout.addView(labelView);

        View viewSplash = getItemCheckView();
        setClick(viewSplash);
        setTag(viewSplash, ISettingPresenter.TAG_SPLASH);
        setData(viewSplash, R.drawable.ic_send_grey_24dp, R.string.splash);
        initLocalData(viewSplash, !mSettingPresenter.getSplashOpen());
        linearLayout.addView(viewSplash);

        View viewFeedback = getItemView();
        setClick(viewFeedback);
        setTag(viewFeedback, ISettingPresenter.TAG_FEEDBACK);
        setData(viewFeedback, R.drawable.ic_inbox_gray_24dp, R.string.feedback);
        linearLayout.addView(viewFeedback);

        View viewAboutApp = getItemView();
        setClick(viewAboutApp);
        setTag(viewAboutApp, ISettingPresenter.TAG_ABOUT);
        setData(viewAboutApp, R.drawable.ic_info_grey_24dp, R.string.about_app);
        cancelDivider(viewAboutApp);
        linearLayout.addView(viewAboutApp);
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(mScrollView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void restartActivity() {
        ActivityCollector.reStart(SettingActivity.this, HomeActivity.class, SettingActivity.class);
    }

    @Override
    public void setCheckBoxState(String tag, boolean check) {
        CheckBox checkBox = (CheckBox) mViewMap.get(tag).findViewById(R.id.cb_setting);
        checkBox.setChecked(check);
    }

    @Override
    public void jump2EditCategoryActivity() {
        Intent intent = new Intent(SettingActivity.this, EditCategoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void jump2CameraFixActivity() {
        startActivity(new Intent(this, AdjustCameraActivity.class));
    }

    @Override
    public void jump2FeedbackActivity() {
        Intent feedbackIntent = new Intent(this, FeedbackActivity.class);
        feedbackIntent.putExtra(IFeedbackPresenter.TYPE, IFeedbackPresenter.TYPE_FEEDBACK);
        startActivity(feedbackIntent);
    }

    @Override
    public void jump2AboutActivity() {
        startActivity(new Intent(this, AboutAppActivity.class));
    }

    @Override
    public void showCamera2Gray() {
        View camera2View = mViewMap.get(ISettingPresenter.TAG_CAMERA2);
        TextView textView2 = (TextView) camera2View.findViewById(R.id.txt_setting);
        textView2.setTextColor(getResources().getColor(R.color.txt_alpha_gray));
    }

    @Override
    public void startActivityAnimation() {
        int actionBarHeight = getActionBarSize();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int contentHeight = screenHeight - actionBarHeight;
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(mToolbarLayout, "translationY", -actionBarHeight, 0),
                ObjectAnimator.ofFloat(mScrollView, "translationY", contentHeight, 0)
        );
        animation.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityAnimation();
                break;
        }
        return true;
    }


    @Override
    public void onClick(final View v) {
        mSettingPresenter.onClickSettingItem((String) v.getTag());
    }

    private void closeActivityAnimation() {
        int actionBarHeight = getActionBarSize();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int contentHeight = screenHeight - actionBarHeight;
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(mToolbarLayout, "translationY", 0, -actionBarHeight),
                ObjectAnimator.ofFloat(mScrollView, "translationY", 0, contentHeight)
        );
        animation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsHiding = false;
                finish();
                overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    @Override
    public void showThemeColorChooser(int index) {
        new ColorChooserDialog().show(this, index, new ColorChooserDialog.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                mSettingPresenter.onThemeSelected(index);
            }
        });
    }

    @Override
    public void showFontChooser() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_font, null);
        v.findViewById(R.id.txt_dialog_font_system).setOnClickListener(mFontDialogClickListener);
        v.findViewById(R.id.txt_dialog_font_personal).setOnClickListener(mFontDialogClickListener);
        if (mFontDialog == null) {
            mFontDialog = new AlertDialog.Builder(this, R.style.note_dialog)
                    .setView(v)
                    .setTitle(R.string.font_choose)
                    .setCancelable(true)
                    .show();
        } else {
            mFontDialog.show();
        }
    }

    private View.OnClickListener mFontDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            boolean use = v.getId() == R.id.txt_dialog_font_personal ? false : true;
            mSettingPresenter.onUseSystemFontSelected(use);
            mFontDialog.dismiss();
        }
    };

    @Override
    public void showStatusBarStyleChooser() {
        new AlertDialog.Builder(this, R.style.note_dialog)
                .setTitle(R.string.status_bar)
                .setCancelable(true)
                .setItems(new String[]{
                                getResources().getString(R.string.status_bar_immersive),
                                getResources().getString(R.string.status_bar_translation)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean translate = which == 0 ? false : true;
                                mSettingPresenter.onStatusBarStyleSelected(translate);
                            }
                        }).show();
    }

    @Override
    public void showCameraIdsChooser() throws JSONException {
        String[] items = new String[]{getResources().getString(R.string.camera_back), getResources().getString(R.string.camera_front)};
        new AlertDialog.Builder(SettingActivity.this, R.style.note_dialog)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mSettingPresenter.onCameraIdsSelected(which);
                    }
                })
                .show();
    }

    @Override
    public void showPictureSizeChooser(final String cameraId, List<Size> list, Size targetSize) throws JSONException {
        String[] sizeArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            if (targetSize.equals(list.get(i))) {
                sizeArray[i] = targetSize.toString() + getResources().getString(R.string.camera_current_picture_size);
                continue;
            }
            sizeArray[i] = list.get(i).toString();
        }
        new AlertDialog.Builder(SettingActivity.this, R.style.note_dialog)
                .setItems(sizeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSettingPresenter.onPictureSizeSelected(cameraId, which);
                    }
                })
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mIsHiding) {
            mIsHiding = true;
            closeActivityAnimation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View getItemView() {
        return LayoutInflater.from(this).inflate(R.layout.item_setting, null);
    }

    private View getItemCheckView() {
        return LayoutInflater.from(this).inflate(R.layout.item_setting_check, null);
    }

    private void setClick(View v) {
        v.findViewById(R.id.layout_ripple_setting).setOnClickListener(this);
    }

    private void setTag(View v, String tag) {
        v.findViewById(R.id.layout_setting_click).setTag(tag);
        mViewMap.put(tag, v);
    }

    private void setData(View v, int drawable, int string) {
        ImageView imageView1 = (ImageView) v.findViewById(R.id.img_setting);
        imageView1.setImageResource(drawable);
        TextView textView1 = (TextView) v.findViewById(R.id.txt_setting);
        textView1.setText(getResources().getString(string));
    }

    private void initLocalData(View v, boolean isChecked) {
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.cb_setting);
        if (isChecked) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    private void cancelDivider(View v) {
        v.findViewById(R.id.view_divider).setVisibility(View.INVISIBLE);
    }
}
