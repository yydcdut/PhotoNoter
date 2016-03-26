package com.yydcdut.noteplugin.adapter.vh;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yydcdut.noteplugin.R;
import com.yydcdut.noteplugin.bean.GalleryApp;
import com.yydcdut.noteplugin.utils.AppCompat;

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
        if (galleryAppList != null && galleryAppList.size() > 0) {
            linearLayout.addView(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_nav_separator, null));
            TextView subHeaderTextView = (TextView) LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_nav_subheader, null);
            subHeaderTextView.setText("Third App");
            linearLayout.addView(subHeaderTextView);
            for (GalleryApp galleryApp : galleryAppList) {
                ViewGroup viewGroup = null;
                if (AppCompat.AFTER_ICE_CREAM) {
                    viewGroup = (ViewGroup) LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_nav_footer_v14, null);
                } else {
                    viewGroup = (ViewGroup) LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_nav_footer, null);
                }
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View view = viewGroup.getChildAt(i);
                    view.setTag(galleryApp);
                }
                ImageView imageView = (ImageView) viewGroup.findViewById(R.id.img_nav_footer);
                imageView.setImageDrawable(galleryApp.getLogoDrawable());
                TextView textView = (TextView) viewGroup.findViewById(R.id.txt_nav_footer);
                textView.setText(galleryApp.getAppName());
                linearLayout.addView(viewGroup);
                viewGroup.setOnClickListener(this);
            }
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
