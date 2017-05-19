package com.yydcdut.note.adapter.list.vh;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/5/6.
 */
public class CategoryVH extends AbsVH {
    @BindView(R.id.icon)
    public ImageView imgLogo;
    @BindView(R.id.title)
    public TextView txtName;
    @BindView(R.id.counter)
    public TextView txtPicturesNum;

    public CategoryVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
