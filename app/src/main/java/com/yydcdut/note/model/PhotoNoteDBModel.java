package com.yydcdut.note.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.sqlite.NotesSQLite;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.compare.ComparatorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/7/17.
 * 先进行数据库操作，然后再操作动画之类的
 */
public class PhotoNoteDBModel extends AbsNotesDBModel implements IModel {

    private static PhotoNoteDBModel sInstance = new PhotoNoteDBModel();

    private Map<String, List<PhotoNote>> mCache = new HashMap<>();

    private PhotoNoteDBModel() {
    }

    public static PhotoNoteDBModel getInstance() {
        return sInstance;
    }

    public List<PhotoNote> findByCategoryLabel(String categoryLabel, int comparatorFactory) {
        List<PhotoNote> list = mCache.get(categoryLabel);
        if (null == list) {
            list = findDataByLabel(categoryLabel);
            mCache.put(categoryLabel, list);
        }
        if (comparatorFactory != -1) {
            Collections.sort(list, ComparatorFactory.get(comparatorFactory));
        }
        return list;
    }

    public List<PhotoNote> findByCategoryLabelByForce(String categoryLabel, int comparatorFactory) {
        List<PhotoNote> list = findDataByLabel(categoryLabel);
        mCache.remove(categoryLabel);
        mCache.put(categoryLabel, list);
        if (comparatorFactory != -1) {
            Collections.sort(list, ComparatorFactory.get(comparatorFactory));
        }
        return list;
    }

    private boolean refresh(String categoryLabel) {
        mCache.remove(categoryLabel);
        mCache.put(categoryLabel, findDataByLabel(categoryLabel));
        return true;
    }

    public boolean update(PhotoNote photoNote) {
        boolean bool = updateData(photoNote);
        refresh(photoNote.getCategoryLabel());
        return bool;
    }

    public boolean save(PhotoNote photoNote) {
        boolean bool = true;
        if (isSaved(photoNote)) {
            bool &= updateData(photoNote);
        } else {
            bool &= saveData(photoNote) >= 0;
        }
        if (bool) {
            bool &= refresh(photoNote.getCategoryLabel());
        }
        return bool;
    }

    public void delete(PhotoNote photoNote) {
        if (isSaved(photoNote)) {
            mCache.get(photoNote.getCategoryLabel()).remove(photoNote);
            //注意 java.util.ConcurrentModificationException
            deleteData(photoNote);
            FilePathUtils.deleteAllFiles(photoNote.getPhotoName());
        }
    }

    public void deleteByCategory(String categoryLabel) {
        List<PhotoNote> photoNoteList = findByCategoryLabel(categoryLabel, -1);
        List<PhotoNote> wait4Delete = new ArrayList<>(photoNoteList.size());
        for (int i = 0; i < photoNoteList.size(); i++) {
            wait4Delete.add(photoNoteList.get(i));
        }
        for (int i = 0; i < wait4Delete.size(); i++) {
            delete(wait4Delete.get(i));
        }
        mCache.remove(categoryLabel);
    }

    public void updateAll(List<PhotoNote> photoNoteList) {
        if (photoNoteList.size() <= 0) {
            return;
        }
        String category = photoNoteList.get(0).getCategoryLabel();
        for (PhotoNote photoNote : photoNoteList) {
            updateData(photoNote);
        }
        refresh(category);
    }

    private List<PhotoNote> findDataByLabel(String categoryLabel2find) {
        List<PhotoNote> list = new ArrayList<>();
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        Cursor cursor = db.query(NotesSQLite.TABLE_PHOTONOTE, null, "categoryLabel = ?", new String[]{categoryLabel2find}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String photoName = cursor.getString(cursor.getColumnIndex("photoName"));
            long createdPhotoTime = cursor.getLong(cursor.getColumnIndex("createdPhotoTime"));
            long editedPhotoTime = cursor.getLong(cursor.getColumnIndex("editedPhotoTime"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long createdNoteTime = cursor.getLong(cursor.getColumnIndex("createdNoteTime"));
            long editedNoteTime = cursor.getLong(cursor.getColumnIndex("editedNoteTime"));
            int tag = cursor.getInt(cursor.getColumnIndex("tag"));
            String categoryLabel = cursor.getString(cursor.getColumnIndex("categoryLabel"));
            PhotoNote photoNote = new PhotoNote(id, photoName, createdPhotoTime, editedPhotoTime, title, content,
                    createdNoteTime, editedNoteTime, categoryLabel);
            list.add(photoNote);
        }
        cursor.close();
        db.close();
        return list;
    }

    private boolean updateData(PhotoNote photoNote) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("photoName", photoNote.getPhotoName());
        contentValues.put("createdPhotoTime", photoNote.getCreatedPhotoTime());
        contentValues.put("editedPhotoTime", photoNote.getEditedPhotoTime());
        contentValues.put("title", photoNote.getTitle());
        contentValues.put("content", photoNote.getContent());
        contentValues.put("createdNoteTime", photoNote.getCreatedNoteTime());
        contentValues.put("editedNoteTime", photoNote.getEditedNoteTime());
        contentValues.put("categoryLabel", photoNote.getCategoryLabel());
        int rows = db.update(NotesSQLite.TABLE_PHOTONOTE, contentValues, "_id = ?", new String[]{photoNote.getId() + ""});
        db.close();
        return rows >= 0;
    }

    private long saveData(PhotoNote photoNote) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("photoName", photoNote.getPhotoName());
        contentValues.put("createdPhotoTime", photoNote.getCreatedPhotoTime());
        contentValues.put("editedPhotoTime", photoNote.getEditedPhotoTime());
        contentValues.put("title", photoNote.getTitle());
        contentValues.put("content", photoNote.getContent());
        contentValues.put("createdNoteTime", photoNote.getCreatedNoteTime());
        contentValues.put("editedNoteTime", photoNote.getEditedNoteTime());
        contentValues.put("categoryLabel", photoNote.getCategoryLabel());
        long id = db.insert(NotesSQLite.TABLE_PHOTONOTE, null, contentValues);
        db.close();
        return id;
    }

    private boolean isSaved(PhotoNote photoNote) {
        String categoryLabel = photoNote.getCategoryLabel();
        List<PhotoNote> photoNoteList = findByCategoryLabel(categoryLabel, ComparatorFactory.FACTORY_NOT_SORT);
        for (PhotoNote item : photoNoteList) {
            if (item.getId() == photoNote.getId()) {
                return true;
            }
        }
        return false;
    }

    private int deleteData(PhotoNote photoNote) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        int rows = db.delete(NotesSQLite.TABLE_PHOTONOTE, "_id = ?", new String[]{photoNote.getId() + ""});
        db.close();
        return rows;
    }
}
