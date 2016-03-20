package com.yydcdut.gallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.adapter.PhotoAdapter;
import com.yydcdut.gallery.controller.MainActivity;
import com.yydcdut.gallery.model.MediaFolder;
import com.yydcdut.gallery.utils.PhotoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/19.
 */
public class PhotoFragment extends BaseFragment implements ActionBar.OnNavigationListener {
    private MainActivity mMainActivity;

    @Bind(R.id.rv_album)
    RecyclerView mRecyclerView;

    private ArrayAdapter<String> mAdapter;
    private PhotoAdapter mPhotoAdapter;
    private ActionBar mActionBar;
    private List<String> mFolderNameList;
    private Map<String, MediaFolder> mMediaFolderByNameMap;

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_photo, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMediaFolderByNameMap = PhotoUtils.findByMedia(getContext());
        if (mMediaFolderByNameMap == null) {
            return;
        }
        mFolderNameList = new ArrayList<>(mMediaFolderByNameMap.size());
        for (Map.Entry<String, MediaFolder> entry : mMediaFolderByNameMap.entrySet()) {
            mFolderNameList.add(entry.getKey());
        }
        mFolderNameList.remove(MediaFolder.ALL);
        mFolderNameList.add(0, MediaFolder.ALL);
        mAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, mFolderNameList);
        mActionBar = mMainActivity.getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setListNavigationCallbacks(mAdapter, this);
        int size = getResources().getDisplayMetrics().widthPixels / 3;
        mPhotoAdapter = new PhotoAdapter(getContext(), size, mMediaFolderByNameMap.get(MediaFolder.ALL), null, null);
        mRecyclerView.setAdapter(mPhotoAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mPhotoAdapter.updateMediaFolder(mMediaFolderByNameMap.get(mFolderNameList.get(itemPosition)));
        return true;
    }
}
