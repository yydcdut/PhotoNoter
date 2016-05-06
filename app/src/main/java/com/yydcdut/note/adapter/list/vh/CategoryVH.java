package com.yydcdut.note.adapter.list.vh;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/5/6.
 */
public class CategoryVH extends AbsVH {
    @Bind(R.id.icon)
    public ImageView imgLogo;
    @Bind(R.id.title)
    public TextView txtName;
    @Bind(R.id.counter)
    public TextView txtPicturesNum;

    public CategoryVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
