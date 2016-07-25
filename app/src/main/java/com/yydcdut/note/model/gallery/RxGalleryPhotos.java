package com.yydcdut.note.model.gallery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.yydcdut.note.entity.gallery.MediaFolder;
import com.yydcdut.note.entity.gallery.MediaPhoto;
import com.yydcdut.note.injector.ContextLife;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by yuyidong on 16/4/28.
 */
public class RxGalleryPhotos {
    private Map<String, MediaFolder> mMediaCache;

    private Context mContext;

    @Singleton
    @Inject
    public RxGalleryPhotos(@ContextLife("Application") Context context) {
        mContext = context;
    }

    @NonNull
    public Observable<Map<String, MediaFolder>> findByMedia() {
        return Observable.create(new Observable.OnSubscribe<Map<String, MediaFolder>>() {
            @Override
            public void call(Subscriber<? super Map<String, MediaFolder>> subscriber) {
                if (mMediaCache == null || mMediaCache.size() == 0) {
                    mMediaCache = new HashMap<>();
                    MediaFolder mediaFolder4All = new MediaFolder(MediaFolder.ALL, new ArrayList<MediaPhoto>());
                    mMediaCache.put(MediaFolder.ALL, mediaFolder4All);
                    findInDatabase(mediaFolder4All);
                }
                subscriber.onNext(mMediaCache);
            }
        }).subscribeOn(Schedulers.io());
    }

    private void findInDatabase(MediaFolder mediaFolder4All) {
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA
        };
        Cursor cursor = MediaStore.Images.Media.query(mContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                MediaPhoto mediaPhoto = new MediaPhoto(path, thumb);
                String folderName = file.getParentFile().getName();
                MediaFolder mediaFolder = mMediaCache.get(folderName);
                if (mediaFolder == null) {
                    List<MediaPhoto> mediaPhotoList = new ArrayList<>();
                    mediaPhotoList.add(mediaPhoto);
                    mediaFolder = new MediaFolder(folderName, mediaPhotoList);
                    mMediaCache.put(folderName, mediaFolder);
                } else {
                    mediaFolder.getMediaPhotoList().add(mediaPhoto);
                }
                mediaFolder4All.getMediaPhotoList().add(mediaPhoto);
            }
        }
    }

    public void clear() {
        mMediaCache.clear();
        mMediaCache = null;
    }

}
