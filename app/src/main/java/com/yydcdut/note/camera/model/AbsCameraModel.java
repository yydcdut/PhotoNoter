package com.yydcdut.note.camera.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.yydcdut.note.ICameraData;
import com.yydcdut.note.service.CameraService;
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
    public long capture(boolean sound) {
        return 0l;
    }

    @Override
    public void onCreate(Context context) {
        bindService(context);
    }

    @Override
    public void onDestroy(Context context) {
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

    public boolean addData2Service(byte[] data, String cameraId, long time, String category, boolean isMirror, int ratio) {
        boolean bool = true;
        int size = data.length;
        String fileName = time + ".data";
        File file = new File(FilePathUtils.getPath() + fileName);
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
        try {
            mCameraService.add(fileName, size, cameraId, time, category, isMirror, ratio);
        } catch (RemoteException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }
}
