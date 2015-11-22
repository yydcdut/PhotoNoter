package com.yydcdut.note.mvp.p.service.impl;

import android.os.Handler;
import android.os.Message;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.service.ICheckServicePresenter;
import com.yydcdut.note.mvp.v.service.ICheckServiceView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/22.
 */
public class CheckServicePresenterImpl implements ICheckServicePresenter, Handler.Callback {
    private static final int QUITE = 2;
    private static final int ADD = 1;
    private AtomicInteger mNumber = new AtomicInteger(0);

    private Handler mHandler;

    private CategoryDBModel mCategoryDBModel;
    private PhotoNoteDBModel mPhotoNoteDBModel;
    private ThreadExecutorPool mThreadExecutorPool;

    private ICheckServiceView mCheckServiceView;

    @Inject
    public CheckServicePresenterImpl(CategoryDBModel categoryDBModel, PhotoNoteDBModel photoNoteDBModel,
                                     ThreadExecutorPool threadExecutorPool) {
        mCategoryDBModel = categoryDBModel;
        mPhotoNoteDBModel = photoNoteDBModel;
        mThreadExecutorPool = threadExecutorPool;
        mHandler = new Handler(this);
    }

    @Override
    public void attachView(IView iView) {
        mCheckServiceView = (ICheckServiceView) iView;
    }

    @Override
    public void detachView() {

    }

    @Override
    public void check() {
        mThreadExecutorPool.getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                checkCategoryPhotoNumber();
                checkBigAndSmallPhoto();
            }
        });
    }

    /**
     * 判断category中的pootonumber是否正确
     */
    private void checkCategoryPhotoNumber() {
        List<Category> categoryList = mCategoryDBModel.findAll();
        boolean isChanged = false;
        for (Category category : categoryList) {
            List<PhotoNote> photoNoteList = mPhotoNoteDBModel.findByCategoryLabel(category.getLabel(), -1);
            if (category.getPhotosNumber() != photoNoteList.size()) {
                category.setPhotosNumber(photoNoteList.size());
                isChanged = true;
            }
        }
        if (isChanged) {
            mCategoryDBModel.updateCategoryListInService(categoryList);
        }
        mHandler.sendEmptyMessage(ADD);
    }

    /**
     * 判断大图片和小图片是否同时存在
     */
    private void checkBigAndSmallPhoto() {
        //大图不在&小图在&数据库在，说明可能是人为删除的，所以同时把小图和数据库中的数据删除
        //小图不在&大图在&数据库在，说明可能是系统删除的，所以生成一张小图
        //数据库不在&大图小图都在，删除大图小图
        List<Category> categoryList = mCategoryDBModel.findAll();
        for (Category category : categoryList) {
            List<PhotoNote> photoNoteList = mPhotoNoteDBModel.findByCategoryLabel(category.getLabel(), -1);
            for (int i = 0; i < photoNoteList.size(); i++) {
                PhotoNote photoNote = photoNoteList.get(i);
                int result = FilePathUtils.isFileExist(photoNote.getPhotoName());
                switch (result) {
                    case FilePathUtils.ALL_NOT_EXIST:
                    case FilePathUtils.BIG_PHOTO_NOT_EXIST:
                        // java.util.ConcurrentModificationException
                        mPhotoNoteDBModel.delete(photoNote);
                        FilePathUtils.deleteAllFiles(photoNote.getPhotoName());
                        break;
                    case FilePathUtils.SMALL_PHOTO_NOT_EXIST:
                        FilePathUtils.saveSmallPhotoFromBigPhoto(photoNote);
                        break;
                    case FilePathUtils.ALL_EXIST:
                    default:
                        break;
                }
            }
        }
        mHandler.sendEmptyMessage(ADD);
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ADD:
                int number = mNumber.incrementAndGet();
                if (number == QUITE) {
                    mCheckServiceView.stopService();
                }
                break;
        }
        return false;
    }
}
