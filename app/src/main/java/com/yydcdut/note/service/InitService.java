package com.yydcdut.note.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.bean.Category;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.camera.param.Size;
import com.yydcdut.note.model.CategoryDBModel;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LocalStorageUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yyd on 15-4-26.
 */
public class InitService extends Service {
    private static final int QUITE = 3;
    private static final int ADD = 1;
    private AtomicInteger mNumber = new AtomicInteger(0);

    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLooper();
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                initDefaultCategory();
                initDefaultPhotoNote();
            }
        });
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                initCameraPictureSize();
            }
        });
    }

    /**
     * 初始化looper
     */
    private void initLooper() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case ADD:
                        stopService(mNumber.incrementAndGet());
                        break;
                }
                return false;
            }

        });
    }

    /**
     * 初始化相机的拍照尺寸、相机个数
     */
    private void initCameraPictureSize() {
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                //暂时用Camera的方法
                int total = Camera.getNumberOfCameras();
                LocalStorageUtils.getInstance().setCameraNumber(total);
                int[] cameraIds;
                if (total == 0) {
                    cameraIds = new int[0];
                } else if (total == 1) {
                    cameraIds = new int[]{0};
                } else {
                    cameraIds = new int[]{0, 1};
                }
                for (int i = 0; i < cameraIds.length; i++) {
                    try {
                        List<Size> sizeList = getPictureSizeJsonArray(cameraIds[i]);
                        Collections.sort(sizeList, new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                return -(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                            }
                        });
                        LocalStorageUtils.getInstance().setPictureSizes(String.valueOf(cameraIds[i]), sizeList);
                        Size suitableSize = sizeList.get(0);
                        LocalStorageUtils.getInstance().setPictureSize(String.valueOf(cameraIds[i]), suitableSize);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(ADD);
            }
        });
    }

    /**
     * 将List的数据存为JsonArray
     *
     * @param cameraId
     * @return
     * @throws JSONException
     */
    private List<Size> getPictureSizeJsonArray(int cameraId) throws JSONException {
        Camera camera = Camera.open(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> cameraSizeList = parameters.getSupportedPictureSizes();
        camera.release();
        List<Size> sizeList = new ArrayList<>();
        for (Camera.Size size : cameraSizeList) {
            sizeList.add(Size.parseSize(size));
        }
        return sizeList;
    }


    /**
     * 处理Category
     */
    private void initDefaultCategory() {
        CategoryDBModel.getInstance().saveCategory(new Category(0, "斯里兰卡 游记", 16, 0, true));
        mHandler.sendEmptyMessage(ADD);
    }

    private void initDefaultPhotoNote() {
        NoteApplication.getInstance().getExecutorPool().execute(new Runnable() {
            @Override
            public void run() {
                String[] outFileName = new String[]{
                        "0.jpg",
                        "1.jpg",
                        "2.jpg",
                        "3.jpg",
                        "4.jpg",
                        "5.jpg",
                        "6.jpg",
                        "7.jpg",
                        "8.jpg",
                        "9.jpg",
                        "10.jpg",
                        "11.jpg",
                        "12.jpg",
                        "13.jpg",
                        "14.jpg",
                        "15.jpg"
                };
                boolean bool = false;
                try {
                    bool = takePhotosToSdCard(outFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!bool) {
                    mHandler.sendEmptyMessage(ADD);
                    return;
                }
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[0], System.currentTimeMillis(), System.currentTimeMillis(), 800, 1067, "佛牙寺主殿外墙", "佛牙寺主殿外墙", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[1], System.currentTimeMillis(), System.currentTimeMillis(), 800, 1067, "佛牙寺主殿外墙.", "佛牙寺主殿外墙.", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[2], System.currentTimeMillis(), System.currentTimeMillis(), 800, 1067, "康提咦著名的佛牙寺", "康提咦著名的佛牙寺", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[3], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "刚好赶上当地的波耶节，佛牙寺内人头攒动", "刚好赶上当地的波耶节，佛牙寺内人头攒动", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[4], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "狮子岩壁上的精美画壁", "狮子岩壁上的精美画壁", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[5], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线.", "加勒海岸线.", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[6], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线", "加勒海岸线", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[7], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线", "加勒海岸线", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[8], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线-高跷海钓", "加勒海岸线-高跷海钓", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[9], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线-高跷海钓", "加勒海岸线-高跷海钓", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[10], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "加勒海岸线-高跷海钓", "加勒海岸线-高跷海钓", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[11], System.currentTimeMillis(), System.currentTimeMillis(), 720, 1280, "加勒古堡街景", "加勒古堡街景", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[12], System.currentTimeMillis(), System.currentTimeMillis(), 720, 1280, "加勒古堡街景", "加勒古堡街景", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[13], System.currentTimeMillis(), System.currentTimeMillis(), 720, 1280, "加勒古堡街景", "加勒古堡街景", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[14], System.currentTimeMillis(), System.currentTimeMillis(), 800, 600, "卧佛寺", "卧佛寺", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                PhotoNoteDBModel.getInstance().save(new PhotoNote(outFileName[15], System.currentTimeMillis(), System.currentTimeMillis(), 800, 1067, "科伦坡，致命时期英国建筑", "科伦坡，致命时期英国建筑", System.currentTimeMillis(), System.currentTimeMillis(), "斯里兰卡 游记"));
                mHandler.sendEmptyMessage(ADD);
            }
        });
    }


    private boolean takePhotosToSdCard(String[] outFileName) throws IOException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        String path = FilePathUtils.getPath();
        for (int i = 0; i < outFileName.length; i++) {
            FilePathUtils.copyFile(getResources().getAssets().open(outFileName[i]), path + outFileName[i]);
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync("file:/" + path + outFileName[i]);
            FilePathUtils.saveSmallPhoto(outFileName[i], bitmap);
        }
        return true;
    }

    /**
     * 退出
     *
     * @param number 当值满足为QUITE的时候退出
     */
    private void stopService(int number) {
        if (number == QUITE) {
            sendDataUpdateBroadcast(true, null, true, true, true);
            stopSelf();
        }
    }

    public void sendDataUpdateBroadcast(boolean delete, String move, boolean sort, boolean number, boolean photo) {
        Intent intent = new Intent();
        intent.setAction(Const.BROADCAST_PHOTONOTE_UPDATE);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_DELETE, delete);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_MOVE, move);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_SORT, sort);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_NUMBER, number);
        intent.putExtra(Const.TARGET_BROADCAST_CATEGORY_PHOTO, photo);
        sendBroadcast(intent);
    }
}
