package com.yydcdut.note.presenters.note.impl;

import android.media.ExifInterface;
import android.support.annotation.NonNull;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.presenters.note.IMapPresenter;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.note.IMapView;

import java.io.IOException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 16/1/11.
 */
public class MapPresenterImpl implements IMapPresenter, OnGetGeoCoderResultListener {
    private IMapView mIMapView;

    private RxPhotoNote mRxPhotoNote;

    /* Data */
    private int mCategoryId;
    private int mComparator;
    private int mPosition;

    /* Baidu Map */
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    @Inject
    public MapPresenterImpl(RxPhotoNote rxPhotoNote) {
        mRxPhotoNote = rxPhotoNote;
    }

    @Override
    public void bindData(int categoryID, int position, int comparator) {
        mCategoryId = categoryID;
        mPosition = position;
        mComparator = comparator;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mIMapView = (IMapView) iView;
        initBaiduMap();
        showLocation();
    }

    private void initBaiduMap() {
        mBaiduMap = mIMapView.getBaiduMap();
        //获取地图对象控制器
        mBaiduMap.setBuildingsEnabled(true);//设置显示楼体
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19f));//设置地图状态
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setOverlookingGesturesEnabled(true);
        mUiSettings.setCompassEnabled(true);
        MapStatus ms = new MapStatus.Builder().overlook(30).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u, 1000);
        // 初始化搜索模块
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    private void showLocation() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNoteList -> {
                    PhotoNote photoNote = photoNoteList.get(mPosition);
                    try {
                        gps(photoNote.getBigPhotoPathWithoutFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void detachView() {
        mSearch.destroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(reverseGeoCodeResult.getLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult
                .getLocation()));
        mIMapView.setToolbarTitle(reverseGeoCodeResult.getAddress());
    }

    private void gps(String path) throws IOException {
        ExifInterface exifInterface = new ExifInterface(path);
        String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if (longitude != null && !"null".equals(longitude.toLowerCase())) {
            String[] longitudeSs = longitude.split(",");
            double longitudesD = 0;
            longitudesD += Double.parseDouble(longitudeSs[0].split("/")[0]);
            longitudesD += (((int) (Double.parseDouble(longitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(longitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;

            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String[] latitudeSs = latitude.split(",");
            double latitudesD = 0;
            latitudesD += Double.parseDouble(latitudeSs[0].split("/")[0]);
            latitudesD += (((int) (Double.parseDouble(latitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(latitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;
            doGps(latitudesD, longitudesD);
        }
    }

    private void doGps(double lat, double lon) {
        LatLng ptCenter = new LatLng(lat, lon);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
    }
}
