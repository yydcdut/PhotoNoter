package com.yydcdut.gallery.adapter.vh;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yydcdut.gallery.R;
import com.yydcdut.gallery.model.GalleryApp;

import java.util.List;

/**
 * Created by yuyidong on 16/3/20.
 */
public class NavFooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnNavFooterItemClickListener mOnNavFooterItemClickListener;

    public NavFooterViewHolder(@NonNull View itemView, @Nullable List<GalleryApp> galleryAppList,
                               @Nullable OnNavFooterItemClickListener onNavFooterItemClickListener) {
        super(itemView);
        mOnNavFooterItemClickListener = onNavFooterItemClickListener;
        LinearLayout linearLayout = (LinearLayout) itemView;
        for (GalleryApp galleryApp : galleryAppList) {
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_nav_footer, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_nav_footer);
            imageView.setImageDrawable(galleryApp.getLogoDrawable());
            TextView textView = (TextView) view.findViewById(R.id.txt_nav_footer);
            textView.setText(galleryApp.getAppName());
            linearLayout.addView(view);
            view.setTag(galleryApp);
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnNavFooterItemClickListener != null) {
            mOnNavFooterItemClickListener.onNavFooterItemClick((GalleryApp) v.getTag());
        }
    }

    public interface OnNavFooterItemClickListener {
        void onNavFooterItemClick(@NonNull GalleryApp galleryApp);
    }
}
