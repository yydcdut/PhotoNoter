package com.yydcdut.note.views.note.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.note.impl.DetailFragPresenterImpl;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.PhoneUtils;
import com.yydcdut.note.utils.RxImageBlur;
import com.yydcdut.note.views.BaseFragment;
import com.yydcdut.note.views.note.IDetailFragView;
import com.yydcdut.note.widget.AutoFitImageView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/12.
 */
public class DetailFragment extends BaseFragment implements IDetailFragView {
    @Inject
    DetailFragPresenterImpl mDetailFragPresenter;

    @Bind(R.id.img_detail)
    AutoFitImageView mAutoFitImageView;

    @Bind(R.id.img_blur)
    AutoFitImageView mAutoFitBlurView;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mDetailFragPresenter.bindData(bundle.getInt(Const.CATEGORY_ID_4_PHOTNOTES),
                bundle.getInt(Const.PHOTO_POSITION), bundle.getInt(Const.COMPARATOR_FACTORY));
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_detail, null);
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
        mIPresenter = mDetailFragPresenter;
    }

    @Override
    public void initUI(View view) {
        ButterKnife.bind(this, view);
        mDetailFragPresenter.attachView(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener(View view) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.img_detail)
    public void clickImageView(View v) {
        mDetailFragPresenter.jump2ZoomActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_PICTURE) {
            mDetailFragPresenter.showImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showImage(int width, int height, String path) {
        mAutoFitImageView.setAspectRatio(width, height);
        Bitmap bitmap = ImageLoaderManager.loadImageSync(path);
        mAutoFitImageView.setImageBitmap(bitmap);
        long men = PhoneUtils.getTotalMem();
        int radius = 1;
        if (men < 500) {//小于512M
            radius = 1;
        } else if (men < 980) {//512--1024M
            radius = 2;
        } else if (men < 2100) {//1G--2G
            radius = 3;
        } else if (men < 3100) {//2G--3G
            radius = 4;
        } else {//3G以上
            radius = 5;
        }
        RxImageBlur.with(getActivity())
                .radius(radius)
                .blur(bitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawable -> mAutoFitBlurView.setImageDrawable(drawable));
    }

    @Override
    public void jump2ZoomActivity(int categoryId, int position, int comparator) {
        ZoomActivity.startActivityForResult(this, categoryId, position, comparator);
    }
}
