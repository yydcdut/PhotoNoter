package com.yydcdut.note.views.gallery;

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
import com.yydcdut.note.model.gallery.PhotoModel;
import com.yydcdut.note.model.gallery.SelectPhotoModel;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/4/2.
 */
public class MediaPhotoFragment extends BaseFragment implements ActionBar.OnNavigationListener,
        MediaPhotoViewHolder.OnItemClickListener, MediaPhotoViewHolder.OnItemSelectListener {
    private GalleryActivity mMainActivity;

    @Bind(R.id.rv_gallery)
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

    @Override
    public void getBundle(Bundle bundle) {

    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_media_photo, null);
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initUI(View view) {
        ButterKnife.bind(this, view);
        mMainActivity = (GalleryActivity) getActivity();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void initData() {
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
    public void initListener(View view) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
        intent.putExtra(BaseActivity.INTENT_PAGE, adapterPosition);
        intent.putExtra(BaseActivity.INTENT_FOLDER, mCurrentFolderName);
        intent.putExtra(BaseActivity.INTENT_PREVIEW_SELECTED, false);
        startActivityForResult(intent, BaseActivity.REQUEST_CODE);
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
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mCurrentFolderName = mFolderNameList.get(itemPosition);
        mMediaPhotoAdapter.updateMediaFolder(mMediaFolderByNameMap.get(mCurrentFolderName));
        return true;
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
