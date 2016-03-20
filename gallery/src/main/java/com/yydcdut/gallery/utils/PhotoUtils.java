package com.yydcdut.gallery.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yydcdut.gallery.model.MediaFolder;
import com.yydcdut.gallery.model.MediaPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 16/3/19.
 */
public class PhotoUtils {

    @Nullable
    public static Map<String, MediaFolder> findByMedia(@NonNull Context context) {
        Map<String, MediaFolder> mediaFolderByNameMap = new HashMap<>();
        MediaFolder mediaFolder4All = new MediaFolder(MediaFolder.ALL, new ArrayList<MediaPhoto>());
        mediaFolderByNameMap.put(MediaFolder.ALL, mediaFolder4All);
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Thumbnails.DATA
        };
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                MediaPhoto mediaPhoto = new MediaPhoto(path, thumb);
                String folderName = file.getParentFile().getName();
                MediaFolder mediaFolder = mediaFolderByNameMap.get(folderName);
                if (mediaFolder == null) {
                    List<MediaPhoto> mediaPhotoList = new ArrayList<>();
                    mediaPhotoList.add(mediaPhoto);
                    mediaFolder = new MediaFolder(folderName, mediaPhotoList);
                    mediaFolderByNameMap.put(folderName, mediaFolder);
                } else {
                    mediaFolder.getMediaPhotoList().add(mediaPhoto);
                }
                mediaFolder4All.getMediaPhotoList().add(mediaPhoto);
            }
        }
        return mediaFolderByNameMap;
    }
}
