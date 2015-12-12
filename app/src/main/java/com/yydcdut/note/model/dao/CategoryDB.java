package com.yydcdut.note.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.model.sqlite.AbsNotesDBModel;
import com.yydcdut.note.model.sqlite.NotesSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/11/26.
 */
public class CategoryDB extends AbsNotesDBModel {

    public CategoryDB(Context context) {
        super(context);
    }

    /**
     * 获取数据
     *
     * @return Categories
     */
    public synchronized List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        Cursor cursor = db.query(NotesSQLite.TABLE_CATEGORY, null, null, null, null, null, "sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String label = cursor.getString(cursor.getColumnIndex("label"));
            int photosNumber = cursor.getInt(cursor.getColumnIndex("photosNumber"));
            boolean isCheck = cursor.getInt(cursor.getColumnIndex("isCheck")) == 0 ? false : true;
            int sort = cursor.getInt(cursor.getColumnIndex("sort"));
            Category category = new Category(id, label, photosNumber, sort, isCheck);
            list.add(category);
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized long save(String label, int photosNumber, int sort, boolean isCheck) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", label);
        contentValues.put("photosNumber", photosNumber);
        contentValues.put("isCheck", isCheck ? 1 : 0);
        contentValues.put("sort", sort);
        long id = db.insert(NotesSQLite.TABLE_CATEGORY, null, contentValues);
        db.close();
        return id;
    }

    /**
     * 更新Category
     *
     * @param categories 需要更新的分类
     * @return
     */
    public synchronized int update(Category... categories) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        db.beginTransaction();
        int rows = 0;
        try {
            for (Category category : categories) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("label", category.getLabel());
                contentValues.put("photosNumber", category.getPhotosNumber());
                contentValues.put("isCheck", category.isCheck() ? 1 : 0);
                contentValues.put("sort", category.getSort());
                rows += db.update(NotesSQLite.TABLE_CATEGORY, contentValues, "_id = ?", new String[]{category.getId() + ""});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return -1;
        } finally {
            db.endTransaction();
            db.close();
        }
        return rows;
    }


    /**
     * 删除分类
     *
     * @param categories
     * @return
     */
    public synchronized int delete(Category... categories) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        db.beginTransaction();
        int rows = 0;
        try {
            for (Category category : categories) {
                rows += db.delete(NotesSQLite.TABLE_CATEGORY, "_id = ?", new String[]{category.getId() + ""});
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
}
