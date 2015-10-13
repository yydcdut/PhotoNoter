package com.yydcdut.note.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuyidong on 15/8/10.
 */
public class SandSQLite extends SQLiteOpenHelper {
    public static final String TABLE = "sandbox";

    public SandSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data BLOB NOT NULL, " +
                "time LONG NOT NULL, " +
                "cameraId CHAR(1) NOT NULL, " +
                "category VARCHAR(50) NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            updateFrom1To2(db);
        }
    }

    private void updateFrom1To2(SQLiteDatabase db) {
        //镜像
        String sql1 = "ALTER TABLE " + TABLE + " ADD mirror CHAR(1) DEFAULT '0';";
        db.execSQL(sql1);
        //图片比例
        String sql2 = "ALTER TABLE " + TABLE + " ADD ratio INTEGER DEFAULT 0;";
        db.execSQL(sql2);
        //建立索引 TODO category用
//        String indexSql = "CREATE INDEX student_index on student_table(stu_no);";
    }
}
