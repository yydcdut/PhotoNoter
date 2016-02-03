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
 * Created by yuyidong on 15-3-23.
 */
public class CategoryAdapter extends BaseListAdapter<Category> {
    RandomColor mColor;

    public CategoryAdapter(Context mContext, List<Category> group) {
        super(mContext, group);
        mColor = RandomColor.MATERIAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            int layout = R.layout.navigation_list_item;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
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
        holder.txtName.setText(getGroup().get(position).getLabel());
        holder.txtPicturesNum.setText(getGroup().get(position).getPhotosNumber() + "");
        if (getGroup().get(position).isCheck()) {
            convertView.setBackgroundResource(R.drawable.selector_check_item_navigation);
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, getContext().getResources().getColor(R.color.red_colorPrimary)));
            holder.txtName.setTextColor(getContext().getResources().getColor(R.color.red_colorPrimary));
            holder.txtPicturesNum.setTextColor(getContext().getResources().getColor(R.color.red_colorPrimary));
        } else {
            convertView.setBackgroundResource(R.drawable.selector_no_check_item_navigation);
            holder.imgLogo.setImageDrawable(TextDrawable.builder().buildRound(firstWord, mColor.getColor(firstWord)));
            holder.txtName.setTextColor(getContext().getResources().getColor(R.color.txt_black));
            holder.txtPicturesNum.setTextColor(getContext().getResources().getColor(R.color.txt_gray));
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.icon)
        ImageView imgLogo;
        @Bind(R.id.title)
        TextView txtName;
        @Bind(R.id.counter)
        TextView txtPicturesNum;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
