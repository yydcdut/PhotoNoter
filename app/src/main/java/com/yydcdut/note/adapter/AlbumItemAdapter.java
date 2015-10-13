package com.yydcdut.note.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.view.GridItemImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/7/8.
 */
public class AlbumItemAdapter extends BaseListAdapter<PhotoNote> {

    public AlbumItemAdapter(Context context, List<PhotoNote> group) {
        super(context, group);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_album, null);
            vh.imageView = (GridItemImageView) convertView.findViewById(R.id.img_item_album);
            vh.checkLayout = convertView.findViewById(R.id.layout_item_album_check);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        PhotoNote photoNote = getGroup().get(position);
        if (getItem(position).isSelected()) {
            vh.checkLayout.setVisibility(View.VISIBLE);
        } else {
            vh.checkLayout.setVisibility(View.INVISIBLE);
        }
        vh.imageView.setTag(photoNote.getSmallPhotoPathWithFile());
        ImageLoaderManager.displayImage(photoNote.getSmallPhotoPathWithFile(), vh.imageView);
        return convertView;
    }

    class ViewHolder {
        GridItemImageView imageView;
        View checkLayout;
    }

    /**
     * 点item的菜单时候的删除
     *
     * @param selected
     * @param position
     */
    public void setSelectedPosition(boolean selected, int position) {
        getItem(position).setSelected(selected);
        notifyDataSetChanged();
    }

    /**
     * 删除选中部分
     */
    public void deleteSelectedPhotos() {
        //注意java.util.ConcurrentModificationException at java.util.ArrayList$ArrayListIterator.next(ArrayList.java:573)
        List<PhotoNote> group = getGroup();
        List<PhotoNote> positions = new ArrayList<PhotoNote>();
        for (int i = 0; i < group.size(); i++) {
            PhotoNote photoNote = group.get(i);
            if (photoNote.isSelected()) {
                positions.add(photoNote);
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            PhotoNoteDBModel.getInstance().delete(positions.get(i));
            getGroup().remove(positions.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 全选
     */
    public void selectAllPhotos() {
        List<PhotoNote> group = getGroup();
        for (PhotoNote photoNote : group) {
            photoNote.setSelected(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消选择所有照片
     */
    public void cancelSelectPhotos() {
        List<PhotoNote> group = getGroup();
        for (PhotoNote photoNote : group) {
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
        return getItem(position).isSelected();
    }

    /**
     * 改变PhotoNote的分类
     *
     * @param newCategoryLabel
     */
    public void changeCategory(String newCategoryLabel) {
        List<PhotoNote> group = getGroup();
        List<PhotoNote> positions = new ArrayList<PhotoNote>();
        for (int i = 0; i < group.size(); i++) {
            PhotoNote photoNote = group.get(i);
            if (photoNote.isSelected()) {
                photoNote.setSelected(false);
                photoNote.setCategoryLabel(newCategoryLabel);
                positions.add(photoNote);
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            getGroup().remove(positions.get(i));
        }
        PhotoNoteDBModel.getInstance().updateAll(positions);
        notifyDataSetChanged();
    }
}
