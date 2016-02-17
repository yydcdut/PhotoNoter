package com.yydcdut.note.views.camera.impl;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.camera.impl.AdjustCameraPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.camera.IAdjustCameraView;
import com.yydcdut.note.widget.camera.AutoFitPreviewView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 16/2/16.
 */
public class AdjustCameraActivity extends BaseActivity implements IAdjustCameraView,
        AutoFitPreviewView.SurfaceListener {
    @Inject
    AdjustCameraPresenterImpl mAdjustCameraPresenter;

    @Bind(R.id.auto_preview)
    AutoFitPreviewView mAutoFitPreviewView;

    @Bind(R.id.fab_rotate)
    FloatingActionButton mRotationBtn;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        AppCompat.setFullWindow(getWindow());
        return R.layout.activity_adjust_camera2;
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
        mAdjustCameraPresenter.attachView(this);
        mIPresenter = mAdjustCameraPresenter;
    }

    @Override
    public void initUiAndListener() {
        initToolBarUI();
        if (AppCompat.AFTER_LOLLIPOP) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams();
            l.topMargin = getStatusBarSize();
        }
        if (AppCompat.hasNavigationBar(this)) {
            RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) mRotationBtn.getLayoutParams();
            l.bottomMargin = (int) (AppCompat.getNavigationBarHeight(this) + getResources().getDimension(R.dimen.dimen_12dip));
        }
        mAutoFitPreviewView.setSurfaceListener(this);
    }

    private void initToolBarUI() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setBackgroundColor(AppCompat.getColor(android.R.color.transparent, this));
        mToolbar.setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mAdjustCameraPresenter.clickBack();
                break;
            case R.id.menu_switch_camera:
                mAdjustCameraPresenter.switchCamera();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adjust_camera, menu);
        return true;
    }

    @OnClick(R.id.fab_rotate)
    public void onRotationClick(View view) {
        mAdjustCameraPresenter.clickRotation();
    }

    @Override
    public void onSurfaceAvailable(AutoFitPreviewView.PreviewSurface surface, int width, int height) {
        mAdjustCameraPresenter.onSurfaceAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceDestroy() {
        mAdjustCameraPresenter.onSurfaceDestroy();
    }

    @Override
    public void setSize(int w, int h) {
        mAutoFitPreviewView.setAspectRatio(w, h);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        mAdjustCameraPresenter.clickBack();
    }
}
