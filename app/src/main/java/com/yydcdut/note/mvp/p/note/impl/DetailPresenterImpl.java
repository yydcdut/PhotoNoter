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
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.note.IDetailPresenter;
import com.yydcdut.note.mvp.v.note.IDetailView;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/16.
 */
public class DetailPresenterImpl implements IDetailPresenter, OnGetGeoCoderResultListener {
    private IDetailView mDetailView;
    private Context mContext;
    private RxPhotoNote mRxPhotoNote;
    private LocalStorageUtils mLocalStorageUtils;

    /* Data */
    private int mCategoryId;
    private int mComparator;
    private int mInitPosition;

    /* Baidu Map */
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    @Inject
    public DetailPresenterImpl(@ContextLife("Activity") Context context, RxPhotoNote rxPhotoNote,
                               LocalStorageUtils localStorageUtils) {
        mContext = context;
        mRxPhotoNote = rxPhotoNote;
        mLocalStorageUtils = localStorageUtils;
    }

    @Override
    public void attachView(IView iView) {
        mDetailView = (IDetailView) iView;
        mDetailView.setFontSystem(mLocalStorageUtils.getSettingFontSystem());
        initBaiduMap();
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNoteList -> {
                    mDetailView.setViewPagerAdapter(photoNoteList, mInitPosition, mComparator);
                    showNote(mInitPosition);
                });

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
    public void bindData(int categoryID, int position, int comparator) {
        mCategoryId = categoryID;
        mInitPosition = position;
        mComparator = comparator;
    }

    @Override
    public void showExif() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNoteList -> {
                    int position = mDetailView.getCurrentPosition();
                    PhotoNote photoNote = photoNoteList.get(position);
                    try {
                        mDetailView.showExif(getExifInformation(photoNote.getBigPhotoPathWithoutFile()));
                        gps(photoNote.getBigPhotoPathWithoutFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        mDetailView.showExif(mContext.getResources().getString(R.string.toast_fail));
                    }
                });
    }

    @Override
    public void showNote(int position) {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(position))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote1 -> {
                    String title;
                    String content;
                    if (TextUtils.isEmpty(photoNote1.getTitle())) {
                        title = mContext.getResources().getString(R.string.detail_content_nothing);
                    } else {
                        title = photoNote1.getTitle();
                    }
                    if (TextUtils.isEmpty(photoNote1.getContent())) {
                        content = mContext.getResources().getString(R.string.detail_content_nothing);
                    } else {
                        content = photoNote1.getContent();
                    }
                    String createdTime = decodeTimeInDetail(photoNote1.getCreatedNoteTime());
                    String editedTime = decodeTimeInDetail(photoNote1.getEditedNoteTime());
                    mDetailView.showNote(title, content, createdTime, editedTime);
                    showExif();
                });
    }

    @Override
    public void updateNote(int categoryId, int position, int comparator) {
        mCategoryId = categoryId;
        mComparator = comparator;
        showNote(position);
    }

    @Override
    public void jump2EditTextActivity() {
        mDetailView.jump2EditTextActivity(mCategoryId, mDetailView.getCurrentPosition(), mComparator);
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

    private String getExifInformation(String path) throws IOException {
        String enter = "\n";
        StringBuilder sb = new StringBuilder();
        ExifInterface exifInterface = new ExifInterface(path);
        String fDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        sb.append(mContext.getResources().getString(R.string.detail_dateTime))
                .append(checkExifData(fDateTime))
                .append(enter);
        String fOrientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        if (fOrientation.equals(String.valueOf(ExifInterface.ORIENTATION_NORMAL))) {
            fOrientation = "0";
        } else if (fOrientation.equals(String.valueOf(ExifInterface.ORIENTATION_ROTATE_90))) {
            fOrientation = "90";
        } else if (fOrientation.equals(String.valueOf(ExifInterface.ORIENTATION_ROTATE_270))) {
            fOrientation = "270";
        } else if (fOrientation.equals(String.valueOf(ExifInterface.ORIENTATION_ROTATE_180))) {
            fOrientation = "180";
        } else if (fOrientation.equals(String.valueOf(ExifInterface.ORIENTATION_ROTATE_270 | ExifInterface.ORIENTATION_FLIP_HORIZONTAL))) {
            fOrientation = "270";
        } else {
            fOrientation = mContext.getResources().getString(R.string.detail_unknown);
        }
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
