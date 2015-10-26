package com.yydcdut.note.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.vh.PhotoCardViewHolder;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.util.List;

/**
 * Created by yuyidong on 15/10/23.
 */
public class FrequentImageAdapter extends RecyclerView.Adapter<PhotoCardViewHolder> {
    private Context mContext;
    private List<PhotoNote> mPhotoNoteList;

    public FrequentImageAdapter(Context context, List<PhotoNote> photoNoteList) {
        mContext = context;
        mPhotoNoteList = photoNoteList;
    }

    @Override
    public PhotoCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_center_item_card, parent, false);
        return new PhotoCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoCardViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(mPhotoNoteList.get(position).getPaletteColor());
        ImageLoaderManager.displayImage(mPhotoNoteList.get(position).getSmallPhotoPathWithFile(), holder.imageView);
        holder.textView.setText("See you soon~");
    }

    @Override
    public int getItemCount() {
        return mPhotoNoteList.size();
    }
}
