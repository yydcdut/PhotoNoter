package com.yydcdut.note.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.observer.CategoryChangedObserver;
import com.yydcdut.note.model.observer.IObserver;
import com.yydcdut.note.model.sqlite.NotesSQLite;
import com.yydcdut.note.utils.compare.ComparatorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/7/17.
 * 只允许在AlbumFragment、HomeActivity和EditCategoryActivity中调用
 */
public class CategoryDBModel extends AbsNotesDBModel implements IModel {
    private List<CategoryChangedObserver> mCategoryChangedObservers = new ArrayList<>();

    private List<Category> mCache;

    private static CategoryDBModel sInstance = new CategoryDBModel();

    private CategoryDBModel() {
        findAll();
    }

    public static CategoryDBModel getInstance() {
        return sInstance;
    }

    @Override
    public boolean addObserver(IObserver iObserver) {
        if (iObserver instanceof CategoryChangedObserver) {
            mCategoryChangedObservers.add((CategoryChangedObserver) iObserver);
            return true;
        }
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
                    updateData2DB(updateCategory);
                }
            }
        }
        return true;
    }

    /**
     * 保持分类，并且刷新
     *
     * @param category
     * @return
     */
    public boolean saveCategory(Category category) {
        if (checkLabelExist(category)) {
            return false;
        }
        if (category.isCheck()) {
            resetCheck();
        }
        long id = saveData2DB(category);
        if (id >= 0) {
            refresh();
        }
        doObserver(IObserver.OBSERVER_CATEGORY_CREATE);
        return id >= 0;
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

    public void updateChangeCategory(String oldCategoryLabel, String targetCategoryLabel) {
        Category oldCategory = findByCategoryLabel(oldCategoryLabel);
        Category targetCategory = findByCategoryLabel(targetCategoryLabel);
        //更新移动过去的category数目
        int targetNumber = PhotoNoteDBModel.getInstance().findByCategoryLabel(targetCategory.getLabel(), ComparatorFactory.FACTORY_NOT_SORT).size();
        targetCategory.setPhotosNumber(targetNumber);
        CategoryDBModel.getInstance().update(targetCategory, false);
        int oldNumber = PhotoNoteDBModel.getInstance().findByCategoryLabel(oldCategory.getLabel(), ComparatorFactory.FACTORY_NOT_SORT).size();
        oldCategory.setPhotosNumber(oldNumber);
        CategoryDBModel.getInstance().update(oldCategory);
        doObserver(IObserver.OBSERVER_CATEGORY_MOVE);
    }

    /**
     * todo 时间会比较长
     *
     * @param originalLabel
     * @param newLabel
     * @return
     */
    public boolean updateLabel(String originalLabel, String newLabel) {
        boolean bool = true;
        bool &= checkLabelExist(newLabel);
        if (bool) {
            //数据可能在EditCategoryActivity中改过了，
            Category category = findByCategoryLabel(originalLabel);
            category.setLabel(newLabel);
            bool &= updateData2DB(category);
            if (bool) {
                //处理PhotoNote
                List<PhotoNote> photoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(originalLabel, ComparatorFactory.FACTORY_NOT_SORT);
                int total = photoNoteList.size();
                int number = 0;
                for (PhotoNote photoNote : photoNoteList) {
                    photoNote.setCategoryLabel(newLabel);
                    if ((number++) + 1 == total) {
                        PhotoNoteDBModel.getInstance().update(photoNote, true);
                    } else {
                        PhotoNoteDBModel.getInstance().update(photoNote, false);
                    }
                }
            }
        }
        if (bool) {
            refresh();
        }
        doObserver(IObserver.OBSERVER_CATEGORY_RENAME);
        return bool;
    }

    /**
     * 通过label查抄
     *
     * @param categoryLabel
     * @return
     */
    public Category findByCategoryLabel(String categoryLabel) {
        for (Category category : mCache) {
            if (category.getLabel().equals(categoryLabel)) {
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
        doObserver(IObserver.OBSERVER_CATEGORY_SORT);
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
        doObserver(IObserver.OBSERVER_CATEGORY_DELETE);
        deletePhotoNotes(label);
    }

    /**
     * 删除Category下面的图片
     *
     * @param label
     */
    private void deletePhotoNotes(final String label) {
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                PhotoNoteDBModel.getInstance().deleteByCategoryWithoutObserver(label);
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
        doObserver(IObserver.OBSERVER_CATEGORY_UPDATE);
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

    private long saveData2DB(Category category) {
        SQLiteDatabase db = mNotesSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", category.getLabel());
        contentValues.put("photosNumber", category.getPhotosNumber());
        contentValues.put("isCheck", category.isCheck() ? 1 : 0);
        contentValues.put("sort", category.getSort());
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

    private void doObserver(int CRUD) {
        for (CategoryChangedObserver observer : mCategoryChangedObservers) {
            observer.onUpdate(CRUD);
        }
    }

}
