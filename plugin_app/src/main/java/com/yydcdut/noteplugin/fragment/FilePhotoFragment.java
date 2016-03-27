package com.yydcdut.noteplugin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.FilePhotoAdapter;
import com.yydcdut.noteplugin.bean.TreeFile;
import com.yydcdut.noteplugin.model.PhotoModel;
import com.yydcdut.noteplugin.utils.YLog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/27.
 */
public class FilePhotoFragment extends BaseFragment {

    @Bind(R.id.rv_album)
    RecyclerView mRecyclerView;

    public static FilePhotoFragment newInstance() {
        return new FilePhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_media_photo, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TreeFile root = PhotoModel.getInstance().findByPath();
        if (root != null) {
            mRecyclerView.setAdapter(new FilePhotoAdapter(root));
        } else {
            YLog.i("yuyidong", "root == null");
        }
    }
}
