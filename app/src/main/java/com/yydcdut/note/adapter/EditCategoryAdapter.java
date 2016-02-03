package com.yydcdut.note.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.utils.RandomColor;
import com.yydcdut.note.widget.TextDrawable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/11/15.
 */
public class EditCategoryAdapter extends BaseListAdapter<Category> {
    private int mCurrentPosition = -1;
    private RandomColor mColor;

    public EditCategoryAdapter(Context context, List group) {
        super(context, group);
        mColor = RandomColor.MATERIAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_edit_category, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String label = getGroup().get(position).getLabel();
        String firstWord = null;
        if (label.length() > 0) {
            firstWord = label.substring(0, 1);
        } else {
            firstWord = "N";
        }
        if (mCurrentPosition == position) {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, getContext().getResources().getColor(R.color.red_colorPrimary)));
            holder.txtName.setTextColor(getContext().getResources().getColor(R.color.red_colorPrimary));
        } else {
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, mColor.getColor(firstWord)));
            holder.txtName.setTextColor(getContext().getResources().getColor(R.color.txt_gray));
        }
        holder.txtName.setText(getGroup().get(position).getLabel());
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.img_item_edit_category)
        ImageView imgLogo;
        @Bind(R.id.txt_item_edit_categoty)
        TextView txtName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }
}
