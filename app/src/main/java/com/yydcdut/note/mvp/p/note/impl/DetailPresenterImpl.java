package com.yydcdut.note.mvp.p.note.impl;

import android.content.Context;
import android.media.ExifInterface;
import android.text.TextUtils;

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
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IDetailPresenter;
import com.yydcdut.note.mvp.v.note.IDetailView;
import com.yydcdut.note.utils.FilePathUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yuyidong on 15/11/16.
 */
public class DetailPresenterImpl implements IDetailPresenter, OnGetGeoCoderResultListener {
    private IDetailView mDetailView;
    private Context mContext;

    /* Data */
    private List<PhotoNote> mPhotoNoteList;
    private String mCategoryLabel;
    private int mComparator;
    private int mInitPostion;

    /* Baidu Map */
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    public DetailPresenterImpl(String categoryLabel, int position, int comparator) {
        mContext = NoteApplication.getContext();
        mCategoryLabel = categoryLabel;
        mInitPostion = position;
        mComparator = comparator;
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(categoryLabel, comparator);
    }

    @Override
    public void attachView(IView iView) {
        mDetailView = (IDetailView) iView;
        initBaiduMap();
        mDetailView.setViewPagerAdapter(mCategoryLabel, mDetailView.getCurrentPosition(), mComparator);
        showNote(mInitPostion);
        mDetailView.showCurrentPosition(mInitPostion);
    }

    private String decodeTimeInDetail(long time) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        String[] months = mContext.getResources().getStringArray(R.array.detail_time_month);
        sb.append(months[Integer.parseInt(sdfMonth.format(time)) - 1]);
        SimpleDateFormat sdfDayAndYear = new SimpleDateFormat(" dd. yyyy ");
        sb.append(sdfDayAndYear.format(time));
        sb.append("at");
        SimpleDateFormat sdfHour = new SimpleDateFormat(" hh:mm:ss a");
        sb.append(sdfHour.format(time));
        return sb.toString();
    }

    private void initBaiduMap() {
        mBaiduMap = mDetailView.getBaiduMap();
        //获取地图对象控制器
        mBaiduMap.setBuildingsEnabled(true);//设置显示楼体
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19f));//设置地图状态
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setOverlookingGesturesEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mBaiduMap.showMapPoi(true);
        MapStatus ms = new MapStatus.Builder().overlook(-30).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u, 1000);
        // 初始化搜索模块
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    @Override
    public void showExif() {
        int position = mDetailView.getCurrentPosition();
        PhotoNote photoNote = mPhotoNoteList.get(position);
        try {
            mDetailView.showExif(getExifInfomation(photoNote.getBigPhotoPathWithoutFile()));
            gps(photoNote.getBigPhotoPathWithoutFile());
        } catch (IOException e) {
            e.printStackTrace();
            mDetailView.showExif(mContext.getResources().getString(R.string.toast_fail));
        }
    }

    @Override
    public void showNote(int position) {
        PhotoNote photoNote = mPhotoNoteList.get(position);
        String title;
        String content;
        if (TextUtils.isEmpty(photoNote.getTitle())) {
            title = mContext.getResources().getString(R.string.detail_content_nothing);
        } else {
            title = photoNote.getTitle();
        }
        if (TextUtils.isEmpty(photoNote.getTitle())) {
            content = mContext.getResources().getString(R.string.detail_content_nothing);
        } else {
            content = photoNote.getTitle();
        }
        String createdTime = decodeTimeInDetail(photoNote.getCreatedNoteTime());
        String editedTime = decodeTimeInDetail(photoNote.getEditedNoteTime());
        mDetailView.showNote(title, content, createdTime, editedTime);
        showExif();
    }

    @Override
    public void updateNote(String label, int position, int comparator) {
        mCategoryLabel = label;
        mComparator = comparator;
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(mCategoryLabel, mComparator);
        showNote(position);
    }

    @Override
    public void jump2EditTextActivity() {
        mDetailView.jump2EditTextActivity(mCategoryLabel, mDetailView.getCurrentPosition(), mComparator);
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

    private String getExifInfomation(String path) throws IOException {
        String enter = "\n";
        StringBuilder sb = new StringBuilder();
        ExifInterface exifInterface = new ExifInterface(path);
        String fDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        sb.append(mContext.getResources().getString(R.string.detail_dateTime))
                .append(checkExifData(fDateTime))
                .append(enter);
        String fOrientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        sb.append(mContext.getResources().getString(R.string.detail_orientation))
                .append(fOrientation)
                .append(enter);
        String fFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        sb.append(mContext.getResources().getString(R.string.detail_flash))
                .append((TextUtils.isEmpty(fFlash) || "0".equals(fFlash) || "null".equals(fFlash)) ? mContext.getResources().getString(R.string.detail_flash_close) :
                        mContext.getResources().getString(R.string.detail_flash_open))
                .append(enter);
        String fImageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        if (TextUtils.isEmpty(fImageWidth) || "0".equals(fImageWidth) || "null".equals(fImageWidth)) {
            int[] size = FilePathUtils.getPictureSize(path);
            sb.append(mContext.getResources().getString(R.string.detail_image_width))
                    .append(size[0])
                    .append(enter);
            sb.append(mContext.getResources().getString(R.string.detail_image_length))
                    .append(size[1])
                    .append(enter);
        } else {
            sb.append(mContext.getResources().getString(R.string.detail_image_width))
                    .append(fImageWidth)
                    .append(enter);
            String fImageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            sb.append(mContext.getResources().getString(R.string.detail_image_length))
                    .append(fImageLength)
                    .append(enter);
        }
        String fWhiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        sb.append(mContext.getResources().getString(R.string.detail_white_balance))
                .append((TextUtils.isEmpty(fImageWidth) || "0".equals(fWhiteBalance) || "null".equals(fImageWidth.toLowerCase())) ? mContext.getResources().getString(R.string.detail_wb_auto) :
                        mContext.getResources().getString(R.string.detail_wb_manual))
                .append(enter);
        String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if (TextUtils.isEmpty(longitude) || "null".equals(longitude.toLowerCase())) {
            sb.append(mContext.getResources().getString(R.string.detail_longitude))
                    .append(mContext.getResources().getString(R.string.detail_unknown))
                    .append(enter);
            sb.append(mContext.getResources().getString(R.string.detail_latitude))
                    .append(mContext.getResources().getString(R.string.detail_unknown))
                    .append(enter);
        } else {
            String[] longitudeSs = longitude.split(",");
            double longitudesD = 0;
            longitudesD += Double.parseDouble(longitudeSs[0].split("/")[0]);
            longitudesD += (((int) (Double.parseDouble(longitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(longitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;
            sb.append(mContext.getResources().getString(R.string.detail_longitude))
                    .append(longitudesD + "")
                    .append(enter);

            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String[] latitudeSs = latitude.split(",");
            double latitudesD = 0;
            latitudesD += Double.parseDouble(latitudeSs[0].split("/")[0]);
            latitudesD += (((int) (Double.parseDouble(latitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(latitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;
            sb.append(mContext.getResources().getString(R.string.detail_latitude))
                    .append(latitudesD + "")
                    .append(enter);
        }
        String fMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        sb.append(mContext.getResources().getString(R.string.detail_make))
                .append(checkExifData(fMake))
                .append(enter);
        String fModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        sb.append(mContext.getResources().getString(R.string.detail_model))
                .append(checkExifData(fModel))
                .append(enter);
        String fAperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
        sb.append(mContext.getResources().getString(R.string.detail_aperture)).append(checkExifData(fAperture)).append(enter);
        String fExposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        sb.append(mContext.getResources().getString(R.string.detail_exposure_time))
                .append(checkExifData(fExposureTime))
                .append(enter);
        String fFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        sb.append(mContext.getResources().getString(R.string.detail_focal_length))
                .append(checkExifData(fFocalLength))
                .append(enter);
        String fISOSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        sb.append(mContext.getResources().getString(R.string.detail_iso))
                .append(checkExifData(fISOSpeedRatings))
                .append(enter);
        return sb.toString();
    }

    private String checkExifData(String data) {
        if (TextUtils.isEmpty(data) || data.toLowerCase().equals("null")) {
            return mContext.getResources().getString(R.string.detail_unknown);
        } else {
            return data;
        }
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
    }

    @Override
    public void detachView() {
        mSearch.destroy();
    }
}
