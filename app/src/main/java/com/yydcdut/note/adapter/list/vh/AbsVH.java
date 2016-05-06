package com.yydcdut.note.adapter.list.vh;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by yuyidong on 16/5/5.
 */
public abstract class AbsVH {
    public View itemView;

    public AbsVH(@NonNull View itemView) {
        this.itemView = itemView;
    }
}
