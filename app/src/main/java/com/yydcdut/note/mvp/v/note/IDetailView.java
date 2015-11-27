package com.yydcdut.note.mvp.v.note;

import com.baidu.mapapi.map.BaiduMap;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.mvp.IView;

import java.util.List;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IDetailView extends IView {

    void setFontSystem(boolean usSystem);

    void setViewPagerAdapter(List<PhotoNote> list, int position, int comparator);

    void showCurrentPosition(int position);

    BaiduMap getBaiduMap();

    int getCurrentPosition();

    void showNote(String title, String content, String createdTime, String editedTime);

    void showExif(String exif);

    void jump2EditTextActivity(int categoryId, int position, int comparator);

}
