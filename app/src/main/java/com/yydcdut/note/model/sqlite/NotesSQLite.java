package com.yydcdut.note.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuyidong on 15/10/15.
 */
public class NotesSQLite extends SQLiteOpenHelper {
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_PHOTONOTE = "photonote";

    public NotesSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_category = "create table if not exists " + TABLE_CATEGORY + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "label TEXT NOT NULL, " +
                "photosNumber INTEGER NOT NULL, " +
                "isCheck INTEGER NOT NULL, " +
                "sort INTEGER NOT NULL);";
        db.execSQL(sql_category);

        String sql_photoNote = "create table if not exists " + TABLE_PHOTONOTE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "photoName TEXT NOT NULL, " +
                "createdPhotoTime LONG NOT NULL, " +
                "editedPhotoTime LONG NOT NULL, " +
                "title TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "createdNoteTime LONG NOT NULL, " +
                "editedNoteTime LONG NOT NULL, " +
                "tag INTEGER DEFAULT 0, " +//还没用
                "palette INTEGER DEFAULT " + Color.WHITE + ", " +
                "categoryId INTEGER NOT NULL DEFAULT 0, " +
                "categoryLabel TEXT NOT NULL);";//FIXME:categoryLabel已经废弃掉了
        db.execSQL(sql_photoNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            updateDBFrom1to2(db);
        }
        if (oldVersion == 2 && newVersion == 3) {
            updateCategoryFrom3to2(db);
        }
        if (oldVersion == 1 && newVersion == 3) {
            updateDBFrom1to2(db);
            updateCategoryFrom3to2(db);
        }
    }

    private void updateDBFrom1to2(SQLiteDatabase db) {
        String sql = "ALTER TABLE " + TABLE_PHOTONOTE + " ADD palette INTEGER DEFAULT " + Color.WHITE + ";";
        db.execSQL(sql);
    }

    /**
     * 回归以_id为索引的方式
     *
     * @param db
     */
    private void updateCategoryFrom3to2(SQLiteDatabase db) {
        String sql = "ALTER TABLE " + TABLE_PHOTONOTE + " ADD categoryId INTEGER NOT NULL DEFAULT 0;";
        db.execSQL(sql);
        Map<String, Integer> categoryLabel2Id = new HashMap<>();
        //获取Category
        Cursor cursorCategory = db.query(NotesSQLite.TABLE_CATEGORY, null, null, null, null, null, "sort asc");
        while (cursorCategory.moveToNext()) {
            int id = cursorCategory.getInt(cursorCategory.getColumnIndex("_id"));
            String label = cursorCategory.getString(cursorCategory.getColumnIndex("label"));
            categoryLabel2Id.put(label, id);
        }
        cursorCategory.close();
        Cursor cursorNote = db.query(NotesSQLite.TABLE_PHOTONOTE, null, null, null, null, null, null);
        while (cursorNote.moveToNext()) {
            int id = cursorNote.getInt(cursorNote.getColumnIndex("_id"));
            String categoryLabel = cursorNote.getString(cursorNote.getColumnIndex("categoryLabel"));
            int categoryId = categoryLabel2Id.get(categoryLabel) == null ? 0 : categoryLabel2Id.get(categoryLabel);
            ContentValues contentValues = new ContentValues();
            contentValues.put("categoryId", categoryId);
            db.update(NotesSQLite.TABLE_PHOTONOTE, contentValues, "_id = ?", new String[]{id + ""});
        }
        cursorNote.close();
    }


}
