package com.yydcdut.note.bean;

import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;

import java.io.File;

/**
 * Created by yuyidong on 15/8/11.
 */
public class QQUser implements IUser {
    private String openId;
    private String accessToken;

    private String name;
    private String netImagePath;

    public QQUser(String openId, String accessToken, String nameQQ, String netImagePath) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.name = nameQQ;
        this.netImagePath = netImagePath;
    }

    public String getOpenId() {
        return openId;
    }

    public String getAccessToken() {
        return accessToken;
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
            FilePathUtils.saveImage(FilePathUtils.getQQImagePath(),
                    ImageLoaderManager.loadImageSync(UserCenter.getInstance().getQQ().getNetImagePath()));
            return UserCenter.getInstance().getQQ().getNetImagePath();
        }
    }
}
