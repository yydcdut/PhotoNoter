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
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.home.IIntroducePresenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.Utils;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
    }


    /**
     * 处理Category
     */
    private void initDefaultCategory() {
        mRxCategory.saveCategory("App介绍", 16, 0, true)
                .subscribe(categories -> mHandler.sendEmptyMessage(ADD));
    }

    @Permission(PermissionUtils.CODE_STORAGE)
    private void initDefaultPhotoNote() {
        boolean hasPermission = PermissionUtils.hasPermission4Storage(mContext);
        if (hasPermission) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FilePathUtils.initEnvironment(mContext);
                    String[] outFileName = new String[]{
                            "s0.png",
                            "s1.png",
                            "s2.png",
                            "s3.png",
                            "s4.png",
                            "s5.png",
                            "s6.png",
                            "s7.png",
                            "s8.png",
                            "s9.png",
                            "s10.png",
                            "s11.png",
                            "s12.png",
                            "s13.png",
                            "s14.png",
                            "s15.png",
                    };
                    String[] titles = new String[]{
                            "欢迎界面",//1
                            "相册界面（选择状态）",//2
                            "功能选择",//3
                            "分类界面",//4
                            "登录界面",//5
                            "设置界面",//6
                            "编辑分类（设置）",//7
                            "编辑分类（设置）",//8
                            "App相机界面",//9
                            "App相机比例界面",//10
                            "App相机参数界面",//11
                            "详情界面",//12
                            "文字编辑界面",//13
                            "详情界面",//14
                            "详情界面切换",//15
                            "图片详情界面",//16
                    };
                    String[] contents = new String[]{
                            "可以在设置中关闭欢迎界面",
                            "已开始进入相册界面是非选择界面，菜单选项为“新建分类”、“排序类型”、“进入选择状态”、“设置”，当单击图片之后进入详情页面；" +
                                    "长点击某张图片便可以进入选择界面。当进入选择界面的时候，菜单变为“全选”、“移动到其他分类”、“删除”，当单击其他图片时，图片状态变为选中或者非选中状态。",
                            "当点击FloatingActionButton时弹出功能选择框，依次是“拍照”、“本地”，拍照可使用系统相机，但是如果这样，每次只能拍一张照片，而使用App相机，可以连续多拍，更多相机参数选项等，可以在设置中选择相机功能；" +
                                    "接下来是本地，本地是将本地的照片复制到 照片笔记 当中来。",
                            "当从屏幕最左边往右滑动或者点击最左上菜单按钮的时候弹出分类界面，在这里可以登录第三方帐号、可以查看和选择分类、查看云空间大小。",
                            "登录界面中可以第三方平台登录，提供多种登录方式，同时可以登录多格帐号。",
                            "设置界面中用户可以根据自己的喜好选择主题、选择字体、编辑分类、处理第三方帐号、设置相机等。",
                            "设置里面的编辑分类功能，将用户创建的分类全部显示在这里，分别左滑和右滑显示不同的菜单",
                            "设置里面的编辑分类功能，将用户创建的分类全部显示在这里，分别左滑和右滑显示不同的菜单",
                            "App相机中下方按钮分别是“设置比例和延迟拍照事件”、“拍照”、“相机参数（前置后置摄像头、闪光灯、白平衡等）”。",
                            "1:1、4:3、Full可拍摄三中比例图片，延迟拍照时间分别为：关闭、3s、5s、10s和15s。",
                            "界面下方按钮分别是前后置摄像头、闪光灯、手电筒、白平衡、缩放、拍照声音、GPS和放个辅助拍照",
                            "在详情界面中、图片部分与文字部分的比例大约在7：3，文字部分如果被覆盖的话可往上滑动，文字部分一次是“标题”、“内容”、“GPS”和“事件”。" +
                                    "点击图片进入图片详情界面；点击FloatingActionButton进入文字编辑界面。",
                            "文字编辑界面，上方是标题，下方是内容",
                            "文字编辑之后返回的效果",
                            "详情界面切换效果",
                            "图片详情界面，菜单中的选项分别是：“滤镜”、“照片信息”、“收起菜单”"
                    };
                    boolean bool = false;
                    try {
                        bool = takePhotosToSdCard(outFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
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
                            .subscribe(photoNoteList -> mHandler.sendEmptyMessage(ADD));
                }
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
