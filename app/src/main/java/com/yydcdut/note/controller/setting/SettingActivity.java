package com.yydcdut.note.controller.setting;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.evernote.edam.type.User;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.IUser;
import com.yydcdut.note.camera.controller.AdjustCamera;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.controller.home.HomeActivity;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.ActivityCollector;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.view.ColorChooserDialog;
import com.yydcdut.note.view.RoundedImageView;

import org.json.JSONException;

import java.io.File;
import java.util.List;

/**
 * Created by yuyidong on 15-4-1.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private static final String TAG_THEME = "theme";
    private static final String TAG_STATUS_BAR = "status_bar";
    private static final String TAG_FLOATING = "floating_action_button";
    private static final String TAG_FONT = "font";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_CAMERA2 = "camera2";
    private static final String TAG_CAMERA_SYSTEM = "camera_system";
    private static final String TAG_CAMERA_SIZE = "camera_size";
    private static final String TAG_CAMERA_SAVE = "camera_save";
    private static final String TAG_CAMERA_MIRROR = "camera_mirror";
    private static final String TAG_CAMERA_FIX = "camera_fix";
    private static final String TAG_SYNC_AUTO = "sync_auto";
    private static final String TAG_SYNC_WIFI = "sync_wifi";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_SPLASH = "splash";


    private static final boolean SUPPORT_CAMERA_5_0 = false;
    private static final boolean SUPPORT_WIFI_SYNC = false;
    private static final boolean SUPPORT_AUTO_SYNC = false;

    private Toolbar mToolbar;
    private View mScrollView;
    private LinearLayout mScrollLinear;
    private boolean mIsHiding = false;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
        mScrollView = findViewById(R.id.scroll_setting);
        mScrollLinear = (LinearLayout) findViewById(R.id.layout_scroll_linear);
        initPreferenceSetting();
        initAccountSetting();
        initCameraSetting();
        initSyncSetting();
        initAboutSetting();
    }

    private void toastNotSupport() {
        Toast.makeText(SettingActivity.this, R.string.not_support, Toast.LENGTH_SHORT).show();
    }

    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_setting));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initPreferenceSetting() {
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
        setTag(themeView, TAG_THEME);
        setData(themeView, R.drawable.ic_color_lens_gray_24dp, R.string.theme);
        linearLayout.addView(themeView);

        View statusbarView = getItemView();
        setClick(statusbarView);
        setTag(statusbarView, TAG_STATUS_BAR);
        setData(statusbarView, R.drawable.ic_settings_cell_black_24dp, R.string.status_bar);
        linearLayout.addView(statusbarView);
        if (!LollipopCompat.AFTER_LOLLIPOP) {
            ((TextView) statusbarView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            statusbarView.findViewById(R.id.layout_ripple_setting).setOnClickListener(null);
        }

        View floatingView = getItemView();
        setClick(floatingView);
        setTag(floatingView, TAG_FLOATING);
        setData(floatingView, R.drawable.ic_stars_gray_24dp, R.string.floationg_action_button_style);
        linearLayout.addView(floatingView);

        View viewSort = getItemView();
        setClick(viewSort);
        setTag(viewSort, TAG_FONT);
        setData(viewSort, R.drawable.ic_format_color_text_grey_24dp, R.string.font);
        linearLayout.addView(viewSort);

        View viewFont = getItemView();
        setClick(viewFont);
        setTag(viewFont, TAG_CATEGORY);
        setData(viewFont, R.drawable.ic_format_list_numbered_grey_24dp, R.string.edit_category);
        cancelDivider(viewFont);
        linearLayout.addView(viewFont);
    }

    private void initAccountSetting() {
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
        IUser iQQUser = UserCenter.getInstance().getQQ();
        RoundedImageView imageView1 = (RoundedImageView) accountView.findViewById(R.id.img_item_setting_logo);
        imageView1.setImageResource(R.drawable.ic_person_info_qq);
        TextView textName1 = (TextView) accountView.findViewById(R.id.txt_item_setting_user_name);
        textName1.setText(iQQUser == null ? getResources().getString(R.string.not_login) : iQQUser.getName());
        RoundedImageView imageUser1 = (RoundedImageView) accountView.findViewById(R.id.img_item_setting_user);
        if (iQQUser == null) {
            imageUser1.setImageResource(R.drawable.ic_no_user);
        } else {
            if (new File(FilePathUtils.getQQImagePath()).exists()) {
                ImageLoaderManager.displayImage("file://" + FilePathUtils.getQQImagePath(), imageUser1);
            } else {
                ImageLoaderManager.displayImage(iQQUser.getNetImagePath(), imageUser1);
            }
        }
        linearLayout.addView(accountView);

        View accountView2 = LayoutInflater.from(this).inflate(R.layout.item_setting_account, null);
        RoundedImageView imageView2 = (RoundedImageView) accountView2.findViewById(R.id.img_item_setting_logo);
        imageView2.setImageResource(R.drawable.ic_evernote_fab);
        TextView textName2 = (TextView) accountView2.findViewById(R.id.txt_item_setting_user_name);
        RoundedImageView imageUser2 = (RoundedImageView) accountView2.findViewById(R.id.img_item_setting_user);
        if (UserCenter.getInstance().isLoginEvernote() && UserCenter.getInstance().getEvernote() != null) {
            imageUser2.setImageResource(R.drawable.ic_evernote_color);
            User user = UserCenter.getInstance().getEvernote();
            textName2.setText(user.getUsername());
        } else {
            imageUser2.setImageResource(R.drawable.ic_no_user);
            textName2.setText(getResources().getString(R.string.not_login));
        }
        linearLayout.addView(accountView2);

        View pgView = LayoutInflater.from(this).inflate(R.layout.item_setting_pb, null);
        ProgressBar pg = (ProgressBar) pgView.findViewById(R.id.pg_setting);
        pg.setProgress(0);
        TextView usedView = (TextView) pgView.findViewById(R.id.txt_setting_clound_use);
        linearLayout.addView(pgView);

    }

    private void initCameraSetting() {
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
//        setClick(viewCapture);
        setTag(viewCapture, TAG_CAMERA_SYSTEM);
        setData(viewCapture, R.drawable.ic_photo_camera_grey_24dp, R.string.camera_system);
        initLocalData(viewCapture, LocalStorageUtils.getInstance().getCameraSystem());
        linearLayout.addView(viewCapture);

        final View cameraView = getItemCheckView();
        setClick(cameraView);
        setTag(cameraView, TAG_CAMERA2);
        setData(cameraView, R.drawable.ic_camera_grey_24dp, R.string.camera_5_0);
        initLocalData(cameraView, false);
        linearLayout.addView(cameraView);
        TextView textView2 = (TextView) cameraView.findViewById(R.id.txt_setting);
        textView2.setTextColor(getResources().getColor(R.color.txt_alpha_gray));

        final View viewSize = getItemView();
        setClick(viewSize);
        setTag(viewSize, TAG_CAMERA_SIZE);
        setData(viewSize, R.drawable.ic_crop_original_grey_24dp, R.string.picture_size);
        linearLayout.addView(viewSize);

        final View viewSave = getItemCheckView();
        setClick(viewSave);
        setTag(viewSave, TAG_CAMERA_SAVE);
        setData(viewSave, R.drawable.ic_tune_gray_24dp, R.string.camera_save);
        initLocalData(viewSave, LocalStorageUtils.getInstance().getCameraSaveSetting());
        linearLayout.addView(viewSave);

        final View viewMirror = getItemCheckView();
        setClick(viewMirror);
        setTag(viewMirror, TAG_CAMERA_MIRROR);
        setData(viewMirror, R.drawable.ic_compare_gray_24dp, R.string.camera_mirror);
        initLocalData(viewMirror, LocalStorageUtils.getInstance().getCameraMirrorOpen());
        linearLayout.addView(viewMirror);
        if (LocalStorageUtils.getInstance().getCameraNumber() < 2) {
            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
        }

        final View viewFix = getItemView();
        setClick(viewFix);
        setTag(viewFix, TAG_CAMERA_FIX);
        setData(viewFix, R.drawable.ic_style_grey_24dp, R.string.camera_fix);
        cancelDivider(viewFix);
        linearLayout.addView(viewFix);

        if (LocalStorageUtils.getInstance().getCameraSystem()) {
//            ((TextView) cameraView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
        } else {
//            ((TextView) cameraView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
            if (LocalStorageUtils.getInstance().getCameraNumber() < 2) {
                ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            }
        }

        viewCapture.findViewById(R.id.layout_setting_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean use = LocalStorageUtils.getInstance().getCameraSystem();
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.cb_setting);
                if (use) {
                    checkBox.setChecked(false);
                    LocalStorageUtils.getInstance().setCameraSystem(false);
//                    ((TextView) cameraView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
                    ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
                    ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
                    ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
                    ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_gray));
                    if (LocalStorageUtils.getInstance().getCameraNumber() < 2) {
                        ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                    }
                } else {
                    checkBox.setChecked(true);
                    LocalStorageUtils.getInstance().setCameraSystem(true);
//                    ((TextView) cameraView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                    ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                    ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                    ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                    ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
                }
            }
        });

        if (LocalStorageUtils.getInstance().getCameraNumber() == 0) {
            ((TextView) viewCapture.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) cameraView.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewSize.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewSave.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewMirror.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            ((TextView) viewFix.findViewById(R.id.txt_setting)).setTextColor(getResources().getColor(R.color.txt_alpha_gray));
            viewCapture.findViewById(R.id.layout_setting_click).setOnClickListener(null);
            viewSize.setOnClickListener(null);
            viewSave.setOnClickListener(null);
            viewMirror.setOnClickListener(null);
            viewFix.setOnClickListener(null);
        }

    }

    private void initSyncSetting() {
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
        setTag(viewAuto, TAG_SYNC_AUTO);
        setData(viewAuto, R.drawable.ic_cloud_circle_grey_24dp, R.string.sync_auto);
        initLocalData(viewAuto, false);
        linearLayout.addView(viewAuto);
        TextView textView1 = (TextView) viewAuto.findViewById(R.id.txt_setting);
        textView1.setTextColor(getResources().getColor(R.color.txt_alpha_gray));

        View viewWifi = getItemCheckView();
        setClick(viewWifi);
        setTag(viewWifi, TAG_SYNC_WIFI);
        setData(viewWifi, R.drawable.ic_network_wifi_grey_24dp, R.string.sync_wifi);
        initLocalData(viewWifi, false);
        cancelDivider(viewWifi);
        linearLayout.addView(viewWifi);
        TextView textView2 = (TextView) viewWifi.findViewById(R.id.txt_setting);
        textView2.setTextColor(getResources().getColor(R.color.txt_alpha_gray));

    }

    private void initAboutSetting() {
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
        setTag(viewSplash, TAG_SPLASH);
        setData(viewSplash, R.drawable.ic_send_grey_24dp, R.string.splash);
        initLocalData(viewSplash, !LocalStorageUtils.getInstance().getSplashOpen());
        linearLayout.addView(viewSplash);

        View viewAboutApp = getItemView();
        setClick(viewAboutApp);
        setTag(viewAboutApp, TAG_ABOUT);
        setData(viewAboutApp, R.drawable.ic_info_grey_24dp, R.string.about_app);
        cancelDivider(viewAboutApp);
        linearLayout.addView(viewAboutApp);
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
                ObjectAnimator.ofFloat(mToolbar, "translationY", -actionBarHeight, 0),
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
        String tag = (String) v.getTag();
        switch (tag) {
            case TAG_THEME:
                showThemeColorChooser();
                break;
            case TAG_STATUS_BAR:
                showStatusBarStyleDialog();
                break;
            case TAG_FLOATING:
                startActivity(new Intent(this, FloatingEditActivity.class));
                break;
            case TAG_FONT:
                showFontChooser();
                break;
            case TAG_CATEGORY:
                Intent intent = new Intent(SettingActivity.this, EditCategoryActivity.class);
                startActivity(intent);
                break;
            case TAG_CAMERA2:
                boolean use = LocalStorageUtils.getInstance().getCameraSystem();
                if ((!LollipopCompat.AFTER_LOLLIPOP || !SUPPORT_CAMERA_5_0) && !use) {
                    toastNotSupport();
                    return;
                }
                break;
            case TAG_CAMERA_SIZE:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                try {
                    pictureSizeDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case TAG_CAMERA_SAVE:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                boolean isSave = LocalStorageUtils.getInstance().getCameraSaveSetting();
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.cb_setting);
                if (isSave) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                LocalStorageUtils.getInstance().setCameraSaveSetting(!isSave);
                break;
            case TAG_CAMERA_MIRROR:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    break;
                }
                boolean open = LocalStorageUtils.getInstance().getCameraMirrorOpen();
                CheckBox checkBoxMirror = (CheckBox) v.findViewById(R.id.cb_setting);
                if (open) {
                    checkBoxMirror.setChecked(false);
                } else {
                    checkBoxMirror.setChecked(true);
                }
                LocalStorageUtils.getInstance().setCameraMirrorOpen(!open);
                break;
            case TAG_CAMERA_FIX:
                startActivity(new Intent(this, AdjustCamera.class));
                break;
            case TAG_SYNC_AUTO:
            case TAG_SYNC_WIFI:
                toastNotSupport();
                break;
            case TAG_SPLASH:
                boolean splashOpen = LocalStorageUtils.getInstance().getSplashOpen();
                CheckBox checkBoxSplash = (CheckBox) v.findViewById(R.id.cb_setting);
                if (!splashOpen) {
                    checkBoxSplash.setChecked(false);
                } else {
                    checkBoxSplash.setChecked(true);
                }
                LocalStorageUtils.getInstance().setSplashOpen(!splashOpen);
                break;
            case TAG_ABOUT:
                startActivity(new Intent(this, AboutAppActivity.class));
                break;
        }
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
                ObjectAnimator.ofFloat(mToolbar, "translationY", 0, -actionBarHeight),
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

    /**
     * Theme的dialog
     */
    private void showThemeColorChooser() {
        new ColorChooserDialog().show(this, LocalStorageUtils.getInstance().getThemeColor(), new ColorChooserDialog.Callback() {
            @Override
            public void onColorSelection(int index, int color, int darker) {
                LocalStorageUtils.getInstance().setThemeColor(index);
                ActivityCollector.reStart(SettingActivity.this, HomeActivity.class, SettingActivity.class);
            }
        });
    }

    private AlertDialog mFontDialog;

    /**
     * 排序的dialog
     */
    private void showFontChooser() {
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

    private void showStatusBarStyleDialog() {
        new AlertDialog.Builder(this, R.style.note_dialog)
                .setTitle(R.string.status_bar)
                .setCancelable(true)
                .setItems(new String[]{
                                getResources().getString(R.string.status_bar_immersive),
                                getResources().getString(R.string.status_bar_translation)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        LocalStorageUtils.getInstance().setStatusBarTranslation(false);
                                        break;
                                    case 1:
                                        LocalStorageUtils.getInstance().setStatusBarTranslation(true);
                                        break;
                                }
                                ActivityCollector.reStart(SettingActivity.this, HomeActivity.class, SettingActivity.class);
                            }
                        }).show();
    }

    private View.OnClickListener mFontDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_dialog_font_personal:
                    LocalStorageUtils.getInstance().setSettingFontSystem(false);
                    break;
                case R.id.txt_dialog_font_system:
                    LocalStorageUtils.getInstance().setSettingFontSystem(true);
                    break;
            }
            mFontDialog.dismiss();
        }
    };

    /**
     * 选择摄像头的dialog
     *
     * @throws JSONException
     */
    private void pictureSizeDialog() throws JSONException {
        int total = LocalStorageUtils.getInstance().getCameraNumber();
        if (total == 2) {
            String[] items = new String[]{getResources().getString(R.string.camera_back), getResources().getString(R.string.camera_front)};
            new AlertDialog.Builder(SettingActivity.this, R.style.note_dialog)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                if (which == 0) {
                                    choosePictureSizeDialog(Const.CAMERA_BACK);
                                } else {
                                    choosePictureSizeDialog(Const.CAMERA_FRONT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                YLog.i(TAG, e.getMessage() + "");
                            }
                        }
                    })
                    .show();
        } else {
            choosePictureSizeDialog(Const.CAMERA_BACK);
        }
    }

    /**
     * 选择照片尺寸dialog
     *
     * @param cameraId
     * @throws JSONException
     */
    private void choosePictureSizeDialog(final String cameraId) throws JSONException {
        final List<Size> list = LocalStorageUtils.getInstance().getPictureSizes(cameraId);
        Size targetSize = LocalStorageUtils.getInstance().getPictureSize(cameraId);
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
                        Size size = list.get(which);
                        try {
                            LocalStorageUtils.getInstance().setPictureSize(cameraId, size);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            YLog.i(TAG, e.getMessage() + "");
                        }
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
