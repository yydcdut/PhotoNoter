package com.yydcdut.note.mvp.v.note.impl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.component.DaggerFragmentComponent;
import com.yydcdut.note.injector.module.FragmentModule;
import com.yydcdut.note.mvp.p.note.impl.DetailFragPresenterImpl;
import com.yydcdut.note.mvp.v.BaseFragment;
import com.yydcdut.note.mvp.v.note.IDetailFragView;
import com.yydcdut.note.utils.Const;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 15/11/12.
 */
public class DetailFragment extends BaseFragment implements IDetailFragView {
    @Inject
    DetailFragPresenterImpl mDetailFragPresenter;

    @Bind(R.id.img_detail)
    SimpleDraweeView mDraweeView;

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
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .applicationComponent(((NoteApplication) getActivity().getApplication()).getApplicationComponent())
                .build();
        mFragmentComponent.inject(this);
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
    public void showImage(String path) {
        Uri uri = Uri.parse(path);
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setAutoRotateEnabled(true)
//                .build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setOldController(mDraweeView.getController())
//                .setImageRequest(request)
//                .setUri(uri)
//                .build();
//        mDraweeView.setController(controller);
        mDraweeView.setImageURI(uri);
    }

    @Override
    public void jump2ZoomActivity(int categoryId, int position, int comparator) {
        ZoomActivity.startActivityForResult(this, categoryId, position, comparator);
    }
}
