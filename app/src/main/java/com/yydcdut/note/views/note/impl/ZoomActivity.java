package com.yydcdut.note.views.note.impl;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.note.impl.ZoomPresenterImpl;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.note.IZoomView;
import com.yydcdut.note.widget.CircleProgressBarLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;


/**
 * Created by yyd on 15-4-19.
 */
public class ZoomActivity extends BaseActivity implements IZoomView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.img_zoom)
    PhotoView mImage;
    @Bind(R.id.layout_progress)
    CircleProgressBarLayout mProgressLayout;

    @Inject
    ZoomPresenterImpl mZoomPresenter;

    /**
     * 启动Activity
     *
     * @param fragment
     * @param categoryId
     * @param photoNotePosition
     * @param comparator
     */
    public static void startActivityForResult(Fragment fragment, int categoryId, int photoNotePosition, int comparator) {
        Intent intent = new Intent(fragment.getActivity(), ZoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.CATEGORY_ID_4_PHOTNOTES, categoryId);
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
        mActivityComponent.inject(this);
        mIPresenter = mZoomPresenter;
    }

    @Override
    public void initUiAndListener() {
        Bundle bundle = getIntent().getExtras();
        ButterKnife.bind(this);
        mZoomPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES),
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
                case R.id.menu_effect:
                    mZoomPresenter.jump2PGEditActivity();
                    break;
                case R.id.menu_rotation_0:
                    mImage.setRotation(0);
                    break;
                case R.id.menu_rotation_90:
                    mImage.setRotation(90);
                    break;
                case R.id.menu_rotation_180:
                    mImage.setRotation(180);
                    break;
                case R.id.menu_rotation_270:
                    mImage.setRotation(270);
                    break;
            }
            return true;
        }
    };

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
//                && resultCode == Activity.RESULT_OK) {
//
//            final PGEditResult editResult = PGEditSDK.instance().handleEditResult(data);
//
//            mZoomPresenter.refreshImage();
//            mZoomPresenter.saveSmallImage(editResult.getThumbNail());
//        }
//
//        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
//                && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_CANCEL) {
//            //用户取消编辑
//        }
//
//        if (requestCode == PGEditSDK.PG_EDIT_SDK_REQUEST_CODE
//                && resultCode == PGEditSDK.PG_EDIT_SDK_RESULT_CODE_NOT_CHANGED) {
//            // 照片没有修改
//        }
//    }

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
        ImageLoaderManager.displayImageWihtoutCache(path, mImage);
    }

    @Override
    public void jump2PGEditActivity(String path) {
//        PGEditSDK.instance().startEdit(ZoomActivity.this, PGEditActivity.class, path, path);
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
