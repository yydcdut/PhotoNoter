package com.yydcdut.note.views.home;

import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.views.IView;

import java.util.List;

/**
 * Created by yuyidong on 15/11/20.
 */
public interface IAlbumView extends IView {

    void setAdapter(List<PhotoNote> photoNoteList);

    void startSandBoxService();

    void jump2DetailActivity(int categoryId, int position, int comparator);

    void notifyDataSetChanged();

    void updateData(List<PhotoNote> photoNoteList);

    void updateDataNoChange(List<PhotoNote> photoNoteList);

    void showMovePhotos2AnotherCategoryDialog(String[] categoryIdArray, String[] categoryLabelArray);

    void notifyItemRemoved(int position);

    void notifyItemInserted(int position);

    void showToast(String message);

    void showProgressBar();

    void hideProgressBar();

    void changeActivityListMenuCategoryChecked(Category category);

    void jump2CameraActivity(int categoryId);

    void jump2CameraSystemActivity();

    void setToolBarTitle(String title);
}
