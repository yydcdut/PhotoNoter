package com.yydcdut.note.mvp.v.note.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.mvp.p.note.IDetailFragPresenter;
import com.yydcdut.note.mvp.p.note.impl.DetailFragPresenterImpl;
import com.yydcdut.note.mvp.v.BaseFragment;
import com.yydcdut.note.mvp.v.note.IDetailFragView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.AutoFitImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuyidong on 15/11/12.
 */
public class DetailFragment extends BaseFragment implements IDetailFragView, View.OnClickListener {
    private IDetailFragPresenter mDetailFragPresenter;

    @InjectView(R.id.img_detail)
    AutoFitImageView mAutoFitImageView;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mDetailFragPresenter = new DetailFragPresenterImpl(bundle.getString(Const.CATEGORY_LABEL),
                bundle.getInt(Const.PHOTO_POSITION), bundle.getInt(Const.COMPARATOR_FACTORY));
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_detail, null);
    }

    @Override
    public void initUI(View view) {
        ButterKnife.inject(this, view);
        mDetailFragPresenter.attachView(this);
        mAutoFitImageView.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener(View view) {
    }


    @Override
    public void onClick(View v) {
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
        ImageLoaderManager.displayImage(path, mAutoFitImageView, null);
    }

    @Override
    public void jump2ZoomActivity(String label, int position, int comparator) {
        ZoomActivity.startActivityForResult(this, label, position, comparator);
    }
}
