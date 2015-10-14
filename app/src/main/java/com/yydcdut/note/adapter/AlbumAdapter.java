package com.yydcdut.note.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.vh.PhotoViewHolder;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/14.
 */
public class AlbumAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private Context mContext;
    private List<PhotoNote> mPhotoNoteList;

    private PhotoViewHolder.OnItemClickListener mOnItemClickListener;
    private PhotoViewHolder.OnItemLongClickListener mOnItemLongClickListener;

    public AlbumAdapter(Context context, List<PhotoNote> photoNoteList,
                        PhotoViewHolder.OnItemClickListener onItemClickListener,
                        PhotoViewHolder.OnItemLongClickListener onItemLongClickListener) {
        mContext = context;
        mPhotoNoteList = photoNoteList;
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        return new PhotoViewHolder(view, mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoNote photoNote = mPhotoNoteList.get(position);
        if (photoNote.isSelected()) {
            holder.checkLayout.setVisibility(View.VISIBLE);
        } else {
            holder.checkLayout.setVisibility(View.INVISIBLE);
        }
        holder.imageView.setTag(photoNote.getSmallPhotoPathWithFile());
        ImageLoaderManager.displayImage(photoNote.getSmallPhotoPathWithFile(), holder.imageView);
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
        notifyDataSetChanged();
    }

    /**
     * 删除选中部分
     */
    public void deleteSelectedPhotos() {
        //注意java.util.ConcurrentModificationException at java.util.ArrayList$ArrayListIterator.next(ArrayList.java:573)
        List<PhotoNote> positions = new ArrayList<PhotoNote>();
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            PhotoNote photoNote = mPhotoNoteList.get(i);
            if (photoNote.isSelected()) {
                positions.add(photoNote);
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            PhotoNoteDBModel.getInstance().delete(positions.get(i));
            mPhotoNoteList.remove(positions.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 全选
     */
    public void selectAllPhotos() {
        for (PhotoNote photoNote : mPhotoNoteList) {
            photoNote.setSelected(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消选择所有照片
     */
    public void cancelSelectPhotos() {
        for (PhotoNote photoNote : mPhotoNoteList) {
            photoNote.setSelected(false);
        }
        notifyDataSetChanged();
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
     * 改变PhotoNote的分类
     *
     * @param newCategoryLabel
     */
    public void changeCategory(String newCategoryLabel) {
        List<PhotoNote> positions = new ArrayList<PhotoNote>();
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            PhotoNote photoNote = mPhotoNoteList.get(i);
            if (photoNote.isSelected()) {
                photoNote.setSelected(false);
                photoNote.setCategoryLabel(newCategoryLabel);
                positions.add(photoNote);
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            mPhotoNoteList.remove(positions.get(i));
        }
        PhotoNoteDBModel.getInstance().updateAll(positions);
        notifyDataSetChanged();
    }

    public void updateData(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
        notifyDataSetChanged();
    }
}
