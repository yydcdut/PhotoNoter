package com.yydcdut.note.adapter.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.list.vh.CategoryVH;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.RandomColor;
import com.yydcdut.note.widget.TextDrawable;

import java.util.List;

/**
 * Created by yuyidong on 16/5/6.
 */
public class CategoryAdapter extends VHAdapter<Category, CategoryVH> {
    RandomColor mColor;

    public CategoryAdapter(@NonNull Context context, @NonNull List<Category> data) {
        super(context, data);
        mColor = RandomColor.MATERIAL;
    }

    @Override
    public CategoryVH onCreateVH(@NonNull LayoutInflater layoutInflater, int viewType, int position) {
        return new CategoryVH(layoutInflater.inflate(R.layout.navigation_list_item, null));
    }

    @Override
    public void onBindVH(@NonNull CategoryVH holder, int position) {
        String label = getItem(position).getLabel();
        String firstWord = null;
        if (label.length() > 0) {
            firstWord = label.substring(0, 1);
        } else {
            firstWord = "N";
        }
        holder.txtName.setText(getItem(position).getLabel());
        holder.txtPicturesNum.setText(getItem(position).getPhotosNumber() + "");
        if (getItem(position).isCheck()) {
            holder.itemView.setBackgroundResource(R.drawable.selector_check_item_navigation);
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, AppCompat.getColor(R.color.red_colorPrimary, getContext())));
            holder.txtName.setTextColor(AppCompat.getColor(R.color.red_colorPrimary, getContext()));
            holder.txtPicturesNum.setTextColor(AppCompat.getColor(R.color.red_colorPrimary, getContext()));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.selector_no_check_item_navigation);
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, mColor.getColor(firstWord)));
            holder.txtName.setTextColor(AppCompat.getColor(R.color.txt_black, getContext()));
            holder.txtPicturesNum.setTextColor(AppCompat.getColor(R.color.txt_gray, getContext()));
        }
    }
}
