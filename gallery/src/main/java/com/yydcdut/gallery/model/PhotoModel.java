package com.yydcdut.gallery.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 16/3/19.
 */
public class PhotoModel {
    private Map<String, MediaFolder> mMediaCache;

    private PhotoModel() {
    }

    private static class Holder {
        public static final PhotoModel INSTANCE = new PhotoModel();
    }

    public static PhotoModel getInstance() {
        return Holder.INSTANCE;
    }

    @NonNull
    public Map<String, MediaFolder> findByMedia(@NonNull Context context) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        if (mMediaCache == null) {
            mMediaCache = new HashMap<>();
            MediaFolder mediaFolder4All = new MediaFolder(MediaFolder.ALL, new ArrayList<MediaPhoto>());
            mMediaCache.put(MediaFolder.ALL, mediaFolder4All);
            final String[] projectionPhotos = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.ORIENTATION,
                    MediaStore.Images.Thumbnails.DATA
            };
            Cursor cursor = MediaStore.Images.Media.query(contextWeakReference.get().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (cursor == null) {
                return mMediaCache;
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
        contextWeakReference.clear();
        return mMediaCache;
    }
}
