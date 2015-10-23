package com.yydcdut.note.adapter.vh;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/10/23.
 */
public class PhotoCardViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    public ImageView imageView;
    public TextView textView;

    public PhotoCardViewHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView;
        imageView = (ImageView) itemView.findViewById(R.id.img_item_user_card);
        textView = (TextView) itemView.findViewById(R.id.txt_item_user_card);
    }
}
