package com.yydcdut.gallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.gallery.R;
import com.yydcdut.gallery.utils.YLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by yuyidong on 16/3/23.
 */
public class PhotoDetailFragment extends Fragment {
    public static final String PHOTO_PATH = "path";

    @Bind(R.id.img_detail)
    PhotoView mPhotoView;

    public static PhotoDetailFragment getInstance() {
        return new PhotoDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_detail, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        YLog.i("yuyidong", "2222222       " + "file:/" + bundle.getString(PHOTO_PATH));
        ImageLoader.getInstance().displayImage("file:/" + bundle.getString(PHOTO_PATH), mPhotoView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
