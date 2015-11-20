package com.yydcdut.note.mvp.v.home.impl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.AlbumAdapter;
import com.yydcdut.note.adapter.vh.PhotoViewHolder;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.camera.controller.CameraActivity;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.listener.FloatingScrollHideListener;
import com.yydcdut.note.mvp.p.home.IAlbumPresenter;
import com.yydcdut.note.mvp.p.home.impl.AlbumPresenterImpl;
import com.yydcdut.note.mvp.v.home.IAlbumView;
import com.yydcdut.note.mvp.v.note.impl.DetailActivity;
import com.yydcdut.note.mvp.v.setting.impl.SettingActivity;
import com.yydcdut.note.service.SandBoxService;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab.FloatingActionsMenu;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuyidong on 15-3-23.
 */
public class AlbumFragment extends BaseFragment implements IAlbumView, View.OnClickListener, PhotoViewHolder.OnItemClickListener,
        PhotoViewHolder.OnItemLongClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private static final String TAG = AlbumFragment.class.getSimpleName();

    private IAlbumPresenter mAlbumPresenter;

    private static final int INTENT_REQUEST_LOCAL = 101;
    private static final int INTENT_REQUEST_CAMERA = 201;
    private static final int INTENT_REQUEST_CROP = 301;

    /* RecyclerView */
    @InjectView(R.id.rv_album)
    RecyclerView mRecyclerView;
    /* FloatingActionButton */
    @InjectView(R.id.fab_main)
    FloatingActionsMenu mFloatingActionsMenu;
    @InjectView(R.id.view_menu_floating_position)
    View mFloatingView;//当点击FloatingActionsMenu的时候RevealColor可以找到起始坐标
    /* Progress */
    @InjectView(R.id.layout_progress)
    CircleProgressBarLayout mProgressLayout;
    @InjectView(R.id.reveal_album)
    RevealView mAlbumRevealView;
    /* RevealColor */
    private RevealView mLayoutRevealView;
    /* RecyclerView布局 */
    private GridLayoutManager mGridLayoutManager;
    /* RecyclerView的适配器 */
    private AlbumAdapter mAdapter;

    private boolean mIsAlbumRevealOpen = false;//判断相册现在的RevealView是不是打开状态
    private boolean mIsLayoutRevealOpen = false;//判断activity现在的RevealView是不是打开状态

    /* 是不是选择模式 */
    private boolean mIsMenuSelectMode = false;
    /* ScrollView滑动监听器 */
    private FloatingScrollHideListener mFloatingScrollHideListener;
    /* menu的item */
    private MenuItem mSortMenuItem;
    private MenuItem mTrashMenuItem;
    private MenuItem mAllSelectMenuItem;
    private MenuItem mSelectMenuItem;
    private MenuItem mNewCategoryMenuItem;
    private MenuItem mSettingMenuItem;
    private MenuItem mMoveMenuItem;
    private Menu mMainMenu;

    /* Handler */
    private Handler mMainHandler;

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void getBundle(Bundle bundle) {
        mAlbumPresenter = new AlbumPresenterImpl(bundle.getString(Const.CATEGORY_LABEL));
        mMainHandler = new Handler();
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_album, null);
    }

    @Override
    public void initUI(View view) {
        ButterKnife.inject(this, view);
        mAlbumPresenter.attachView(this);
        mLayoutRevealView = (RevealView) getActivity().findViewById(R.id.reveal_layout);
    }

    @Override
    public void initListener(View view) {
        view.findViewById(R.id.fab_camera).setOnClickListener(this);
        view.findViewById(R.id.fab_local).setOnClickListener(this);
        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(this);
        mFloatingScrollHideListener = new FloatingScrollHideListener(mFloatingActionsMenu, mGridLayoutManager);
        mRecyclerView.addOnScrollListener(mFloatingScrollHideListener);
        mAlbumRevealView.setOnTouchListener(mEmptyTouch);
        mLayoutRevealView.setOnTouchListener(mEmptyTouch);
    }

    @Override
    public void initData() {
        initReceiver();
    }

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        if (mIsMenuSelectMode) {
            if (!mAdapter.isPhotoSelected(adapterPosition)) {
                mAdapter.setSelectedPosition(true, adapterPosition);
            } else {
                mAdapter.setSelectedPosition(false, adapterPosition);
            }
        } else {
            mAlbumPresenter.jump2DetailActivity(adapterPosition);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAlbumPresenter.checkSandBox();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_LOCAL) {
            mAlbumPresenter.savePhotoFromLocal(data.getData());
        } else if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_CAMERA) {
            mAlbumPresenter.savePhotoFromSystemCamera();
        } else {
            closeLayoutRevealColorView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_album, menu);
        boolean isNotMenuExist = mMainMenu == null;
        mMainMenu = menu;
        mSortMenuItem = menu.findItem(R.id.menu_sort);
        mTrashMenuItem = menu.findItem(R.id.menu_trash);
        mAllSelectMenuItem = menu.findItem(R.id.menu_all_select);
        mSelectMenuItem = menu.findItem(R.id.menu_select);
        mNewCategoryMenuItem = menu.findItem(R.id.menu_new_file);
        mSettingMenuItem = menu.findItem(R.id.menu_setting);
        mMoveMenuItem = menu.findItem(R.id.menu_move);
        setAlbumSortKind(menu);
        if (isNotMenuExist) {
            mAlbumPresenter.sortData();
        }
    }

    /**
     * 在menu中设置排序方式
     *
     * @param menu
     */
    private void setAlbumSortKind(Menu menu) {
        int sort = mAlbumPresenter.getAlbumSort();
        switch (sort) {
            case 1:
                menu.findItem(R.id.menu_sort_create_far).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.menu_sort_create_close).setChecked(true);
                break;
            case 3:
                menu.findItem(R.id.menu_sort_edit_far).setChecked(true);
                break;
            case 4:
                menu.findItem(R.id.menu_sort_edit_close).setChecked(true);
                break;
        }
    }

    /**
     * menu为选择模式
     */
    private void menuSelectMode() {
        mSettingMenuItem.setVisible(false);
        mSortMenuItem.setVisible(false);
        mSelectMenuItem.setVisible(false);
        mNewCategoryMenuItem.setVisible(false);
        mTrashMenuItem.setVisible(true);
        mAllSelectMenuItem.setVisible(true);
        mMoveMenuItem.setVisible(true);
        mIsMenuSelectMode = true;
    }

    /**
     * menu为显示模式
     */
    private void menuPreviewMode() {
        mSettingMenuItem.setVisible(true);
        mSortMenuItem.setVisible(true);
        mSelectMenuItem.setVisible(true);
        mNewCategoryMenuItem.setVisible(true);
        mTrashMenuItem.setVisible(false);
        mAllSelectMenuItem.setVisible(false);
        mMoveMenuItem.setVisible(false);
        mIsMenuSelectMode = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_create_far:
                mAlbumPresenter.setAlbumSort(1);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_create_close:
                mAlbumPresenter.setAlbumSort(2);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_far:
                mAlbumPresenter.setAlbumSort(3);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_close:
                mAlbumPresenter.setAlbumSort(4);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_trash:
                mAdapter.deleteSelectedPhotos();
                mAlbumPresenter.refreshData();
                menuPreviewMode();
                break;
            case R.id.menu_all_select:
                mAdapter.selectAllPhotos();
                break;
            case R.id.menu_move:
                mAlbumPresenter.changePhotos2AnotherCategory();
                break;
            case R.id.menu_setting:
                showLayoutRevealColorView(new RevealView.RevealAnimationListener() {
                    @Override
                    public void finish() {
                        Intent intent = new Intent(getContext(), SettingActivity.class);
                        startActivityForResult(intent, REQUEST_NOTHING);
                        getActivity().overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
                    }
                });
                break;
            case R.id.menu_select:
                if (!mIsMenuSelectMode) {
                    menuSelectMode();
                } else {
                    menuPreviewMode();
                }
                break;
            case R.id.menu_new_file:
                showAddCategoryDialog();
                break;
        }
        return true;
    }

    /**
     * 添加category的dialog
     */
    private void showAddCategoryDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edittext, null);
        final EditText editText = (EditText) v.findViewById(R.id.edit_dialog);
        new AlertDialog.Builder(getContext(), R.style.note_dialog)
                .setTitle(R.string.dialog_title_new)
                .setCancelable(false)
                .setView(v)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlbumPresenter.createCategory(editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onClick(final View v) {
        if (!mAlbumPresenter.checkStorageEnough()) {
            mFloatingActionsMenu.collapse(false);
            hideAlbumRevealColorView(getLocationInView(mAlbumRevealView, v));
            return;
        }
        switch (v.getId()) {
            case R.id.fab_camera:
                mAlbumPresenter.jump2Camera();
                break;
            case R.id.fab_local:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, INTENT_REQUEST_LOCAL);
                break;
        }
        //过1s自动关闭
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFloatingActionsMenu.collapse(false);
                hideAlbumRevealColorView(getLocationInView(mAlbumRevealView, v));
            }
        }, 1000);
    }

    @Override
    public boolean onItemLongClick(View v, int layoutPosition, int adapterPosition) {
        if (!mIsMenuSelectMode) {
            menuSelectMode();
        }
        mAdapter.setSelectedPosition(true, adapterPosition);
        return true;
    }

    @Override
    public void onMenuExpanded() {
        showAlbumRevealColorView(getLocationInView(mAlbumRevealView, mFloatingView));
    }

    @Override
    public void onMenuCollapsed() {
        hideAlbumRevealColorView(getLocationInView(mAlbumRevealView, mFloatingView));
    }

    /**
     * 隐藏RevealColorView
     *
     * @param p
     */
    private void hideAlbumRevealColorView(Point p) {
        mAlbumRevealView.hide(p.x, p.y, Color.TRANSPARENT, 0, Const.DURATION, null);
        mIsAlbumRevealOpen = false;
    }

    /**
     * 显示RevealColorView
     *
     * @param p
     */
    private void showAlbumRevealColorView(Point p) {
        mAlbumRevealView.reveal(p.x, p.y, getResources().getColor(R.color.fab_reveal_white), Const.RADIUS, Const.DURATION, null);
        mIsAlbumRevealOpen = true;
    }

    /**
     * Layout的RevealView
     *
     * @param listener
     */
    private void showLayoutRevealColorView(RevealView.RevealAnimationListener listener) {
        mLayoutRevealView.reveal(Evi.sScreenWidth, getActionBarSize(), getThemeColor(), Const.RADIUS, Const.DURATION, listener);
        mIsLayoutRevealOpen = true;
    }

    /**
     * Layout的RevaelView
     */
    private void closeLayoutRevealColorView() {
        if (mFloatingScrollHideListener != null && mFloatingScrollHideListener.isHide()) {
            mFloatingScrollHideListener.show();
        }
        Point p = getLocationInView(mLayoutRevealView, mFloatingView);
        mLayoutRevealView.hide(p.x, p.y, Color.TRANSPARENT, Const.RADIUS, Const.DURATION, null);
        mIsLayoutRevealOpen = false;
    }

    /**
     * floatingActionButton打开的时候不能点击后面的gridview
     */
    private View.OnTouchListener mEmptyTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.reveal_album) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Point point = new Point((int) event.getX(), (int) event.getY());
                    mFloatingActionsMenu.collapse(false);
                    hideAlbumRevealColorView(point);
                }
            }
            return true;
        }
    };

    /**
     * 如果是再select模式的话，换为preview模式
     *
     * @return
     */
    public boolean isMenuSelectModeAndChangeIt() {
        if (mIsMenuSelectMode) {
            menuPreviewMode();
            mAdapter.cancelSelectPhotos();
            return true;
        }
        return false;
    }


    /**
     * 如果RevealColorView是开着的话，关闭他
     * 没有开的话就不管
     *
     * @return
     */
    public boolean ifRevealOpenAndCloseIt() {
        if (mIsAlbumRevealOpen) {
            hideAlbumRevealColorView(getLocationInView(mAlbumRevealView, mFloatingView));
            mFloatingActionsMenu.collapse(false);
            return true;
        }
        return false;
    }

    /**
     * activity的RevealView是不是打开状态
     * 如果是打开状态的，按下back按键不起作用
     *
     * @return
     */
    public boolean isLayoutRevealOpen() {
        return mIsLayoutRevealOpen;
    }


    /**
     * 换Category
     *
     * @param categoryLabel
     */
    public void changePhotos4Category(String categoryLabel) {
        mAdapter.cancelSelectPhotos();
        menuPreviewMode();
        mAlbumPresenter.changeCategoryWithPhotos(categoryLabel);
    }

    /**
     * 注册广播
     */
    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        getActivity().registerReceiver(mUpdatePhotoNoteList, intentFilter);
    }

    /**
     * 广播，收到广播之后发消息
     */
    private BroadcastReceiver mUpdatePhotoNoteList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //当图片数据改变的时候，比如滤镜，Service作图
            //另外个进程发来广播的时候
            mAlbumPresenter.updateFromBroadcast(intent.getBooleanExtra(Const.TARGET_BROADCAST_PROCESS, false),
                    intent.getBooleanExtra(Const.TARGET_BROADCAST_SERVICE, false),
                    intent.getBooleanExtra(Const.TARGET_BROADCAST_PHOTO, false));
        }
    };

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        getActivity().unregisterReceiver(mUpdatePhotoNoteList);
    }

    @Override
    public void onPause() {
        mAlbumPresenter.saveAlbumSort();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

    @Override
    public void setAdapter(List<PhotoNote> photoNoteList) {
        mAdapter = new AlbumAdapter(getContext(), photoNoteList, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void startSandBoxService() {
        Intent intent = new Intent(getContext(), SandBoxService.class);
        getActivity().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void jump2DetailActivity(String categoryLabel, int position, int comparator) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, categoryLabel);
        bundle.putInt(Const.PHOTO_POSITION, position);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateData(List<PhotoNote> photoNoteList) {
        mAdapter.updateData(photoNoteList);
    }

    @Override
    public void showChangePhotos2AnotherCategoryDialog(final String[] categoryLabelArray) {
        new AlertDialog.Builder(getContext(), R.style.note_dialog)
                .setTitle(R.string.dialog_title_move)
                .setItems(categoryLabelArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlbumPresenter.changePhotosCategory(categoryLabelArray[which]);
                        menuPreviewMode();
                    }
                })
                .show();
    }

    @Override
    public void notifyItemRemoved(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressBar() {
        mProgressLayout.show();
    }

    @Override
    public void hideProgressBar() {
        mProgressLayout.hide();
    }

    @Override
    public void changeActivityListMenuCategoryChecked(Category category) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.changeCategoryAfterSaving(category);
    }

    @Override
    public void jump2CameraActivity(String categoryLabel) {
        Intent intent = new Intent(getContext(), CameraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, categoryLabel);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    @Override
    public void jump2CameraSystemActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(FilePathUtils.getTempFilePath()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, INTENT_REQUEST_CAMERA);
    }


}
