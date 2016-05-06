package com.yydcdut.note.adapter.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.list.vh.EditCategoryVH;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.RandomColor;
import com.yydcdut.note.widget.TextDrawable;

import java.util.List;

/**
 * Created by yuyidong on 16/5/6.
 */
public class EditCategoryAdapter extends VHAdapter<Category, EditCategoryVH> {
    private int mCurrentPosition = -1;
    private RandomColor mColor;

    public EditCategoryAdapter(@NonNull Context context, @NonNull List<Category> data) {
        super(context, data);
        mColor = RandomColor.MATERIAL;
    }

    @Override
    public EditCategoryVH onCreateVH(@NonNull LayoutInflater layoutInflater, int viewType, int position) {
        return new EditCategoryVH(layoutInflater.inflate(R.layout.item_setting_edit_category, null));
    }

    @Override
    public void onBindVH(@NonNull EditCategoryVH holder, int position) {
        String label = getItem(position).getLabel();
        String firstWord = null;
        if (label.length() > 0) {
            firstWord = label.substring(0, 1);
        } else {
            firstWord = "N";
        }
        if (mCurrentPosition == position) {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, AppCompat.getColor(R.color.red_colorPrimary, getContext())));
            holder.txtName.setTextColor(AppCompat.getColor(R.color.red_colorPrimary, getContext()));
        } else {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, mColor.getColor(firstWord)));
            holder.txtName.setTextColor(AppCompat.getColor(R.color.txt_gray, getContext()));
        }
        holder.txtName.setText(getItem(position).getLabel());
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }
}
