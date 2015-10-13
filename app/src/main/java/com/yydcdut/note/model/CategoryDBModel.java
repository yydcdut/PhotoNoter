package com.yydcdut.note.model;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.utils.compare.ComparatorFactory;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by yuyidong on 15/7/17.
 */
public class CategoryDBModel implements IModel {

    private List<Category> mCache;

    private static CategoryDBModel sInstance = new CategoryDBModel();

    private CategoryDBModel() {
        findAll();
    }

    public static CategoryDBModel getInstance() {
        return sInstance;
    }


    public List<Category> findAll() {
        if (mCache == null) {
            mCache = DataSupport.order("sort asc").find(Category.class);
        }
        return mCache;
    }

    public List<Category> refresh() {
        return mCache = DataSupport.order("sort asc").find(Category.class);
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
                category.save();
            } else {
                if (category.isCheck()) {
                    category.setCheck(false);
                    category.save();
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
        boolean bool = category.save();
        if (bool) {
            refresh();
        }
        return bool;
    }

    /**
     * 更新分类，并且刷新
     *
     * @param categoryList
     * @return
     */
    public boolean updateCategoryList(List<Category> categoryList) {
        boolean bool = true;
        for (Category category : categoryList) {
            bool &= category.save();
        }
        if (bool) {
            refresh();
        }
        return bool;
    }

    public boolean update(Category category) {
        boolean bool = true;
        if (category.isSaved()) {
            bool &= checkLabelExist(category);
            if (bool) {
                bool &= category.save();
            }
        }
        if (bool) {
            refresh();
        }
        return bool;
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
            Category category = findByCategoryLabel(originalLabel);
            category.setLabel(newLabel);
            bool &= category.save();
            if (bool) {
                List<PhotoNote> photoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(originalLabel, ComparatorFactory.FACTORY_NOT_SORT);
                for (PhotoNote photoNote : photoNoteList) {
                    photoNote.setCategoryLabel(newLabel);
                }
                PhotoNoteDBModel.getInstance().updateAll(photoNoteList);
            }
        }
        if (bool) {
            refresh();
        }
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
        List<Category> list = DataSupport.where("label = ?", categoryLabel).find(Category.class);
        if (list == null && list.size() == 0) {
            return null;
        } else if (list != null && list.size() == 1) {
            refresh();
            return list.get(0);
        } else if (list != null && list.size() > 1) {
            throw new IllegalArgumentException("BUG!!! label是独一无二的啊！！！");
        }
        return null;
    }

    /**
     * 更新顺序
     *
     * @param categoryList
     * @return
     */
    public boolean updateOrder(List<Category> categoryList) {
        boolean bool = true;
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            category.setSort(i);
            bool &= update(category);
        }
        refresh();
        return bool;
    }

    public void delete(Category category) {
        final String label = category.getLabel();
        mCache.remove(category);
        category.delete();
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                //todo 这里会不会有内存泄露问题，感觉应该没有
                PhotoNoteDBModel.getInstance().deleteByCategory(label);
            }
        });
    }

    private boolean checkLabelExist(Category category) {
        for (Category item : mCache) {
            if (item.getLabel().equals(category.getLabel())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLabelExist(String newLabel) {
        for (Category item : mCache) {
            if (item.getLabel().equals(newLabel)) {
                return true;
            }
        }
        return false;
    }


}
