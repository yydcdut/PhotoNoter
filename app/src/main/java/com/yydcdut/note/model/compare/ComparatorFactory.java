package com.yydcdut.note.model.compare;

import com.yydcdut.note.bean.PhotoNote;

import java.util.Comparator;

/**
 * Created by yuyidong on 15/7/23.
 */
public class ComparatorFactory {

    public static final int FACTORY_NOT_SORT = -1;
    public static final int FACTORY_CREATE_FAR = 1;
    public static final int FACTORY_CREATE_CLOSE = 2;
    public static final int FACTORY_EDITED_FAR = 3;
    public static final int FACTORY_EDITED_CLOSE = 4;

    public static Comparator<PhotoNote> get(int factory) {
        switch (factory) {
            case FACTORY_CREATE_FAR:
                return new CreateFarComparable();
            case FACTORY_CREATE_CLOSE:
                return new CreateCloseComparable();
            case FACTORY_EDITED_FAR:
                return new EditedFarComparable();
            case FACTORY_EDITED_CLOSE:
                return new EditedCloseComparable();
        }
        return null;
    }


}
