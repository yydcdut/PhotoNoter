package com.yydcdut.note.model.sqlite;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yydcdut.note.utils.FilePathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                "data BLOB NOT NULL, " +//数据byte[],已经废弃
                "time LONG NOT NULL, " +//时间
                "cameraId CHAR(1) NOT NULL, " +//前置还是后置摄像头
                "category VARCHAR(50) NOT NULL, " +//分类//FIXME:现在变成CategoryId了
                "mirror CHAR(1) NOT NULL DEFAULT '0', " +//镜像
                "ratio INTEGER NOT NULL DEFAULT 0, " +//比例，4：3？16：9？1：1
                "orientation_ INTEGER DEFAULT 0, " +//方向
                "latitude_ VARCHAR(50), " +//经度
                "lontitude_ VARCHAR(50), " +//纬度
                "whiteBalance_ INTEGER DEFAULT 0, " +//白平衡
                "flash_ INTEGER DEFAULT 0, " +//闪光灯
                "imageLength_ INTEGER, " +//照片长度
                "imageWidth_ INTEGER, " +//照片宽度
                "make_ VARCHAR(50), " +//手机牌子
                "model_ VARCHAR(50), " +//手机牌子
                "imageFormat_ INTEGER DEFAULT 256, " +//图片格式
                "size INTEGER NOT NULL DEFAULT -1, " +//byte[]大小
                "fileName VARCHAR(100) NOT NULL DEFAULT 'X');";//文件名字

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 4 && newVersion == 5) {
            updateFrom4to5(db);
        }
        if (oldVersion == 3 && newVersion == 5) {
            updateFrom3To4(db);
            updateFrom4to5(db);
        }
        if (oldVersion == 2 && newVersion == 5) {
            updateFrom2To3(db);
            updateFrom3To4(db);
            updateFrom4to5(db);
        }
        if (oldVersion == 1 && newVersion == 5) {
            updateFrom1To2(db);
            updateFrom2To3(db);
            updateFrom3To4(db);
            updateFrom4to5(db);
        }
        if (oldVersion == 3 && newVersion == 4) {
            updateFrom3To4(db);
        }
        if (oldVersion == 2 && newVersion == 4) {
            updateFrom2To3(db);
            updateFrom3To4(db);
        }
        if (oldVersion == 1 && newVersion == 4) {
            updateFrom1To2(db);
            updateFrom2To3(db);
            updateFrom3To4(db);
        }
        if (oldVersion == 2 && newVersion == 3) {
            updateFrom2To3(db);
        }
        if (oldVersion == 1 && newVersion == 3) {
            updateFrom1To2(db);
            updateFrom2To3(db);
        }
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
    }

    private void updateFrom2To3(SQLiteDatabase db) {
        //方向
        String orientation_ = "ALTER TABLE " + TABLE + " ADD orientation_ INTEGER;";
        db.execSQL(orientation_);
        //经度
        String latitude_ = "ALTER TABLE " + TABLE + " ADD latitude_ VARCHAR(50);";
        db.execSQL(latitude_);
        //纬度
        String lontitude_ = "ALTER TABLE " + TABLE + " ADD lontitude_ VARCHAR(50);";
        db.execSQL(lontitude_);
        //白平衡
        String whiteBalance_ = "ALTER TABLE " + TABLE + " ADD whiteBalance_ INTEGER;";
        db.execSQL(whiteBalance_);
        //闪光灯
        String flash_ = "ALTER TABLE " + TABLE + " ADD flash_ INTEGER;";
        db.execSQL(flash_);
        //照片长度
        String imageLength_ = "ALTER TABLE " + TABLE + " ADD imageLength_ INTEGER;";
        db.execSQL(imageLength_);
        //照片宽度
        String imageWidth_ = "ALTER TABLE " + TABLE + " ADD imageWidth_ INTEGER;";
        db.execSQL(imageWidth_);
        //手机牌子
        String make_ = "ALTER TABLE " + TABLE + " ADD make_ VARCHAR(50);";
        db.execSQL(make_);
        //手机型号
        String model_ = "ALTER TABLE " + TABLE + " ADD model_ VARCHAR(50);";
        db.execSQL(model_);
        //增加Filename
        String fileName = "ALTER TABLE " + TABLE + " ADD fileName VARCHAR(100) NOT NULL DEFAULT 'X';";
        db.execSQL(fileName);
        //大小
        String size = "ALTER TABLE " + TABLE + " ADD size INTEGER NOT NULL DEFAULT -1;";
        db.execSQL(size);
    }

    /**
     * 因为category字段的意义变了，以前存的是label，现在存的是categoryId了
     *
     * @param db
     */
    private void updateFrom3To4(SQLiteDatabase db) {
        Cursor cursor = db.query(SandSQLite.TABLE, null, null, null, null, null, null, null);
        List<Long> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            list.add(id);
        }
        cursor.close();
        for (long id : list) {
            int rows = db.delete(SandSQLite.TABLE, "_id = ?", new String[]{id + ""});
        }
    }

    private void updateFrom4to5(SQLiteDatabase db) {
        //image格式
        String imageFormat = "ALTER TABLE " + TABLE + " ADD imageFormat_ INTEGER DEFAULT 256;";
        db.execSQL(imageFormat);
    }

    public static class DatabaseContext extends ContextWrapper {
        public DatabaseContext(Context base) {
            super(base);
        }

        /**
         * 获得数据库路径，如果不存在，则创建对象对象
         *
         * @param name
         */
        @Override
        public File getDatabasePath(String name) {
            String dbPath = FilePathUtils.getSandBoxDir() + name;//数据库路径
            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isFileCreateSuccess = true;
            }

            //返回数据库文件对象
            if (isFileCreateSuccess) {
                return dbFile;
            } else {
                return null;
            }
        }

        /**
         * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
         *
         * @param name
         * @param mode
         * @param factory
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                                   SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }

        /**
         * Android 4.0会调用此方法获取数据库。
         *
         * @param name
         * @param mode
         * @param factory
         * @param errorHandler
         * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
         * android.database.sqlite.SQLiteDatabase.CursorFactory,
         * android.database.DatabaseErrorHandler)
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                                   DatabaseErrorHandler errorHandler) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }
    }
}
