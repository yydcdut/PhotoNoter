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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        notifyItemChanged(position);
    }

    /**
     * 删除选中部分
     */
    public void deleteSelectedPhotos() {
        //注意java.util.ConcurrentModificationException at java.util.ArrayList$ArrayListIterator.next(ArrayList.java:573)
        TreeMap<Integer, PhotoNote> map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            PhotoNote photoNote = mPhotoNoteList.get(i);
            if (photoNote.isSelected()) {
                map.put(i, photoNote);
            }
        }
        int times = 0;
        for (Map.Entry<Integer, PhotoNote> entry : map.entrySet()) {
            PhotoNoteDBModel.getInstance().delete(entry.getValue());
            mPhotoNoteList.remove(entry.getValue());
            notifyItemRemoved(entry.getKey() - times);
            times++;
        }
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
     * 改变PhotoNote的分类
     *
     * @param newCategoryLabel
     */
    public void changeCategory(String newCategoryLabel) {
        TreeMap<Integer, PhotoNote> map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            PhotoNote photoNote = mPhotoNoteList.get(i);
            if (photoNote.isSelected()) {
                photoNote.setSelected(false);
                photoNote.setCategoryLabel(newCategoryLabel);
                map.put(i, photoNote);
            }
        }
        int times = 0;
        int total = map.size();
        for (Map.Entry<Integer, PhotoNote> entry : map.entrySet()) {
            mPhotoNoteList.remove(entry.getValue());
            notifyItemRemoved(entry.getKey() + times);
            if (times + 1 != total) {
                PhotoNoteDBModel.getInstance().update(entry.getValue(), false);
            } else {
                PhotoNoteDBModel.getInstance().update(entry.getValue(), true);
            }
            times++;
        }
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

    public void updateDataWithoutChanged(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
    }
}
