package com.yydcdut.note.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yyd on 15-4-16.
 */
public class FilePathUtils {
    private static final String DIR_NAME = "PhotoNoter";
    private static String FULL_PATH;

    private static final String SMALL_PICTURE_DIR_NAME = ".small";
    private static String FULL_SMALL_PATH;

    private static final String OTHER_DIR_NAME = ".other";

    private static final String QQ_FILE = "qq.jpg";

    private static final String SANDBOX_DIR_NAME = ".database";

    public static void initEnvironment(Context context) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            FULL_PATH = context.getFilesDir().getAbsolutePath() + File.separator;
        } else {
            FULL_PATH = Environment.getExternalStorageDirectory() + File.separator + DIR_NAME + File.separator;
        }
        FULL_SMALL_PATH = FULL_PATH + SMALL_PICTURE_DIR_NAME + File.separator;
        createDirIfNotExist();
        createSmallDirIfNotExist();
        createOtherImageDirIfNotExist();
        createSandBoxDirIfNotExist();
    }

    public static void createSandBoxDirIfNotExist() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(FULL_PATH + SANDBOX_DIR_NAME);
            if (file.isDirectory()) {
                return;
            }
            if (file.isFile()) {
                file.delete();
                file.mkdir();
            }
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    /**
     * 创建大图的文件夹
     */
    public static void createDirIfNotExist() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(FULL_PATH);
            if (file.isDirectory()) {
                return;
            }
            if (file.isFile()) {
                file.delete();
                file.mkdir();
            }
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    /**
     * 创建缩略图的文件夹
     */
    public static void createSmallDirIfNotExist() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file2 = new File(FULL_SMALL_PATH);
            if (file2.isDirectory()) {
                return;
            }
            if (file2.isFile()) {
                file2.delete();
                file2.mkdir();
            }
            if (!file2.exists()) {
                file2.mkdir();
            }
        }
    }

    public static void createOtherImageDirIfNotExist() {
        String dirPath = FULL_PATH + OTHER_DIR_NAME;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (dir.isFile()) {
            dir.delete();
            dir.mkdir();
        }
    }

    public static String getPath() {
        return FULL_PATH;
    }

    public static String getTempFilePath() {
        return FULL_PATH + "temp.jpg";
    }

    public static String getSmallPath() {
        return FULL_SMALL_PATH;
    }

    public static final int BIG_PHOTO_NOT_EXIST = 0;
    public static final int SMALL_PHOTO_NOT_EXIST = 1;
    public static final int ALL_EXIST = 2;
    public static final int ALL_NOT_EXIST = 3;

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return BIG_PHOTO_NOT_EXIST/SMALL_PHOTO_NOT_EXIST/ALL_EXIST/ALL_NOT_EXIST
     */
    public static int isFileExist(String fileName) {
        if (fileName == null) {
            return -1;
        }
        File bigFile = new File(FULL_PATH + fileName);
        File smallFile = new File(FULL_SMALL_PATH + fileName);
        if (bigFile.exists() && smallFile.exists()) {
            return ALL_EXIST;
        } else if (!bigFile.exists() && smallFile.exists()) {
            return BIG_PHOTO_NOT_EXIST;
        } else if (bigFile.exists() && !smallFile.exists()) {
            return SMALL_PHOTO_NOT_EXIST;
        } else {
            return ALL_NOT_EXIST;
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteAllFiles(String fileName) {
        if (fileName == null) {
            return false;
        }
        File bigFile = new File(FULL_PATH + fileName);
        File smallFile = new File(FULL_SMALL_PATH + fileName);
        boolean bool = true;
        if (bigFile.exists()) {
            bool &= bigFile.delete();
        }
        if (smallFile.exists()) {
            bool &= smallFile.delete();
        }
        return bool;
    }

    /**
     * 存大图
     *
     * @param fileName
     * @param bitmap
     */
    public static boolean savePhoto(String fileName, Bitmap bitmap) {
        if (fileName == null || bitmap == null) {
            return false;
        }
        boolean bool = true;
        File file = new File(FilePathUtils.getPath() + fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
            bos.flush();// 输出
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();// 关闭
                }
            } catch (IOException e) {
                bool = false;
                e.printStackTrace();
            }
        }
        return bool;
    }

    /**
     * 保存小图
     *
     * @param fileName
     * @param bitmap
     * @return
     */
    public static boolean saveSmallPhoto(String fileName, Bitmap bitmap) {
        if (fileName == null || bitmap == null) {
            return false;
        }
        int newWidth = Const.SMALL_PHOTO_WIDTH;
        int newHeight = (int) (((float) bitmap.getHeight()) * Const.SMALL_PHOTO_WIDTH / bitmap.getWidth());
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        boolean bool = true;
        //存小图
        File file1 = new File(FilePathUtils.getSmallPath() + fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file1));
            resizeBmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);// 将图片压缩到流中
            bos.flush();// 输出
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();// 关闭
                }
            } catch (IOException e) {
                bool = false;
                e.printStackTrace();
            }
        }

        return bool;
    }

    /**
     * 从SDK中读出来的图直接就是宽度为500的
     *
     * @param fileName
     * @param bitmap
     * @return
     */
    public static boolean saveSmallPhotoFromSDK(String fileName, Bitmap bitmap) {
        if (fileName == null || bitmap == null) {
            return false;
        }
        boolean bool = true;
        //存小图
        File file1 = new File(FilePathUtils.getSmallPath() + fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file1));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);// 将图片压缩到流中
            bos.flush();// 输出
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();// 关闭
                }
            } catch (IOException e) {
                bool = false;
                e.printStackTrace();
            }
        }
        return bool;
    }

    /**
     * 从大图片地址去保存小图片
     *
     * @param bigPhotoPathWithFile
     * @param photoName
     */
    public static boolean saveSmallPhotoFromBigPhoto(String bigPhotoPathWithFile, String photoName) {
        Bitmap bitmap = ImageLoaderManager.loadImageSync(bigPhotoPathWithFile);
        return saveSmallPhoto(photoName, bitmap);
    }

    /**
     * 检查SD卡的存储是否够用
     * 5M一下就不够用
     *
     * @return
     */
    public static boolean isSDCardStoredEnough() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());

            long blockSize = 0;
            long availCount = 0;
            long available = 0;
            if (Build.VERSION.SDK_INT < 18) {
                blockSize = sf.getBlockSize();
                availCount = sf.getAvailableBlocks();
                available = availCount * blockSize / 1024 / 1024;//单位MB;
            } else {
                blockSize = sf.getBlockSizeLong();
                availCount = sf.getAvailableBlocksLong();
                available = availCount * blockSize / 1024 / 1024;//单位MB;
            }
            if (available > 5) {//大于5M
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static long[] getSDCardStorage() {
        long[] storage = new long[]{-1, -1};
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = 0;
            long availCount = 0;
            long totalCount = 0;
            if (Build.VERSION.SDK_INT < 18) {
                blockSize = sf.getBlockSize();
                availCount = sf.getAvailableBlocks();
                totalCount = sf.getBlockCount();
                storage[0] = availCount * blockSize / 1024 / 1024;//单位MB;
                storage[1] = totalCount * blockSize / 1024 / 1024;
            } else {
                blockSize = sf.getBlockSizeLong();
                availCount = sf.getAvailableBlocksLong();
                totalCount = sf.getBlockCountLong();
                storage[0] = availCount * blockSize / 1024 / 1024;//单位MB;
                storage[1] = totalCount * blockSize / 1024 / 1024;
            }
        }
        return storage;
    }

    private static long calculateDirSize(File dir) {
        long size = 0l;
        File[] filesOrDirs = dir.listFiles();
        for (File file : filesOrDirs) {
            if (file.isDirectory()) {
                size += calculateDirSize(file);
            } else {
                size += file.length();
            }
        }
        return size;
    }


    public static long getFolderStorage() {
        long size = 0l;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = new File(FULL_PATH);
            size += calculateDirSize(dir);
            return (size / 1024 / 1024);
        } else {
            return -1l;
        }
    }

    /**
     * 获得图片大小
     *
     * @param filePath 图片路径
     * @return
     */
    public static int[] getPictureSize(String filePath) {
        int[] array = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        final int width = options.outWidth;
        final int height = options.outHeight;
        options.inJustDecodeBounds = false;
        array[0] = width;
        array[1] = height;
        return array;
    }

    /**
     * 获得图片大小
     *
     * @param inputStream
     * @return
     */
    public static int[] getPictureSize(InputStream inputStream) {
        int[] array = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        final int width = options.outWidth;
        final int height = options.outHeight;
        options.inJustDecodeBounds = false;
        array[0] = width;
        array[1] = height;
        return array;
    }

    /**
     * 复制文件
     *
     * @param fromPath
     * @param toPath
     * @throws IOException
     */
    public static void copyFile(String fromPath, String toPath) throws IOException {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);

        InputStream inputStream = new FileInputStream(fromFile);
        OutputStream outputStream = new FileOutputStream(toFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    /**
     * 复制文件
     *
     * @param inputStream
     * @param toPath
     * @throws IOException
     */
    public static void copyFile(InputStream inputStream, String toPath) throws IOException {
        File toFile = new File(toPath);

        OutputStream outputStream = new FileOutputStream(toFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    /**
     * QQ
     *
     * @return
     */
    public static String getQQImagePath() {
        return FULL_PATH + OTHER_DIR_NAME + File.separator + QQ_FILE;
    }

    /**
     * 保存高斯模糊图片
     *
     * @param bitmap
     * @return
     */
    public static boolean saveImage(String path, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        boolean bool = true;
        File file = new File(path);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
            bos.flush();// 输出
        } catch (IOException e) {
            bool = false;
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();// 关闭
                }
            } catch (IOException e) {
                bool = false;
                e.printStackTrace();
            }
        }
        return bool;
    }

    public static String getSandBoxDir() {
        return FULL_PATH + SANDBOX_DIR_NAME + File.separator;
    }


}
