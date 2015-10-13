// ICameraData.aidl
package com.yydcdut.note;

// Declare any non-default types here with import statements

interface ICameraData {

    void add(String fileName,int size, String cameraId, long time, String category, boolean isMirror, int ratio);
}
