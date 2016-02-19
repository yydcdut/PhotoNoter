package com.yydcdut.note.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.vh.PhotoViewHolder;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.util.List;

/**
 * Created by yuyidong on 15/10/14.
 */
public class AlbumAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private Context mContext;
    private int mSize;
    private List<PhotoNote> mPhotoNoteList;

    private PhotoViewHolder.OnItemClickListener mOnItemClickListener;
    private PhotoViewHolder.OnItemLongClickListener mOnItemLongClickListener;

    public AlbumAdapter(Context context, List<PhotoNote> photoNoteList, int size,
                        PhotoViewHolder.OnItemClickListener onItemClickListener,
                        PhotoViewHolder.OnItemLongClickListener onItemLongClickListener) {
        mContext = context;
        mSize = size;
        mPhotoNoteList = photoNoteList;
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        return new PhotoViewHolder(view, mSize, mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoNote photoNote = mPhotoNoteList.get(position);
        if (photoNote.isSelected()) {
            holder.checkLayout.setVisibility(View.VISIBLE);
        } else {
            holder.checkLayout.setVisibility(View.INVISIBLE);
        }
        ImageLoaderManager.displayImage(photoNote.getSmallPhotoPathWithFile(), holder.imageView);
        int color = photoNote.getPaletteColor();
        AppCompat.setBackgroundDrawable(holder.checkLayout,
                new ColorDrawable(Color.argb(0x70, Color.red(color), Color.green(color), Color.blue(color))));
    }

    @Override
    public int getItemCount() {
        return mPhotoNoteList.size();
    }

    /**
     * 点item的菜单时候的删除
     *
     * @param selected
     * @param position
     */
    public void setSelectedPosition(boolean selected, int position) {
        mPhotoNoteList.get(position).setSelected(selected);
        notifyItemChanged(position);
    }

    /**
     * 全选
     */
    public void selectAllPhotos() {
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            mPhotoNoteList.get(i).setSelected(true);
            notifyItemChanged(i);
        }
    }

    /**
     * 取消选择所有照片
     */
    public void cancelSelectPhotos() {
        for (PhotoNote photoNote : mPhotoNoteList) {
            photoNote.setSelected(false);
            int index = mPhotoNoteList.indexOf(photoNote);
            notifyItemChanged(index);
        }
    }

    /**
     * 照片是否被选择了
     *
     * @param position
     * @return
     */
    public boolean isPhotoSelected(int position) {
        return mPhotoNoteList.get(position).isSelected();
    }

    /**
     * 更新数据
     *
     * @param photoNotes
     */
    public void updateData(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
        notifyDataSetChanged();
    }

    public void updateDataNoChange(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
    }
}
