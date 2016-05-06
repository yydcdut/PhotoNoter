package com.yydcdut.note.adapter.list.vh;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/5/6.
 */
public class EditCategoryVH extends AbsVH {
    @Bind(R.id.img_item_edit_category)
    public ImageView imgLogo;
    @Bind(R.id.txt_item_edit_category)
    public TextView txtName;

    public EditCategoryVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
