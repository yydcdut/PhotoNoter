package com.yydcdut.note.mvp.v.login;

import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.mvp.IView;

import java.util.List;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IUserDetailFragView extends IView {
    void initUserDetail(String location, String useAge, String phone, String android, String storage);

    void updateLocation(String location);

    void initUserImages(List<PhotoNote> list);

    void addView();

    void addQQView(boolean isQQLogin, String QQName);

    void addEvernoteView(boolean isEvernoteLogin, String evernoteName);

    void addUseStorageView(String useStorage);

    void addNoteNumberView(String noteNumber);

    void addSandBoxNumber(String sandboxNumber);

    void addWordNumber(String wordNumber);

    void addCloud(String cloud);

    void logoutQQ();

    void logoutEvernote();

    void showProgressBar();

    void hideProgressBar();

    void showQQ(String name, String path);

    void showSnakebar(String message);

}
