package com.yydcdut.note.controller.note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.AutoFitImageView;

/**
 * Created by yuyidong on 15/11/12.
 */
public class DetailFragment2 extends BaseFragment {
    /* data */
    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;
    /* ImageView */
    private AutoFitImageView mAutoFitImageView;

    public static DetailFragment2 newInstance() {
        return new DetailFragment2();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
        mPosition = bundle.getInt(Const.PHOTO_POSITION);
        String category = bundle.getString(Const.CATEGORY_LABEL);
        mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_detail, null);
    }

    @Override
    public void initUI(View view) {
        mAutoFitImageView = (AutoFitImageView) view.findViewById(R.id.img_detail);
    }

    @Override
    public void initData() {
        int[] size = FilePathUtils.getPictureSize(mPhotoNote.getBigPhotoPathWithoutFile());
        mAutoFitImageView.setAspectRatio(size[0], size[1]);
        ImageLoaderManager.displayImage(mPhotoNote.getSmallPhotoPathWithFile(), mAutoFitImageView, null);
    }

    @Override
    public void initListener(View view) {
    }

}
