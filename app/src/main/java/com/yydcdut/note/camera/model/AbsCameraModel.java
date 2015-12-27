package com.yydcdut.note.camera.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yydcdut.note.ICameraData;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.service.CameraService;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yuyidong on 15/7/17.
 */
public class AbsCameraModel implements ICameraModel {

    private boolean mIsBind = false;

    private ICameraData mCameraService;

    private LocationClient mLocationClient;
    private double mLatitude;
    private double mLontitude;

    @Override
    public ICameraSetting getSettingModel() {
        return null;
    }

    @Override
    public ICameraFocus getFocusModel() {
        return null;
    }

    @Override
    public void setTouchArea(int width, int height) {

    }

    @Override
    public void openCamera(String id, int orientation) {

    }

    @Override
    public void reopenCamera(String id, int orientation) {

    }

    @Override
    public void startPreview() {

    }

    @Override
    public void reStartPreview() {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void closeCamera() {

    }

    @Override
    public long capture(boolean sound, int ratio, boolean isMirror) {
        return 0l;
    }

    @Override
    public void onCreate(Context context) {
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLatitude = bdLocation.getLatitude();
                mLontitude = bdLocation.getLongitude();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(checkGeoLocation.isChecked());//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        bindService(context);
    }

    @Override
    public void onDestroy(Context context) {
        mLocationClient.stop();
        unBindService(context);
    }

    private void bindService(Context context) {
        Intent intent = new Intent(context, CameraService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCameraService = ICameraData.Stub.asInterface(service);
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCameraService = null;
            mIsBind = false;
        }
    };

    private void unBindService(Context context) {
        if (mIsBind) {
            context.unbindService(mServiceConnection);
            mIsBind = false;
        }
    }

    public boolean addData2Service(byte[] data, String cameraId, long time, int categoryId,
                                   boolean isMirror, int ratio) {
        boolean bool = true;
        int size = data.length;
        String fileName = time + ".data";
        File file = new File(FilePathUtils.getSandBoxDir() + fileName);
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    bool = false;
                    e.printStackTrace();
                }
            }
        }

        int orientation = 0;//todo 这个还没做，下个版本做

        String latitude0 = String.valueOf((int) mLatitude) + "/1,";
        String latitude1 = String.valueOf((int) ((mLatitude - (int) mLatitude) * 60) + "/1,");
        String latitude2 = String.valueOf((int) ((((mLatitude - (int) mLatitude) * 60) - ((int) ((mLatitude - (int) mLatitude) * 60))) * 60 * 10000)) + "/10000";
        String latitude = new StringBuilder(latitude0).append(latitude1).append(latitude2).toString();
        String lontitude0 = String.valueOf((int) mLontitude) + "/1,";
        String lontitude1 = String.valueOf((int) ((mLontitude - (int) mLontitude) * 60) + "/1,");
        String lontitude2 = String.valueOf((int) ((((mLontitude - (int) mLontitude) * 60) - ((int) ((mLontitude - (int) mLontitude) * 60))) * 60 * 10000)) + "/10000";
        String lontitude = new StringBuilder(lontitude0).append(lontitude1).append(lontitude2).toString();
        int whiteBalance = 0;
        if (getSettingModel().getSupportedWhiteBalance().size() > 0) {
            if (getSettingModel().getWhiteBalance() != ICameraParams.WHITE_BALANCE_AUTO) {
                whiteBalance = 1;
            }
        }
        //todo 这里的flash是指拍照的那个时候闪光灯是否打开了,所以啊。。。这个。。。。
        int flash = 0;
        if (getSettingModel().getSupportedFlash().size() > 0) {
            if (getSettingModel().getFlash() != ICameraParams.FLASH_OFF) {
                flash = 1;
            }
        }
        Size size1 = getSettingModel().getPictureSize();
        int imageLength = size1.getHeight();
        int imageWidth = size1.getWidth();
        if (ratio == Const.CAMERA_SANDBOX_PHOTO_RATIO_1_1) {
            imageLength = imageWidth;
        }
        String make = Build.BRAND;
        String model = Build.MODEL;
        try {
            mCameraService.add(fileName, size, cameraId, time, categoryId, isMirror, ratio,
                    orientation, latitude, lontitude, whiteBalance, flash, imageLength, imageWidth, make, model);
        } catch (RemoteException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }
}
