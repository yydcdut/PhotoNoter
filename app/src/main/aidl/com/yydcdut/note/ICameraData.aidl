// ICameraData.aidl
package com.yydcdut.note;

// Declare any non-default types here with import statements

interface ICameraData {

    void add(String fileName, int size, String cameraId, long time,
            int categoryId, boolean isMirror, int ratio, int orientation,
            String latitude, String longitude, int whiteBalance, int flash,
            int imageLength, int imageWidth, String make, String model, int imageFormat);
}
