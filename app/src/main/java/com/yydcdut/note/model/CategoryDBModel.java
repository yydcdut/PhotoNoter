package com.yydcdut.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.observer.IObserver;
import com.yydcdut.note.model.sqlite.NotesSQLite;
import com.yydcdut.note.utils.ThreadExecutorPool;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yuyidong on 15/7/17.
 * 只允许在AlbumFragment、HomeActivity和EditCategoryActivity中调用
 */
public class CategoryDBModel extends AbsNotesDBModel implements IModel {

    private List<Category> mCache;

    private ThreadExecutorPool mThreadExecutorPool;

    private PhotoNoteDBModel mPhotoNoteDBModel;

    @Singleton
    @Inject
    public CategoryDBModel(@ContextLife("Application") Context context, PhotoNoteDBModel photoNoteDBModel,
                           ThreadExecutorPool threadExecutorPool) {
        super(context);
        mPhotoNoteDBModel = photoNoteDBModel;
        mThreadExecutorPool = threadExecutorPool;
        findAll();
    }

    @Override
    public boolean addObserver(IObserver iObserver) {
        return false;
    }

    @Override
    public boolean removeObserver(IObserver iObserver) {
        return false;
    }

    public List<Category> findAll() {
        if (mCache == null) {
            mCache = new ArrayList<>();
            SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
            Cursor cursor = db.query(NotesSQLite.TABLE_CATEGORY, null, null, null, null, null, "sort asc");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String label = cursor.getString(cursor.getColumnIndex("label"));
                int photosNumber = cursor.getInt(cursor.getColumnIndex("photosNumber"));
                boolean isCheck = cursor.getInt(cursor.getColumnIndex("isCheck")) == 0 ? false : true;
                int sort = cursor.getInt(cursor.getColumnIndex("sort"));
                Category category = new Category(id, label, photosNumber, sort, isCheck);
                mCache.add(category);
            }
            cursor.close();
            db.close();
        }
        return mCache;
    }

    public List<Category> refresh() {
        if (mCache == null) {
            mCache = new ArrayList<>();
        } else {
            mCache.clear();
        }
        SQLiteDatabase db = mNotesSQLite.getReadableDatabase();
        Cursor cursor = db.query(NotesSQLite.TABLE_CATEGORY, null, null, null, null, null, "sort asc");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String label = cursor.getString(cursor.getColumnIndex("label"));
            int photosNumber = cursor.getInt(cursor.getColumnIndex("photosNumber"));
            boolean isCheck = cursor.getInt(cursor.getColumnIndex("isCheck")) == 0 ? false : true;
            int sort = cursor.getInt(cursor.getColumnIndex("sort"));
            Category category = new Category(id, label, photosNumber, sort, isCheck);
            mCache.add(category);
        }
        cursor.close();
        db.close();
        return mCache;
    }

    /**
     * 针对Category，在DrawerLayout中的菜单的选中
     *
     * @param updateCategory
     * @return
     */
    public boolean setCategoryMenuPosition(Category updateCategory) {
        for (Category category : mCache) {
            if (category.getLabel().equals(updateCategory.getLabel())) {
                category.setCheck(true);
                updateData2DB(updateCategory);
            } else {
                if (category.isCheck()) {
                    category.setCheck(false);
                    updateData2DB(category);
                }
            }
        }
        return true;
    }

    /**
     * 保存分类，并且刷新
     *
     * @param label
     * @param photosNumber
     * @param sort
     * @param isCheck
     * @return
     */
    public long saveCategory(String label, int photosNumber, int sort, boolean isCheck) {
        if (checkLabelExist(label)) {
            return -1;
        }
        if (isCheck) {
            resetCheck();
        }
        long id = saveData2DB(label, photosNumber, sort, isCheck);
        if (id >= 0) {
            refresh();
        }
        return id;
    }

    private void resetCheck() {
        for (Category category : mCache) {
            category.setCheck(false);
            updateData2DB(category);
        }
    }

    /**
     * 更新分类，并且刷新
     *
     * @param categoryList
     * @return
     */
    public boolean updateCategoryListInService(List<Category> categoryList) {
        boolean bool = true;
        for (Category category : categoryList) {
            bool &= updateData2DB(category);
        }
        if (bool) {
            refresh();
        }
        return bool;
    }

    public boolean update(Category category) {
        return update(category, true);
    }

    public void updateChangeCategory(int oldCategoryId, int targetCategoryId, int changeNumber) {
        Category oldCategory = findByCategoryId(oldCategoryId);
        Category targetCategory = findByCategoryId(targetCategoryId);
        //更新移动过去的category数目
        targetCategory.setPhotosNumber(targetCategory.getPhotosNumber() + changeNumber);
        update(targetCategory, false);
        oldCategory.setPhotosNumber(oldCategory.getPhotosNumber() - changeNumber);
        update(oldCategory);
    }

    /**
     * @param categoryId
     * @param newLabel
     * @return
     */
    public boolean updateLabel(int categoryId, String newLabel) {
        boolean bool = true;
        bool &= (!checkLabelExist(newLabel));
        if (bool) {
            //数据可能在EditCategoryActivity中改过了，
            Category category = findByCategoryId(categoryId);
            category.setLabel(newLabel);
            bool &= updateData2DB(category);
        }
        if (bool) {
            refresh();
        }
        return bool;
    }

    /**
     * 通过Id查
     *
     * @param categoryId
     * @return
     */
    public Category findByCategoryId(int categoryId) {
        for (Category category : mCache) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        return null;
    }

    /**
     * 更新顺序
     * (目前只在EditCategoryActivity中调用了)
     *
     * @param categoryList
     * @return
     */
    public boolean updateOrder(List<Category> categoryList) {
        boolean bool = true;
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            category.setSort(i);
            bool &= updateData2DB(category);
        }
        refresh();
        return bool;
    }

    /**
     * 目前只在EditCategoryActivity中调用了
     *
     * @param category
     */
    public void delete(Category category) {
        final String label = category.getLabel();
        mCache.remove(category);
        deleteData2DB(category);
        deletePhotoNotes(category.getId());
    }

    /**
     * 删除Category下面的图片
     *
     * @param id
     */
    private void deletePhotoNotes(final int id) {
        mThreadExecutorPool.getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                mPhotoNoteDBModel.deleteByCategoryWithoutObserver(id);
            }
        });
    }

    private int deleteData2DB(Category category) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        int rows = db.delete(NotesSQLite.TABLE_CATEGORY, "_id = ?", new String[]{category.getId() + ""});
        db.close();
        return rows;
    }

    private boolean update(Category category, boolean refresh) {
        boolean bool = true;
        if (checkLabelExist(category)) {
            bool &= updateData2DB(category);
        }
        if (bool && refresh) {
            refresh();
        }
        return bool;
    }

    private boolean checkLabelExist(Category category) {
        return checkLabelExist(category.getLabel());
    }

    private boolean checkLabelExist(String newLabel) {
        for (Category item : mCache) {
            if (item.getLabel().equals(newLabel)) {
                return true;
            }
        }
        return false;
    }

    private long saveData2DB(String label, int photosNumber, int sort, boolean isCheck) {
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

    private boolean updateData2DB(Category category) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", category.getLabel());
        contentValues.put("photosNumber", category.getPhotosNumber());
        contentValues.put("isCheck", category.isCheck() ? 1 : 0);
        contentValues.put("sort", category.getSort());
        int rows = db.update(NotesSQLite.TABLE_CATEGORY, contentValues, "_id = ?", new String[]{category.getId() + ""});
        db.close();
        return rows >= 0;
    }

}
