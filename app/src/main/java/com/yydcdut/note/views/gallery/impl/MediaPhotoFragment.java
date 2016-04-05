package com.yydcdut.note.views.gallery.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.MediaPhotoAdapter;
import com.yydcdut.note.adapter.vh.MediaPhotoViewHolder;
import com.yydcdut.note.bean.gallery.MediaFolder;
import com.yydcdut.note.presenters.gallery.impl.MediaPhotoPresenterImpl;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.BaseFragment;
import com.yydcdut.note.views.gallery.IMediaPhotoView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/4/2.
 */
public class MediaPhotoFragment extends BaseFragment implements IMediaPhotoView,
        ActionBar.OnNavigationListener, MediaPhotoViewHolder.OnItemClickListener,
        MediaPhotoViewHolder.OnItemSelectListener {
    private GalleryActivity mGalleryActivity;

    @Bind(R.id.rv_gallery)
    RecyclerView mRecyclerView;

    private ArrayAdapter<String> mFolderAdapter;
    private MediaPhotoAdapter mMediaPhotoAdapter;
    private ActionBar mActionBar;

    @Inject
    MediaPhotoPresenterImpl mMediaPhotoPresenter;

    public static MediaPhotoFragment newInstance() {
        return new MediaPhotoFragment();
    }

    @Override
    public void getBundle(Bundle bundle) {

    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_media_photo, null);
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initUI(View view) {
        ButterKnife.bind(this, view);
        mGalleryActivity = (GalleryActivity) getActivity();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMediaPhotoPresenter.attachView(this);
    }

    @Override
    public void setListNavigationAdapter(List<String> folderNameList) {
        mFolderAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, folderNameList);
        mActionBar = mGalleryActivity.getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setListNavigationCallbacks(mFolderAdapter, this);
    }

    @Override
    public void setMediaAdapter(MediaFolder mediaAdapter) {
        int size = getResources().getDisplayMetrics().widthPixels / 3;
        mMediaPhotoAdapter = new MediaPhotoAdapter(getContext(), size, mediaAdapter, this, this);
        mRecyclerView.setAdapter(mMediaPhotoAdapter);
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

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        mMediaPhotoPresenter.jump2DetailPhoto(adapterPosition, false);
    }

    @Override
    public void jump2PhotoDetail(int position, String folderName, boolean isPreviewSelected) {
        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PAGE, position);
        intent.putExtra(BaseActivity.INTENT_FOLDER, folderName);
        intent.putExtra(BaseActivity.INTENT_PREVIEW_SELECTED, isPreviewSelected);
        startActivityForResult(intent, BaseActivity.REQUEST_CODE);
    }

    @Override
    public void updateMediaFolder(MediaFolder mediaFolder) {
        mMediaPhotoAdapter.updateMediaFolder(mediaFolder);
    }

    @Override
    public void setMenuTitle(String content) {
        mGalleryActivity.getPreviewMenu().setTitle(content);
    }

    @Override
    public void notifyDataChanged() {
        mMediaPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelectClick(View v, int layoutPosition, int adapterPosition, boolean isSelected) {
        mMediaPhotoPresenter.onSelected(adapterPosition, isSelected);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mMediaPhotoPresenter.updateListNavigation(itemPosition);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mMediaPhotoPresenter.onReturnData(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void notifyAdapterDataChanged() {
        mMediaPhotoAdapter.notifyDataSetChanged();
    }


}
