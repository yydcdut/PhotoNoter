package com.yydcdut.note.presenters.setting;

import com.yydcdut.note.presenters.IPresenter;

/**
 * Created by yuyidong on 15/11/15.
 */
public interface IEditCategoryPresenter extends IPresenter {
    /**
     * 改名字并显示出来
     * //但是在数据库中还没有改，最后退出的时候一起改
     *
     * @param index
     * @param newLabel
     */
    void renameCategory(int index, String newLabel);

    /**
     * 删除分类
     * //在逻辑中专门添加到一个List中，最后一起删除
     *
     * @param index
     */
    void deleteCategory(int index);

    /**
     * 最后的工作，在数据库中进行重命名和删除工作
     */
    void doJob();
}
