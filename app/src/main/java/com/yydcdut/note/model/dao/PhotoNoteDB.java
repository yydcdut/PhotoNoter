package com.yydcdut.note.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.sqlite.AbsNotesDBModel;
import com.yydcdut.note.model.sqlite.NotesSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/11/27.
 */
public class PhotoNoteDB extends AbsNotesDBModel {
    public PhotoNoteDB(Context context) {
        super(context);
    }

    public synchronized List<PhotoNote> findByCategoryId(int categoryId) {
        List<PhotoNote> list = new ArrayList<>();
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        Cursor cursor = db.query(NotesSQLite.TABLE_PHOTONOTE, null, "categoryId = ?", new String[]{categoryId + ""}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String photoName = cursor.getString(cursor.getColumnIndex("photoName"));
            long createdPhotoTime = cursor.getLong(cursor.getColumnIndex("createdPhotoTime"));
            long editedPhotoTime = cursor.getLong(cursor.getColumnIndex("editedPhotoTime"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long createdNoteTime = cursor.getLong(cursor.getColumnIndex("createdNoteTime"));
            long editedNoteTime = cursor.getLong(cursor.getColumnIndex("editedNoteTime"));
            int color = cursor.getInt(cursor.getColumnIndex("palette"));
            int tag = cursor.getInt(cursor.getColumnIndex("tag"));
            int categoryId_ = cursor.getInt(cursor.getColumnIndex("categoryId"));
            PhotoNote photoNote = new PhotoNote(id, photoName, createdPhotoTime, editedPhotoTime, title, content,
                    createdNoteTime, editedNoteTime, categoryId_);
            photoNote.setPaletteColor(color);
            list.add(photoNote);
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized PhotoNote findByPhotoNoteId(long photoNoteId) {
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        Cursor cursor = db.query(NotesSQLite.TABLE_PHOTONOTE, null, "_id = ?", new String[]{photoNoteId + ""}, null, null, null);
        PhotoNote photoNote = null;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String photoName = cursor.getString(cursor.getColumnIndex("photoName"));
            long createdPhotoTime = cursor.getLong(cursor.getColumnIndex("createdPhotoTime"));
            long editedPhotoTime = cursor.getLong(cursor.getColumnIndex("editedPhotoTime"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long createdNoteTime = cursor.getLong(cursor.getColumnIndex("createdNoteTime"));
            long editedNoteTime = cursor.getLong(cursor.getColumnIndex("editedNoteTime"));
            int color = cursor.getInt(cursor.getColumnIndex("palette"));
            int tag = cursor.getInt(cursor.getColumnIndex("tag"));
            int categoryId_ = cursor.getInt(cursor.getColumnIndex("categoryId"));
            photoNote = new PhotoNote(id, photoName, createdPhotoTime, editedPhotoTime, title, content,
                    createdNoteTime, editedNoteTime, categoryId_);
            photoNote.setPaletteColor(color);
        }
        cursor.close();
        db.close();
        return photoNote;
    }

    public synchronized int update(PhotoNote... photoNotes) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        int rows = 0;
        for (PhotoNote photoNote : photoNotes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("photoName", photoNote.getPhotoName());
            contentValues.put("createdPhotoTime", photoNote.getCreatedPhotoTime());
            contentValues.put("editedPhotoTime", photoNote.getEditedPhotoTime());
            contentValues.put("title", photoNote.getTitle());
            contentValues.put("content", photoNote.getContent());
            contentValues.put("createdNoteTime", photoNote.getCreatedNoteTime());
            contentValues.put("editedNoteTime", photoNote.getEditedNoteTime());
            contentValues.put("palette", photoNote.getPaletteColor());
//            contentValues.put("tag", );
            contentValues.put("categoryId", photoNote.getCategoryId());
            contentValues.put("categoryLabel", "");
            rows += db.update(NotesSQLite.TABLE_PHOTONOTE, contentValues, "_id = ?", new String[]{photoNote.getId() + ""});
        }
        db.close();
        return rows;
    }

    public synchronized long save(PhotoNote... photoNotes) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        db.beginTransaction();
        long id = -1;
        try {
            for (PhotoNote photoNote : photoNotes) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("photoName", photoNote.getPhotoName());
                contentValues.put("createdPhotoTime", photoNote.getCreatedPhotoTime());
                contentValues.put("editedPhotoTime", photoNote.getEditedPhotoTime());
                contentValues.put("title", photoNote.getTitle());
                contentValues.put("content", photoNote.getContent());
                contentValues.put("createdNoteTime", photoNote.getCreatedNoteTime());
                contentValues.put("editedNoteTime", photoNote.getEditedNoteTime());
                contentValues.put("palette", photoNote.getPaletteColor());
                //            contentValues.put("tag", );
                contentValues.put("categoryId", photoNote.getCategoryId());
                contentValues.put("categoryLabel", "");
                id = db.insert(NotesSQLite.TABLE_PHOTONOTE, null, contentValues);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction();
            db.close();
        }
        return id;
    }

    public synchronized boolean isExistInDB(PhotoNote photoNote) {
        int categoryId = photoNote.getCategoryId();
        List<PhotoNote> photoNoteList = findByCategoryId(categoryId);
        for (PhotoNote item : photoNoteList) {
            if (item.getId() == photoNote.getId()) {
                return true;
            }
        }
        return false;
    }

    public synchronized int delete(PhotoNote... photoNotes) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        db.beginTransaction();
        int rows = 0;
        try {
            for (PhotoNote photoNote : photoNotes) {
                rows = db.delete(NotesSQLite.TABLE_PHOTONOTE, "_id = ?", new String[]{photoNote.getId() + ""});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction(); //处理完成
            db.close();
        }
        return rows;
    }

    public synchronized int getAllNumber() {
        int number = 0;
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        String sql = "select count(*) from " + NotesSQLite.TABLE_PHOTONOTE + ";";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            number = cursor.getInt(cursor.getColumnIndex("count(*)"));
        }
        cursor.close();
        db.close();
        return number;
    }

    public synchronized int getWordsNumber() {
        int number = 0;
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        String sql = "select SUM(LENGTH(title))+SUM(LENGTH(content)) from photonote;";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            number = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return number;
    }

}
