package com.yydcdut.note.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.SandPhoto;
import com.yydcdut.note.model.observer.IObserver;
import com.yydcdut.note.model.sqlite.SandSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/8/10.
 * 不要有cache，因为这个db随时都在变
 * 记住关DB，关Cursor
 */
public class SandBoxDBModel implements IModel {

    private static final String NAME = "SandBox.db";
    private static final int VERSION = 2;

    private SandSQLite mSandSQLite;

    private static SandBoxDBModel sInstance = new SandBoxDBModel();

    private SandBoxDBModel() {
        mSandSQLite = new SandSQLite(NoteApplication.getContext(), NAME, null, VERSION);
    }

    public static SandBoxDBModel getInstance() {
        return sInstance;
    }

    /**
     * 查询
     *
     * @return
     */
    public List<SandPhoto> findAll() {
        List<SandPhoto> sandPhotoList = new ArrayList<>();
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            byte[] data = cursor.getBlob(cursor.getColumnIndex("data"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            String cameraId = cursor.getString(cursor.getColumnIndex("cameraId"));
            String category = cursor.getString(cursor.getColumnIndex("category"));
            String isMirrorString = cursor.getString(cursor.getColumnIndex("mirror"));
            boolean isMirror = isMirrorString.equals("0") ? false : true;
            int ratio = cursor.getInt(cursor.getColumnIndex("ratio"));
            SandPhoto sandPhoto = new SandPhoto(id, data, time, cameraId, category, isMirror, ratio);
            sandPhotoList.add(sandPhoto);
        }
        cursor.close();
        db.close();
        return sandPhotoList;
    }

    /**
     * 保存
     *
     * @param sandPhoto
     * @return
     */
    public long save(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", sandPhoto.getData());
        contentValues.put("time", sandPhoto.getTime());
        contentValues.put("cameraId", sandPhoto.getCameraId());
        contentValues.put("category", sandPhoto.getCategory());
        contentValues.put("mirror", sandPhoto.isMirror());
        contentValues.put("ratio", sandPhoto.getRatio());
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
    public int delete(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        int rows = db.delete(SandSQLite.TABLE, "_id = ?", new String[]{sandPhoto.getId() + ""});
        db.close();
        return rows;
    }

    public int getAllNumber() {
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


    @Override
    public boolean addObserver(IObserver iObserver) {
        return false;
    }
}
