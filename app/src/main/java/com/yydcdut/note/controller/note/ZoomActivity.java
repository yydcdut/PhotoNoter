package com.yydcdut.note.controller.note;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.TimeDecoder;
import com.yydcdut.note.utils.UiHelper;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.ZoomImageView;

import java.io.IOException;

import us.pinguo.edit.sdk.PGEditActivity;
import us.pinguo.edit.sdk.base.PGEditResult;
import us.pinguo.edit.sdk.base.PGEditSDK;


/**
 * Created by yyd on 15-4-19.
 */
public class ZoomActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private static final String TAG = ZoomActivity.class.getSimpleName();
    /* UI */
    private Toolbar mToolbar;
    private ZoomImageView mImage;
    private View mSpreadView;
    /* Progress Bar */
    private CircleProgressBarLayout mProgressLayout;
    /* 数据 */
    private int mPosition;
    private PhotoNote mPhotoNote;
    private int mComparator;
    /* Handler */
    private Handler mMainHandler = new Handler(this);
    private static final int MSG_UPDATE_DATA = 100;
    /* 图片有没有修改过 */
    private boolean mIsChanged = false;

    /**
     * 启动Activity
     *
     * @param fragment
     * @param categoryLabel
     * @param photoNotePosition
     * @param comparator
     */
    public static void startActivityForResult(Fragment fragment, String categoryLabel, int photoNotePosition, int comparator) {
        Intent intent = new Intent(fragment.getContext(), ZoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, categoryLabel);
        bundle.putInt(Const.PHOTO_POSITION, photoNotePosition);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, REQUEST_NOTHING);
    }

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_zoom;
    }

    @Override
    public void initUiAndListener() {
        Bundle bundle = getIntent().getExtras();
        String category = bundle.getString(Const.CATEGORY_LABEL);
        mPosition = bundle.getInt(Const.PHOTO_POSITION);
        mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
        mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
        try {
            initImageView(mPhotoNote.getBigPhotoPathWithFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initToolBarUI();
        initProgress();
    }

    private void initImageView(String path) throws IOException {
        mImage = (ZoomImageView) findViewById(R.id.img_zoom);
        ImageLoaderManager.displayImage(path, mImage);
        mSpreadView = findViewById(R.id.img_zoom_spread);
        mSpreadView.setOnClickListener(this);

//        ExifInterface exifInterface = new ExifInterface(mPhotoNote.getBigPhotoPathWithoutFile());
//        String FFNumber = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
//        YLog.i("yuyidong", "FFNumber--->" + FFNumber);
//        String FDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
//        YLog.i("yuyidong", "FDateTime--->" + FDateTime);
//        String FExposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
//        YLog.i("yuyidong", "FExposureTime--->" + FExposureTime);
//        String FFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
//        YLog.i("yuyidong", "FFlash--->" + FFlash);
//        String FFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
//        YLog.i("yuyidong", "FFocalLength--->" + FFocalLength);
//        String FImageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
//        YLog.i("yuyidong", "FImageLength--->" + FImageLength);
//        String FImageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
//        YLog.i("yuyidong", "FImageWidth--->" + FImageWidth);
//        String FISOSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
//        YLog.i("yuyidong", "FISOSpeedRatings--->" + FISOSpeedRatings);
//        String FMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
//        YLog.i("yuyidong", "FMake--->" + FMake);
//        String FModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
//        YLog.i("yuyidong", "FModel--->" + FModel);
//        String FOrientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//        YLog.i("yuyidong", "FOrientation--->" + FOrientation);
//        String FWhiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
//        YLog.i("yuyidong", "FWhiteBalance--->" + FWhiteBalance);
//        String ALTITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
//        YLog.i("yuyidong", "ALTITUDE--->" + ALTITUDE);
//        String LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//        YLog.i("yuyidong", "LATITUDE--->" + LATITUDE);

    }

    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setOnMenuItemClickListener(onToolBarMenuItemClick);
        mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mToolbar.setTitle("");
    }

    private void initProgress() {
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zoom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsChanged) {
                    setResult(RESULT_PICTURE);
                    finish();
                } else {
                    finish();
                }
                break;
        }
        return true;
    }

    private Toolbar.OnMenuItemClickListener onToolBarMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_spread:
                    hideToolBar();
                    break;
                case R.id.menu_edit:
                    break;
                case R.id.menu_effect:
                    PGEditSDK.instance().startEdit(ZoomActivity.this, PGEditActivity.class, mPhotoNote.getBigPhotoPathWithoutFile(), mPhotoNote.getBigPhotoPathWithoutFile());
                    break;
                case R.id.menu_trash:
                    break;
                case R.id.menu_info:
                    int[] arr = FilePathUtils.getPictureSize(mPhotoNote.getBigPhotoPathWithoutFile());
                    final int width = arr[0];
                    final int height = arr[1];
                    String[] array = new String[]{
                            getResources().getString(R.string.file_name) + mPhotoNote.getPhotoName(),
                            getResources().getString(R.string.size) + width + " * " + height,
                            getResources().getString(R.string.date) + TimeDecoder.decodeTimeInImageDetail(mPhotoNote.getCreatedPhotoTime())
                    };
                    new AlertDialog.Builder(ZoomActivity.this, R.style.note_dialog)
                            .setPositiveButton(getResources().getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setItems(array, null)
                            .setTitle(getResources().getString(R.string.dialog_title_info))
                            .show();
                    break;
            }
            return true;
        }
    };

    private void hideToolBar() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mToolbar, "translationY", mToolbar.getTranslationY(), -mToolbar.getHeight())
        );
        animatorSet.setDuration(100);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSpreadView.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.start();
    }

    private void showToolBar() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mToolbar, "translationY", mToolbar.getTranslationY(), 0)
        );
        animatorSet.setDuration(100);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mSpreadView.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {

            final PGEditResult editResult = PGEditSDK.instance().handleEditResult(data);

            ImageLoaderManager.displayImage(mPhotoNote.getBigPhotoPathWithFile(), mImage);

            mProgressLayout.show();
            mIsChanged = true;
            NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {

                    FilePathUtils.saveSmallPhotoFromSDK(mPhotoNote.getPhotoName(), editResult.getThumbNail());

                    mPhotoNote.setPaletteColor(UiHelper.getPaletteColor(ImageLoaderManager.loadImageSync(mPhotoNote.getBigPhotoPathWithFile())));
                    PhotoNoteDBModel.getInstance().update(mPhotoNote);

                    Intent intent = new Intent();
                    intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
                    intent.putExtra(Const.TARGET_BROADCAST_PHOTO, true);
                    sendBroadcast(intent);

                    mMainHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                }
            });

        }

        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
                && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_CANCEL) {
            //用户取消编辑
        }

        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
                && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_NOT_CHANGED) {
            // 照片没有修改
        }
    }

    @Override
    public void onClick(View v) {
        showToolBar();
    }

    @Override
    public boolean handleMessage(Message msg) {
        mProgressLayout.hide();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mIsChanged) {
            setResult(RESULT_PICTURE);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
