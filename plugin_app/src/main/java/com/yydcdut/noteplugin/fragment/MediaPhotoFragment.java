package com.yydcdut.noteplugin.fragment;

import android.content.Intent;
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

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.adapter.MediaPhotoAdapter;
import com.yydcdut.noteplugin.adapter.vh.MediaPhotoViewHolder;
import com.yydcdut.noteplugin.bean.MediaFolder;
import com.yydcdut.noteplugin.controller.BaseActivity;
import com.yydcdut.noteplugin.controller.MainActivity;
import com.yydcdut.noteplugin.model.PhotoModel;
import com.yydcdut.noteplugin.model.SelectPhotoModel;
import com.yydcdut.noteplugin.utils.Jumper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/3/19.
 */
public class MediaPhotoFragment extends BaseFragment implements ActionBar.OnNavigationListener,
        MediaPhotoViewHolder.OnItemClickListener, MediaPhotoViewHolder.OnItemSelectListener {

    private MainActivity mMainActivity;

    @Bind(R.id.rv_album)
    RecyclerView mRecyclerView;

    private ArrayAdapter<String> mFolderAdapter;
    private MediaPhotoAdapter mMediaPhotoAdapter;
    private ActionBar mActionBar;
    private List<String> mFolderNameList;
    private Map<String, MediaFolder> mMediaFolderByNameMap;
    private String mCurrentFolderName = null;

    public static MediaPhotoFragment newInstance() {
        return new MediaPhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_media_photo, null);
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
        mMediaFolderByNameMap = PhotoModel.getInstance().findByMedia(getContext());
        mFolderNameList = new ArrayList<>(mMediaFolderByNameMap.size());
        for (Map.Entry<String, MediaFolder> entry : mMediaFolderByNameMap.entrySet()) {
            mFolderNameList.add(entry.getKey());
        }
        mFolderNameList.remove(MediaFolder.ALL);
        mFolderNameList.add(0, MediaFolder.ALL);
        mCurrentFolderName = MediaFolder.ALL;
        mFolderAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, mFolderNameList);
        mActionBar = mMainActivity.getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setListNavigationCallbacks(mFolderAdapter, this);
        int size = getResources().getDisplayMetrics().widthPixels / 3;
        mMediaPhotoAdapter = new MediaPhotoAdapter(getContext(), size, mMediaFolderByNameMap.get(mCurrentFolderName), this, this);
        mRecyclerView.setAdapter(mMediaPhotoAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mCurrentFolderName = mFolderNameList.get(itemPosition);
        mMediaPhotoAdapter.updateMediaFolder(mMediaFolderByNameMap.get(mCurrentFolderName));
        return true;
    }

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        Jumper.jump2DetailActivityAll(getActivity(), adapterPosition, mCurrentFolderName);
    }

    @Override
    public void onItemSelectClick(View v, int layoutPosition, int adapterPosition, boolean isSelected) {
        String path = mMediaFolderByNameMap.get(mCurrentFolderName).getMediaPhotoList().get(adapterPosition).getPath();
        if (isSelected) {
            SelectPhotoModel.getInstance().addPath(path);
        } else {
            SelectPhotoModel.getInstance().removePath(path);
        }
        if (SelectPhotoModel.getInstance().getCount() == 0) {
            mMainActivity.getPreviewMenu().setTitle(getResources().getString(R.string.action_view));
        } else {
            mMainActivity.getPreviewMenu().setTitle(getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.REQUEST_CODE && resultCode == BaseActivity.CODE_RESULT_CHANGED) {
            mMediaPhotoAdapter.notifyDataSetChanged();
            if (SelectPhotoModel.getInstance().getCount() == 0) {
                mMainActivity.getPreviewMenu().setTitle(getResources().getString(R.string.action_view));
            } else {
                mMainActivity.getPreviewMenu().setTitle(getResources().getString(R.string.action_view) + "(" + SelectPhotoModel.getInstance().getCount() + ")");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void notifyAdapterDataChanged() {
        mMediaPhotoAdapter.notifyDataSetChanged();
    }
}
