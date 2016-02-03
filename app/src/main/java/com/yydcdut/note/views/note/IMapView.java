package com.yydcdut.note.views.note;

import com.baidu.mapapi.map.BaiduMap;
import com.yydcdut.note.views.IView;

/**
 * Created by yuyidong on 16/1/11.
 */
public interface IMapView extends IView {

    BaiduMap getBaiduMap();

    void setToolbarTitle(String title);
}
