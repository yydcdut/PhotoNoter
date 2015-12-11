package com.yydcdut.note.bean.user;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/12/11.
 */
public class EvernoteUser implements IUser {
    private String name;

    public EvernoteUser(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNetImagePath() {
        return "drawable://" + R.drawable.ic_evernote_color;
    }

    @Override
    public String getImagePath() {
        return "drawable://" + R.drawable.ic_evernote_color;
    }
}
