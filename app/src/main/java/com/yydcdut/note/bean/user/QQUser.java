package com.yydcdut.note.bean.user;

import com.yydcdut.note.utils.FilePathUtils;

import java.io.File;

/**
 * Created by yuyidong on 15/8/11.
 */
public class QQUser implements IUser {
    private String name;
    private String netImagePath;

    public QQUser(String nameQQ, String netImagePath) {
        this.name = nameQQ;
        this.netImagePath = netImagePath;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getNetImagePath() {
        return netImagePath;
    }

    public String getImagePath() {
        if (new File(FilePathUtils.getQQImagePath()).exists()) {
            return "file://" + FilePathUtils.getQQImagePath();
        } else {
            //todo 这个发个post出去
//            FilePathUtils.saveImage(FilePathUtils.getQQImagePath(),
//                    ImageLoaderManager.loadImageSync(getNetImagePath()));
            return getNetImagePath();
        }
    }
}
