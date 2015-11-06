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
                "data BLOB NOT NULL, " +//数据byte[]
                "time LONG NOT NULL, " +//时间
                "cameraId CHAR(1) NOT NULL, " +//前置还是后置摄像头
                "category VARCHAR(50) NOT NULL, " +//分类
                "mirror CHAR(1) NOT NULL DEFAULT '0', " +//镜像
                "ratio INTEGER NOT NULL DEFAULT 0, " +//比例，4：3？16：9？1：1
                "orientation1 INTEGER DEFAULT 0, " +//方向
                "latitude VARCHAR(50), " +//经度
                "lontitude VARCHAR(50), " +//纬度
                "whiteBalance INTEGER DEFAULT 0, " +//白平衡
                "flash INTEGER DEFAULT 0, " +//闪光灯
                "imageLength INTEGER, " +//照片长度
                "imageWidth INTEGER, " +//照片宽度
                "make VARCHAR(50), " +//手机牌子
                "model VARCHAR(50));";//手机型号

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        String orientation1 = "ALTER TABLE " + TABLE + " ADD orientation1 INTEGER;";
        db.execSQL(orientation1);
        //经度
        String latitude = "ALTER TABLE " + TABLE + " ADD latitude VARCHAR(50);";
        db.execSQL(latitude);
        //纬度
        String lontitude = "ALTER TABLE " + TABLE + " ADD lontitude VARCHAR(50);";
        db.execSQL(lontitude);
        //白平衡
        String whiteBalance = "ALTER TABLE " + TABLE + " ADD whiteBalance INTEGER;";
        db.execSQL(whiteBalance);
        //闪光灯
        String flash = "ALTER TABLE " + TABLE + " ADD flash INTEGER;";
        db.execSQL(flash);
        //照片长度
        String imageLength = "ALTER TABLE " + TABLE + " ADD imageLength INTEGER;";
        db.execSQL(imageLength);
        //照片宽度
        String imageWidth = "ALTER TABLE " + TABLE + " ADD imageWidth INTEGER;";
        db.execSQL(imageWidth);
        //手机牌子
        String make = "ALTER TABLE " + TABLE + " ADD make VARCHAR(50);";
        db.execSQL(make);
        //手机型号
        String model = "ALTER TABLE " + TABLE + " ADD model VARCHAR(50);";
        db.execSQL(model);


    }
}
