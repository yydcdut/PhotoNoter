package com.yydcdut.note.mvp.p.service;

import com.yydcdut.note.mvp.IPresenter;

/**
 * Created by yuyidong on 15/11/22.
 */
public interface ICameraServicePresenter extends IPresenter {
    void stopThread();

    void add2DB(String fileName, int size, String cameraId, long time, int categoryId,
                boolean isMirror, int ratio, int orientation,
                String latitude, String lontitude, int whiteBalance, int flash,
                int imageLength, int imageWidth, String make, String model);
}
