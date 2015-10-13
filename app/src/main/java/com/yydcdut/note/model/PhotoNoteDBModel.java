package com.yydcdut.note.model;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.compare.ComparatorFactory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/7/17.
 */
public class PhotoNoteDBModel implements IModel {

    private static PhotoNoteDBModel sInstance = new PhotoNoteDBModel();

    private Map<String, List<PhotoNote>> mCache = new HashMap<>();

    private PhotoNoteDBModel() {
    }

    public static PhotoNoteDBModel getInstance() {
        return sInstance;
    }

    public List<PhotoNote> findByCategoryLabel(String categoryLabel, int comparatorFactory) {

        List<PhotoNote> list = mCache.get(categoryLabel);
        if (null == list) {
            list = DataSupport.where("categoryLabel = ?", categoryLabel).find(PhotoNote.class);
            mCache.put(categoryLabel, list);
        }
        if (comparatorFactory != -1) {
            Collections.sort(list, ComparatorFactory.get(comparatorFactory));
        }
        return list;
    }

    public List<PhotoNote> findByCategoryLabelByForce(String categoryLabel, int comparatorFactory) {
        List<PhotoNote> list = DataSupport.where("categoryLabel = ?", categoryLabel).find(PhotoNote.class);
        mCache.remove(categoryLabel);
        mCache.put(categoryLabel, list);
        if (comparatorFactory != -1) {
            Collections.sort(list, ComparatorFactory.get(comparatorFactory));
        }
        return list;
    }

    private boolean refresh(String categoryLabel) {
        mCache.remove(categoryLabel);
        mCache.put(categoryLabel, DataSupport.where("categoryLabel = ?", categoryLabel).find(PhotoNote.class));
        return true;
    }

    public boolean update(PhotoNote photoNote) {
        boolean bool = photoNote.save();
        refresh(photoNote.getCategoryLabel());
        return bool;
    }

    public boolean save(PhotoNote photoNote) {
        boolean bool = photoNote.save();
        bool &= refresh(photoNote.getCategoryLabel());
        return bool;
    }

    public void delete(PhotoNote photoNote) {
        if (photoNote.isSaved()) {
            mCache.get(photoNote.getCategoryLabel()).remove(photoNote);
            //注意 java.util.ConcurrentModificationException
            photoNote.delete();
            FilePathUtils.deleteAllFiles(photoNote.getPhotoName());
        }
    }

    public void deleteByCategory(String categoryLabel) {
        List<PhotoNote> photoNoteList = findByCategoryLabel(categoryLabel, -1);
        List<PhotoNote> wait4Delete = new ArrayList<>(photoNoteList.size());
        for (int i = 0; i < photoNoteList.size(); i++) {
            wait4Delete.add(photoNoteList.get(i));
        }
        for (int i = 0; i < wait4Delete.size(); i++) {
            delete(wait4Delete.get(i));
        }
        mCache.remove(categoryLabel);
    }

    public void updateAll(List<PhotoNote> photoNoteList) {
        if (photoNoteList.size() <= 0) {
            return;
        }
        String category = photoNoteList.get(0).getCategoryLabel();
        for (PhotoNote photoNote : photoNoteList) {
            photoNote.save();
        }
        refresh(category);
    }

}
