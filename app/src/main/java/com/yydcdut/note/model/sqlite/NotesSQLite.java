package com.yydcdut.note.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                "categoryLabel TEXT NOT NULL);";
        db.execSQL(sql_photoNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
