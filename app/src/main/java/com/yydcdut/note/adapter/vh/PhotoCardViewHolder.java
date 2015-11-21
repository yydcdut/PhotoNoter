package com.yydcdut.note.adapter.vh;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/10/23.
 */
public class PhotoCardViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    @Bind(R.id.img_item_user_card)
    public ImageView imageView;
    @Bind(R.id.txt_item_user_card)
    public TextView textView;

    public PhotoCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        cardView = (CardView) itemView;
    }
}
