package com.yydcdut.note.controller.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.AlbumAdapter;
import com.yydcdut.note.adapter.vh.PhotoViewHolder;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.camera.controller.CameraActivity;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.controller.note.DetailActivity;
import com.yydcdut.note.controller.setting.SettingActivity;
import com.yydcdut.note.listener.FloatingScrollHideListener;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.SandBoxDBModel;
import com.yydcdut.note.service.SandBoxService;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.compare.ComparatorFactory;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab.FloatingActionsMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuyidong on 15-3-23.
 */
public class AlbumFragment extends BaseFragment implements View.OnClickListener, PhotoViewHolder.OnItemClickListener,
        PhotoViewHolder.OnItemLongClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener, Handler.Callback {
    private static final String TAG = AlbumFragment.class.getSimpleName();

    private static final int INTENT_REQUEST_LOCAL = 101;
    private static final int INTENT_REQUEST_CAMERA = 201;
    private static final int INTENT_REQUEST_CROP = 301;

    private static final int MSG_UPDATE_DATA = 100;

    /* RecyclerView */
    private RecyclerView mRecyclerView;
    /* RecyclerView布局 */
    private GridLayoutManager mGridLayoutManager;
    /* RecyclerView的数据 */
    private List<PhotoNote> mPhotoNoteList;
    /* RecyclerView的适配器 */
    private AlbumAdapter mAdapter;
    /* RevealColor */
    private RevealView mLayoutRevealView;
    private RevealView mAlbumRevealView;
    private boolean mIsAlbumRevealOpen = false;//判断相册现在的RevealView是不是打开状态
    private boolean mIsLayoutRevealOpen = false;//判断activity现在的RevealView是不是打开状态
    /* FloatingActionButton */
    private FloatingActionsMenu mFloatingActionsMenu;
    private View mFloatingView;//当点击FloatingActionsMenu的时候RevealColor可以找到起始坐标
    /* 是不是选择模式 */
    private boolean mIsMenuSelectMode = false;
    /* 来自sharedPreference */
    private int mAlbumSortKind;
    /* ScrollView滑动监听器 */
    private FloatingScrollHideListener mFloatingScrollHideListener;
    /* Category的Label */
    private String mCategoryLabel;
    /* menu的item */
    private MenuItem mSortMenuItem;
    private MenuItem mTrashMenuItem;
    private MenuItem mAllSelectMenuItem;
    private MenuItem mSelectMenuItem;
    private MenuItem mNewCategoryMenuItem;
    private MenuItem mSettingMenuItem;
    private MenuItem mMoveMenuItem;
    private Menu mMainMenu;
    /* Progress */
    private CircleProgressBarLayout mProgressLayout;
    /* Handler */
    private Handler mMainHandler = new Handler(this);

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void initSetting() {
        mAlbumSortKind = LocalStorageUtils.getInstance().getSortKind();
    }

    @Override
    public void getBundle(Bundle bundle) {
        mCategoryLabel = bundle.getString(Const.CATEGORY_LABEL);
    }


    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_album, null);
    }

    @Override
    public void initUI(View view) {
        initRevealColorUI(view);
        initListView(view);
        initFloatingActionsMenuUI(view);
        initCircleProgressBar();
    }

    /**
     * ListView初始化
     *
     * @param view
     */
    private void initListView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_album);
    }

    /**
     * RevealColor初始化
     */
    private void initRevealColorUI(View view) {
        mAlbumRevealView = (RevealView) view.findViewById(R.id.reveal_album);
        mAlbumRevealView.setOnTouchListener(mEmptyTouch);
        mLayoutRevealView = (RevealView) getActivity().findViewById(R.id.reveal_layout);
        mLayoutRevealView.setOnTouchListener(mEmptyTouch);
    }

    /**
     * FloatingActionButton初始化
     */
    private void initFloatingActionsMenuUI(View view) {
        mFloatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_main);
        mFloatingView = view.findViewById(R.id.view_menu_floating_position);
    }

    private void initCircleProgressBar() {
        mProgressLayout = (CircleProgressBarLayout) getView().findViewById(R.id.layout_progress);
    }

    @Override
    public void initListener(View view) {
        view.findViewById(R.id.fab_camera).setOnClickListener(this);
        view.findViewById(R.id.fab_local).setOnClickListener(this);
        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(this);
        mFloatingScrollHideListener = new FloatingScrollHideListener(mFloatingActionsMenu, mGridLayoutManager);
        mRecyclerView.addOnScrollListener(mFloatingScrollHideListener);
    }


    @Override
    public void initData() {
        initReceiver();
        mAlbumSortKind = LocalStorageUtils.getInstance().getSortKind();
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mAlbumSortKind);

        mAdapter = new AlbumAdapter(getContext(), mPhotoNoteList, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void saveSettingWhenPausing() {
        LocalStorageUtils.getInstance().setSortKind(mAlbumSortKind);
    }

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        if (mIsMenuSelectMode) {
            if (!mAdapter.isPhotoSelected(adapterPosition)) {
                mAdapter.setSelectedPosition(true, adapterPosition);
            } else {
                mAdapter.setSelectedPosition(false, adapterPosition);
            }
            return;
        }
        Intent intent = new Intent(getContext(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, mCategoryLabel);
        bundle.putInt(Const.PHOTO_POSITION, adapterPosition);
        bundle.putInt(Const.COMPARATOR_FACTORY, mAlbumSortKind);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        checkSandBox();
    }

    /**
     * 主要针对于拍完照回到这个界面之后判断沙盒里面还要数据没
     * 这里有延迟的原因是因为怕卡
     */
    private void checkSandBox() {
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SandBoxDBModel.getInstance().findAll().size() > 0) {
                        getContext().startService(new Intent(getContext(), SandBoxService.class));
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_sandbox_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_LOCAL) {
            final Uri uri = data.getData();
            final ContentResolver cr = getActivity().getContentResolver();
            try {
                int[] arr = FilePathUtils.getPictureSize(cr.openInputStream(uri));
                final int width = arr[0];
                final int height = arr[1];
                mProgressLayout.show();
                NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        PhotoNote photoNote = new PhotoNote(System.currentTimeMillis() + ".jpg", System.currentTimeMillis(),
                                System.currentTimeMillis(), "", "", System.currentTimeMillis(),
                                System.currentTimeMillis(), mCategoryLabel);

                        if (PhotoNoteDBModel.getInstance().save(photoNote)) {
                            //复制大图
                            try {
                                FilePathUtils.copyFile(cr.openInputStream(uri), photoNote.getBigPhotoPathWithoutFile());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //保存小图
                            FilePathUtils.saveSmallPhotoFromBigPhoto(photoNote);
                        }
                        mMainHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                    }
                });
            } catch (FileNotFoundException e) {
                YLog.e(TAG, e.getMessage());
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_CAMERA) {
            mProgressLayout.show();
            int[] arr = FilePathUtils.getPictureSize(FilePathUtils.getTempFilePath());
            final int width = arr[0];
            final int height = arr[1];
            NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
                @Override
                public void run() {
                    PhotoNote photoNote = new PhotoNote(System.currentTimeMillis() + ".jpg", System.currentTimeMillis(),
                            System.currentTimeMillis(), "", "", System.currentTimeMillis(),
                            System.currentTimeMillis(), mCategoryLabel);
                    if (PhotoNoteDBModel.getInstance().save(photoNote)) {
                        //复制大图
                        try {
                            FilePathUtils.copyFile(FilePathUtils.getTempFilePath(), photoNote.getBigPhotoPathWithoutFile());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //保存小图
                        FilePathUtils.saveSmallPhotoFromBigPhoto(photoNote);
                    }
                    mMainHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                }
            });
        } else {
            closeLayoutRevealColorView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_DATA:
                //因为是最新时间，即“图片创建事件”、“图片修改时间”、“笔记创建时间”、“笔记修改时间”，所以要么在最前面，要么在最后面
                mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mAlbumSortKind);
                mAdapter.updateDataWithoutChanged(mPhotoNoteList);
                //todo 上面那句注释可能有bug
                switch (mAlbumSortKind) {
                    case ComparatorFactory.FACTORY_CREATE_CLOSE:
                    case ComparatorFactory.FACTORY_EDITED_CLOSE:
                        mAdapter.notifyItemInserted(mPhotoNoteList.size() - 1);
                        break;
                    case ComparatorFactory.FACTORY_CREATE_FAR:
                    case ComparatorFactory.FACTORY_EDITED_FAR:
                        mAdapter.notifyItemInserted(0);
                        break;
                }
                mProgressLayout.hide();
                break;
            default:
                break;
        }
        return true;
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
        setAlbumSortKind(mAlbumSortKind, menu);
        if (isNotMenuExist) {
            sortData(ComparatorFactory.get(mAlbumSortKind));
        }
    }

    /**
     * 在menu中设置排序方式
     *
     * @param sort
     * @param menu
     */
    private void setAlbumSortKind(int sort, Menu menu) {
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
        mAlbumSortKind = sort;
    }

    /**
     * 在menu中设置排序方式
     *
     * @param sort
     * @param menuItem
     */
    private void setAlbumSortKind(int sort, MenuItem menuItem) {
        menuItem.setChecked(true);
        mAlbumSortKind = sort;
    }

    /**
     * 排序
     *
     * @param comparator
     */
    private void sortData(Comparator<PhotoNote> comparator) {
        Collections.sort(mPhotoNoteList, comparator);
        mAdapter.updateData(mPhotoNoteList);
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
                setAlbumSortKind(1, item);
                sortData(ComparatorFactory.get(mAlbumSortKind));
                break;
            case R.id.menu_sort_create_close:
                setAlbumSortKind(2, item);
                sortData(ComparatorFactory.get(mAlbumSortKind));
                break;
            case R.id.menu_sort_edit_far:
                setAlbumSortKind(3, item);
                sortData(ComparatorFactory.get(mAlbumSortKind));
                break;
            case R.id.menu_sort_edit_close:
                setAlbumSortKind(4, item);
                sortData(ComparatorFactory.get(mAlbumSortKind));
                break;
            case R.id.menu_trash:
                mAdapter.deleteSelectedPhotos();
                mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mAlbumSortKind);
                menuPreviewMode();
                break;
            case R.id.menu_all_select:
                mAdapter.selectAllPhotos();
                break;
            case R.id.menu_move:
                changeCategoryFromDialog();
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
                addCategoryDialog();
                break;
        }
        return true;
    }

    /**
     * 添加category的dialog
     */
    private void addCategoryDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edittext, null);
        final EditText editText = (EditText) v.findViewById(R.id.edit_dialog);
        new AlertDialog.Builder(getContext(), R.style.note_dialog)
                .setTitle(R.string.dialog_title_new)
                .setCancelable(false)
                .setView(v)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        int totalNumber = CategoryDBModel.getInstance().findAll().size();
                        if (!TextUtils.isEmpty(name)) {
                            Category category = new Category(name, 0, totalNumber, true);
                            boolean bool = CategoryDBModel.getInstance().saveCategory(category);
                            if (bool) {
                                HomeActivity homeActivity = (HomeActivity) getActivity();
                                homeActivity.changeCategoryAfterSaving(category);
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.toast_fail), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_fail), Toast.LENGTH_LONG).show();
                        }
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

    /**
     * 把选中图片变到另外一个Category的dialog
     *
     * @return
     */
    private String changeCategoryFromDialog() {
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        final String[] categoryLabelArray = new String[categoryList.size()];
        for (int i = 0; i < categoryLabelArray.length; i++) {
            categoryLabelArray[i] = categoryList.get(i).getLabel();
        }

        new AlertDialog.Builder(getContext(), R.style.note_dialog)
                .setTitle(R.string.dialog_title_move)
                .setItems(categoryLabelArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!mCategoryLabel.equals(categoryLabelArray[which])) {
                            mAdapter.changeCategory(categoryLabelArray[which]);
                            mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabelByForce(mCategoryLabel, mAlbumSortKind);
                            CategoryDBModel.getInstance().updateChangeCategory(mCategoryLabel, categoryLabelArray[which]);
                        }
                    }
                })
                .show();
        return null;
    }

    @Override
    public void onClick(final View v) {
        Intent intent = null;
        if (!FilePathUtils.isSDCardStoredEnough()) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_space), Toast.LENGTH_LONG).show();
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFloatingActionsMenu.collapse(false);
                    hideAlbumRevealColorView(getLocationInView(mAlbumRevealView, v));
                }
            }, 0);
            return;
        }
        switch (v.getId()) {
            case R.id.fab_camera:
                if (LocalStorageUtils.getInstance().getCameraSystem()) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri imageUri = Uri.fromFile(new File(FilePathUtils.getTempFilePath()));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, INTENT_REQUEST_CAMERA);
                } else {
                    intent = new Intent(getContext(), CameraActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.CATEGORY_LABEL, mCategoryLabel);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }
                break;
            case R.id.fab_local:
                intent = new Intent();
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
        mAlbumRevealView.reveal(p.x, p.y, getResources().getColor(R.color.bg_revealcolorview), Const.RADIUS, Const.DURATION, null);
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
        mCategoryLabel = categoryLabel;
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mAlbumSortKind);
        mAdapter.updateData(mPhotoNoteList);
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
            if (intent.getBooleanExtra(Const.TARGET_BROADCAST_PROCESS, false) ||
                    intent.getBooleanExtra(Const.TARGET_BROADCAST_SERVICE, false)) {
                mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mAlbumSortKind);
                mAdapter.updateData(mPhotoNoteList);
            } else if (intent.getBooleanExtra(Const.TARGET_BROADCAST_PHOTO, false)) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 注销广播
     */
    private void unregisterReceiver() {
        getActivity().unregisterReceiver(mUpdatePhotoNoteList);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

}
