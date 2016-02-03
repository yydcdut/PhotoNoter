package com.yydcdut.note.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.compare.ComparatorFactory;
import com.yydcdut.note.model.rx.RxCategory;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;

import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 15/7/17.
 */
public class CheckService extends IntentService {

    private RxCategory mRxCategory;
    private RxPhotoNote mRxPhotoNote;

    public CheckService() {
        super("com.yydcdut.note.service.CheckService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mRxCategory = ((NoteApplication) getApplication()).getApplicationComponent().getRxCategory();
        mRxPhotoNote = ((NoteApplication) getApplication()).getApplicationComponent().getRxPhotoNote();
        checkCategoryPhotoNumber();
        checkBigAndSmallPhoto();
    }

    /**
     * 判断category中的photoNumber是否正确
     */
    private void checkCategoryPhotoNumber() {
        mRxCategory.getAllCategories()
                .subscribe(categories -> {
                    for (Category category : categories) {
                        mRxPhotoNote.findByCategoryId(category.getId(), ComparatorFactory.FACTORY_NOT_SORT)
                                .subscribe(photoNoteList1 -> {
                                    if (category.getPhotosNumber() != photoNoteList1.size()) {
                                        category.setPhotosNumber(photoNoteList1.size());
                                        mRxCategory.updateCategory(category).subscribe();
                                    }
                                });
                    }
                });
    }

    /**
     * 判断大图片和小图片是否同时存在
     */
    private void checkBigAndSmallPhoto() {
        //大图不在&小图在&数据库在，说明可能是人为删除的，所以同时把小图和数据库中的数据删除
        //小图不在&大图在&数据库在，说明可能是系统删除的，所以生成一张小图
        //数据库不在&大图小图都在，删除大图小图
        mRxCategory.getAllCategories()
                .subscribe(categories -> {
                    for (Category category : categories) {
                        mRxPhotoNote.findByCategoryId(category.getId(), ComparatorFactory.FACTORY_NOT_SORT)
                                .subscribe(photoNoteList -> {
                                    for (PhotoNote photoNote : photoNoteList) {
                                        int result = FilePathUtils.isFileExist(photoNote.getPhotoName());
                                        switch (result) {
                                            case FilePathUtils.ALL_NOT_EXIST:
                                            case FilePathUtils.BIG_PHOTO_NOT_EXIST:
                                                // java.util.ConcurrentModificationException
                                                deletePhotoAndFiles(photoNote);
                                                break;
                                            case FilePathUtils.SMALL_PHOTO_NOT_EXIST:
                                                FilePathUtils.saveSmallPhotoFromBigPhoto(photoNote.getBigPhotoPathWithFile(), photoNote.getPhotoName());
                                                break;
                                            case FilePathUtils.ALL_EXIST:
                                            default:
                                                break;
                                        }
                                    }
                                });
                    }
                });
    }

    private void deletePhotoAndFiles(PhotoNote photoNote) {
        String fileName = photoNote.getPhotoName();
        mRxPhotoNote.deletePhotoNote(photoNote)
                .observeOn(Schedulers.io())
                .subscribe(photoNoteList -> FilePathUtils.deleteAllFiles(fileName));
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_SERVICE, true);
        sendBroadcast(intent);
        super.onDestroy();

    }
}
