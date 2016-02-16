package com.yydcdut.note.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.bean.SandExif;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.sqlite.SandSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/11/27.
 */
public class SandBoxDB {
    private static final String NAME = "SandBox.db";
    private static final int VERSION = 5;

    private SandSQLite mSandSQLite;

    public SandBoxDB(Context context) {
        mSandSQLite = new SandSQLite(context, NAME, null, VERSION);
    }

    public synchronized SandPhoto findFirstOne() {
        SandPhoto sandPhoto = null;
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null, null, null, null, null, null, "0,1");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            String cameraId = cursor.getString(cursor.getColumnIndex("cameraId"));
            String categoryIdString = cursor.getString(cursor.getColumnIndex("category"));
            int categoryId;
            try {
                categoryId = Integer.parseInt(categoryIdString);
            } catch (Exception e) {
                categoryId = -1;
            }
            String isMirrorString = cursor.getString(cursor.getColumnIndex("mirror"));
            boolean isMirror = isMirrorString.equals("0") ? false : true;
            int ratio = cursor.getInt(cursor.getColumnIndex("ratio"));
            String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            int size = cursor.getInt(cursor.getColumnIndex("size"));

            int orientation1 = cursor.getInt(cursor.getColumnIndex("orientation_"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude_"));
            String lontitude = cursor.getString(cursor.getColumnIndex("lontitude_"));
            int whiteBalance = cursor.getInt(cursor.getColumnIndex("whiteBalance_"));
            int flash = cursor.getInt(cursor.getColumnIndex("flash_"));
            int imageLength = cursor.getInt(cursor.getColumnIndex("imageLength_"));
            int imageWidth = cursor.getInt(cursor.getColumnIndex("imageWidth_"));
            String make = cursor.getString(cursor.getColumnIndex("make_"));
            String model = cursor.getString(cursor.getColumnIndex("model_"));
            int imageFormat = cursor.getInt(cursor.getColumnIndex("imageFormat_"));

            SandExif sandExif = new SandExif(orientation1, latitude, lontitude, whiteBalance, flash,
                    imageLength, imageWidth, make, model);
            sandPhoto = new SandPhoto(id, time, cameraId, categoryId, isMirror,
                    ratio, fileName, size, imageFormat, sandExif);
        }
        cursor.close();
        db.close();
        return sandPhoto;
    }

    public synchronized List<SandPhoto> finaAll() {
        List<SandPhoto> sandPhotoList = new ArrayList<>();
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            String cameraId = cursor.getString(cursor.getColumnIndex("cameraId"));
            String categoryIdString = cursor.getString(cursor.getColumnIndex("category"));
            int categoryId;
            try {
                categoryId = Integer.parseInt(categoryIdString);
            } catch (Exception e) {
                categoryId = -1;
            }
            String isMirrorString = cursor.getString(cursor.getColumnIndex("mirror"));
            boolean isMirror = isMirrorString.equals("0") ? false : true;
            int ratio = cursor.getInt(cursor.getColumnIndex("ratio"));
            String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            int size = cursor.getInt(cursor.getColumnIndex("size"));

            int orientation1 = cursor.getInt(cursor.getColumnIndex("orientation_"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude_"));
            String lontitude = cursor.getString(cursor.getColumnIndex("lontitude_"));
            int whiteBalance = cursor.getInt(cursor.getColumnIndex("whiteBalance_"));
            int flash = cursor.getInt(cursor.getColumnIndex("flash_"));
            int imageLength = cursor.getInt(cursor.getColumnIndex("imageLength_"));
            int imageWidth = cursor.getInt(cursor.getColumnIndex("imageWidth_"));
            String make = cursor.getString(cursor.getColumnIndex("make_"));
            String model = cursor.getString(cursor.getColumnIndex("model_"));
            int imageFormat = cursor.getInt(cursor.getColumnIndex("imageFormat_"));

            SandExif sandExif = new SandExif(orientation1, latitude, lontitude, whiteBalance, flash,
                    imageLength, imageWidth, make, model);
            SandPhoto sandPhoto = new SandPhoto(id, time, cameraId, categoryId, isMirror,
                    ratio, fileName, size, imageFormat, sandExif);
            sandPhotoList.add(sandPhoto);
        }
        cursor.close();
        db.close();
        return sandPhotoList;
    }

    public synchronized SandPhoto findById(long _id) {
        SandPhoto sandPhoto = null;
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null, "_id = ?", new String[]{_id + ""}, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            String cameraId = cursor.getString(cursor.getColumnIndex("cameraId"));
            String categoryIdString = cursor.getString(cursor.getColumnIndex("category"));
            int categoryId;
            try {
                categoryId = Integer.parseInt(categoryIdString);
            } catch (Exception e) {
                categoryId = -1;
            }
            String isMirrorString = cursor.getString(cursor.getColumnIndex("mirror"));
            boolean isMirror = isMirrorString.equals("0") ? false : true;
            int ratio = cursor.getInt(cursor.getColumnIndex("ratio"));
            String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
            int size = cursor.getInt(cursor.getColumnIndex("size"));

            int orientation1 = cursor.getInt(cursor.getColumnIndex("orientation_"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude_"));
            String lontitude = cursor.getString(cursor.getColumnIndex("lontitude_"));
            int whiteBalance = cursor.getInt(cursor.getColumnIndex("whiteBalance_"));
            int flash = cursor.getInt(cursor.getColumnIndex("flash_"));
            int imageLength = cursor.getInt(cursor.getColumnIndex("imageLength_"));
            int imageWidth = cursor.getInt(cursor.getColumnIndex("imageWidth_"));
            String make = cursor.getString(cursor.getColumnIndex("make_"));
            String model = cursor.getString(cursor.getColumnIndex("model_"));
            int imageFormat = cursor.getInt(cursor.getColumnIndex("imageFormat_"));

            SandExif sandExif = new SandExif(orientation1, latitude, lontitude, whiteBalance, flash,
                    imageLength, imageWidth, make, model);
            sandPhoto = new SandPhoto(id, time, cameraId, categoryId, isMirror,
                    ratio, fileName, size, imageFormat, sandExif);
        }
        cursor.close();
        db.close();
        return sandPhoto;
    }

    /**
     * 保存
     *
     * @param sandPhoto
     * @return
     */
    public synchronized long save(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", new byte[]{'a'});
        contentValues.put("time", sandPhoto.getTime());
        contentValues.put("cameraId", sandPhoto.getCameraId());
        contentValues.put("category", sandPhoto.getCategoryId() + "");
        contentValues.put("mirror", sandPhoto.isMirror());
        contentValues.put("ratio", sandPhoto.getRatio());
        contentValues.put("fileName", sandPhoto.getFileName());
        contentValues.put("size", sandPhoto.getSize());
        contentValues.put("imageFormat_", sandPhoto.getImageFormat());

        SandExif sandExif = sandPhoto.getSandExif();
        contentValues.put("orientation_", sandExif.getOrientation());
        contentValues.put("latitude_", sandExif.getLatitude());
        contentValues.put("lontitude_", sandExif.getLongitude());
        contentValues.put("whiteBalance_", sandExif.getWhiteBalance());
        contentValues.put("flash_", sandExif.getFlash());
        contentValues.put("imageLength_", sandExif.getImageLength());
        contentValues.put("imageWidth_", sandExif.getImageWidth());
        contentValues.put("make_", sandExif.getMake());
        contentValues.put("model_", sandExif.getModel());
        long id = db.insert(SandSQLite.TABLE, null, contentValues);
        db.close();
        return id;
    }

    /**
     * 删除
     *
     * @param sandPhoto
     * @return
     */
    public synchronized int delete(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        int rows = db.delete(SandSQLite.TABLE, "_id = ?", new String[]{sandPhoto.getId() + ""});
        db.close();
        return rows;
    }

    public synchronized int getAllNumber() {
        int number = 0;
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        String sql = "select count(*) from " + SandSQLite.TABLE + ";";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            number = cursor.getInt(cursor.getColumnIndex("count(*)"));
        }
        cursor.close();
        db.close();
        return number;
    }

}
