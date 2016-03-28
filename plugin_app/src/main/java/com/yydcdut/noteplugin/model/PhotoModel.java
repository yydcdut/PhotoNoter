package com.yydcdut.noteplugin.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.yydcdut.noteplugin.bean.FilePhoto;
import com.yydcdut.noteplugin.bean.MediaFolder;
import com.yydcdut.noteplugin.bean.MediaPhoto;
import com.yydcdut.noteplugin.bean.TreeFile;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
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


    public TreeFile findByPath() {
        if (!hasSDCard()) {
            return null;
        }
        TreeFile rootTreeFile = new FilePhoto(0, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ergodicFiles(rootTreeFile, new File(sdPath));
        return rootTreeFile;
    }

    private boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            return true;
        }
    }

    private void ergodicFiles(TreeFile root, File rootFile) {
        if (rootFile.isDirectory()) {
            for (File file : rootFile.listFiles()) {
                if (file.isDirectory()) {
                    TreeFile child = new FilePhoto(root.getLevel() + 1, file.getName(), root);
                    root.addChild(child);
                    ergodicFiles(child, file);
                } else {
                    TreeFile child = new FilePhoto(root.getLevel() + 1, file.getName(), root);
                    root.addChild(child);
                    if (isPhoto(file)) {
                        root.addCoverPhoto(file.getName());
                    }
                }
            }
        } else {
        }
    }

    private boolean isPhoto(File file) {
        String name = file.getName();
        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif")) {
            return true;
        } else {
            return false;
        }
    }

    public static Comparator<TreeFile> getComparator() {
        return new Comparator<TreeFile>() {
            @Override
            public int compare(TreeFile lhs, TreeFile rhs) {
                int maxLength = lhs.getFileName().length() > rhs.getFileName().length() ? rhs.getFileName().length() : lhs.getFileName().length();
                for (int index = 0; index < maxLength; index++) {
                    char left = lhs.getFileName().charAt(index);
                    char right = rhs.getFileName().charAt(index);
                    int comparator = left - right;
                    if (comparator > 0) {
                        return 1;
                    } else if (comparator < 0) {
                        return -1;
                    }
                }
                return lhs.getFileName().length() > rhs.getFileName().length() ? 1 : -1;
            }
        };
    }


}
