package com.yydcdut.note.mvp.v.note;

import com.baidu.mapapi.map.BaiduMap;
import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 16/1/11.
 */
public interface IMapView extends IView {

    BaiduMap getBaiduMap();

    void setToolbarTitle(String title);
}
