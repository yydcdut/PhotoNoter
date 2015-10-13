package com.yydcdut.note.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yuyidong on 15/7/17.
 */
public class CheckService extends Service {
    private static final int QUITE = 2;
    private static final int ADD = 1;
    private AtomicInteger mNumber = new AtomicInteger(0);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD:
                    stopService(mNumber.incrementAndGet());
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                checkCategoryPhotoNumber();
            }
        });
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                checkBigAndSmallPhoto();
            }
        });
    }

    /**
     * 判断category中的pootonumber是否正确
     */
    private void checkCategoryPhotoNumber() {
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        for (Category category : categoryList) {
            List<PhotoNote> photoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(category.getLabel(), -1);
            if (category.getPhotosNumber() != photoNoteList.size()) {
                category.setPhotosNumber(photoNoteList.size());
            }
            CategoryDBModel.getInstance().updateCategoryList(categoryList);
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
        List<Category> categoryList = CategoryDBModel.getInstance().findAll();
        for (Category category : categoryList) {
            List<PhotoNote> photoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(category.getLabel(), -1);
            for (int i = 0; i < photoNoteList.size(); i++) {
                PhotoNote photoNote = photoNoteList.get(i);
                int result = FilePathUtils.isFileExist(photoNote.getPhotoName());
                switch (result) {
                    case FilePathUtils.ALL_NOT_EXIST:
                    case FilePathUtils.BIG_PHOTO_NOT_EXIST:
                        // java.util.ConcurrentModificationException
                        PhotoNoteDBModel.getInstance().delete(photoNote);
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

    /**
     * 退出
     *
     * @param number 当值满足为QUITE的时候退出
     */
    private void stopService(int number) {
        if (number == QUITE) {
            sendDataUpdateBroadcast(true, null, true, true, true);
            stopSelf();
        }
    }

    public void sendDataUpdateBroadcast(boolean delete, String move, boolean sort, boolean number, boolean photo) {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_DELETE, delete);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_MOVE, move);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_SORT, sort);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_NUMBER, number);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_PHOTO, photo);
        sendBroadcast(intent);
    }

}
