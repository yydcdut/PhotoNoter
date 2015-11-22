package com.yydcdut.note.mvp.v.note.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.yydcdut.note.injector.component.DaggerActivityComponent;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.mvp.p.note.impl.ZoomPresenterImpl;
import com.yydcdut.note.mvp.v.BaseActivity;
import com.yydcdut.note.mvp.v.note.IZoomView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.ZoomImageView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.pinguo.edit.sdk.PGEditActivity;
import us.pinguo.edit.sdk.base.PGEditResult;
import us.pinguo.edit.sdk.base.PGEditSDK;


/**
 * Created by yyd on 15-4-19.
 */
public class ZoomActivity extends BaseActivity implements IZoomView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.img_zoom)
    ZoomImageView mImage;
    @Bind(R.id.img_zoom_spread)
    View mSpreadView;
    @Bind(R.id.layout_progress)
    CircleProgressBarLayout mProgressLayout;

    @Inject
    ZoomPresenterImpl mZoomPresenter;

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
    public void initInjector() {
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build();
        mActivityComponent.inject(this);
    }

    @Override
    public void initUiAndListener() {
        Bundle bundle = getIntent().getExtras();
        ButterKnife.bind(this);
        mZoomPresenter.bindData(bundle.getString(Const.CATEGORY_LABEL),
                bundle.getInt(Const.PHOTO_POSITION), bundle.getInt(Const.COMPARATOR_FACTORY));
        mZoomPresenter.attachView(this);
        initToolBarUI();
    }

    private void initToolBarUI() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setOnMenuItemClickListener(onToolBarMenuItemClick);
        mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mToolbar.setTitle("");
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
                mZoomPresenter.finishActivity();
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
                    mZoomPresenter.jump2PGEditActivity();
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

            mZoomPresenter.refreshImage();
            mZoomPresenter.saveSmallImage(editResult.getThumbNail());
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

    @OnClick(R.id.img_zoom_spread)
    public void click2ShowToolBar(View v) {
        showToolBar();
    }

    @Override
    public void onBackPressed() {
        mZoomPresenter.finishActivity();
    }

    @Override
    public void showProgressBar() {
        mProgressLayout.show();
    }

    @Override
    public void hideProgressBar() {
        mProgressLayout.hide();
    }

    @Override
    public void showImage(String path) {
        ImageLoaderManager.displayImage(path, mImage);
    }

    @Override
    public void jump2PGEditActivity(String path) {
        PGEditSDK.instance().startEdit(ZoomActivity.this, PGEditActivity.class, path, path);

    }

    @Override
    public void finishActivity(boolean hasResult) {
        if (hasResult) {
            setResult(RESULT_PICTURE);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showSnackBar(String massage) {
        Snackbar.make(findViewById(R.id.layout_zoom), massage, Snackbar.LENGTH_SHORT).show();
    }
}
