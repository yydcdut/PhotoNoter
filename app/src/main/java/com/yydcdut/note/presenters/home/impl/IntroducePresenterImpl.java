package com.yydcdut.note.presenters.home.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.note.R;
import com.yydcdut.note.entity.PhotoNote;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.home.IIntroducePresenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.permission.Permission;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.home.IIntroduceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/18.
 */
public class IntroducePresenterImpl implements IIntroducePresenter, Handler.Callback,
        PermissionUtils.OnPermissionCallBacks {
    private static final int QUITE = 2;
    private static final int ADD = 1;
    private static final int CHECK_FINISHED = 4399;
    private AtomicInteger mNumber = new AtomicInteger(0);

    private IIntroduceView mIntroduceView;

    private Handler mHandler;

    private Context mContext;
    private Activity mActivity;
    private RxPhotoNote mRxPhotoNote;
    private RxCategory mRxCategory;
    private LocalStorageUtils mLocalStorageUtils;

    private long mCategoryId = 1;

    @Inject
    public IntroducePresenterImpl(@ContextLife("Activity") Context context, Activity activity, RxCategory rxCategory,
                                  RxPhotoNote rxPhotoNote, LocalStorageUtils localStorageUtils) {
        mContext = context;
        mActivity = activity;
        mRxCategory = rxCategory;
        mRxPhotoNote = rxPhotoNote;
        mLocalStorageUtils = localStorageUtils;
    }

    @Override
    public void attachView(IView iView) {
        mIntroduceView = (IIntroduceView) iView;
        mHandler = new Handler(this);
        initAlbumNumber();
        initDefaultCategory();
        initDefaultPhotoNote();
    }

    @Override
    public void detachView() {

    }

    @Override
    public void wannaFinish() {
        if (isThreadFinished()) {
            mIntroduceView.jump2Album();
        } else {
            mIntroduceView.showProgressBar();
            mHandler.sendEmptyMessage(CHECK_FINISHED);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ADD:
                mNumber.incrementAndGet();
                break;
            case CHECK_FINISHED:
                if (isThreadFinished()) {
                    mIntroduceView.hideProgressBar();
                    mIntroduceView.jump2Album();
                } else {
                    mHandler.sendEmptyMessageDelayed(CHECK_FINISHED, 200);
                }
                break;
        }
        return false;
    }

    private boolean isThreadFinished() {
        int number = mNumber.get();
        if (number == QUITE) {
            return true;
        } else {
            return false;
        }
    }

    private void initAlbumNumber() {
        new Thread(() -> {
            int screenWidth = Utils.sScreenWidth;
            int num = 2;
            if (screenWidth <= 480) {
                num = 2;
            } else if (screenWidth <= 720) {
                num = 3;
            } else if (screenWidth <= 1080) {
                num = 4;
            } else {
                num = 5;
            }
            mLocalStorageUtils.setAlbumItemNumber(num);
        }).start();
    }


    /**
     * 处理Category
     */
    private void initDefaultCategory() {
        mRxCategory.saveCategory("App介绍", 16, 0, true)
                .subscribe(categories -> mHandler.sendEmptyMessage(ADD),
                        (throwable -> YLog.e(throwable)));
    }

    @Permission(PermissionUtils.CODE_STORAGE)
    private void initDefaultPhotoNote() {
        boolean hasPermission = PermissionUtils.hasPermission4Storage(mContext);
        if (hasPermission) {
            new Thread(() -> {
                FilePathUtils.initDirs();
                String[] outFileName = new String[]{
                        "intro_all.jpg",
                        "intro_md.jpg"
                };
                String[] titles = new String[]{
                        Const.INTRODUCE_TITLE,
                        Const.MARKDOWN_TITLE
                };
                String[] contents = new String[]{
                        Const.INTRODUCE_CONTENT,
                        Const.MARKDOWN_CONTENT
                };
                boolean bool = false;
                try {
                    bool = takePhotosToSdCard(outFileName);
                } catch (IOException e) {
                    YLog.e(e);
                }
                if (!bool) {
                    //如果没有成功，就走这里，不走存PhotoNote的逻辑了
                    mHandler.sendEmptyMessage(ADD);
                    return;
                }
                ArrayList<PhotoNote> arrayList = new ArrayList<PhotoNote>(outFileName.length);
                for (int i = 0; i < outFileName.length; i++) {
                    PhotoNote photoNote = new PhotoNote(outFileName[i], System.currentTimeMillis(), System.currentTimeMillis(),
                            titles[i], contents[i], System.currentTimeMillis(), System.currentTimeMillis(), (int) mCategoryId);
                    photoNote.setPaletteColor(Utils.getPaletteColor(ImageLoaderManager.loadImageSync(photoNote.getBigPhotoPathWithFile())));
                    arrayList.add(photoNote);
                }
                mRxPhotoNote.savePhotoNotes(arrayList)
                        .subscribe(photoNoteList -> mHandler.sendEmptyMessage(ADD),
                                (throwable -> YLog.e(throwable)));
            }).start();
        } else {
            PermissionUtils.requestPermissionsWithDialog(mActivity, mContext.getString(R.string.permission_storage_init),
                    PermissionUtils.PERMISSION_STORAGE, PermissionUtils.CODE_STORAGE);
        }
    }

    private boolean takePhotosToSdCard(String[] outFileName) throws IOException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        String path = FilePathUtils.getPath();
        for (int i = 0; i < outFileName.length; i++) {
            FilePathUtils.copyFile(mContext.getResources().getAssets().open(outFileName[i]), path + outFileName[i]);
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file:/" + path + outFileName[i]);
            FilePathUtils.saveSmallPhoto(outFileName[i], bitmap);
        }
        return true;
    }

    @Override
    public void onPermissionsGranted(List<String> permissions) {
    }

    @Override
    public void onPermissionsDenied(List<String> permissions) {
        if (permissions != null && !permissions.isEmpty()) {
            PermissionUtils.requestPermissionsWithDialog(mActivity, mContext.getString(R.string.permission_storage_init),
                    PermissionUtils.PERMISSION_STORAGE, PermissionUtils.CODE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
