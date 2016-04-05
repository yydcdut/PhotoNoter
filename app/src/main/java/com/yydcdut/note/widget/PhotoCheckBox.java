package com.yydcdut.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by yuyidong on 16/3/23.
 */
public class PhotoCheckBox extends CheckBox implements CompoundButton.OnCheckedChangeListener {
    public PhotoCheckBox(Context context) {
        super(context);
        setOnCheckedChangeListener(this);
    }

    public PhotoCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnCheckedChangeListener(this);
    }

    public PhotoCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnCheckedChangeListener(this);
    }

    public void setCheckedWithoutCallback(boolean checked) {
        setOnCheckedChangeListener(null);
        setChecked(checked);
        setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mOnPhotoCheckedChangeListener != null) {
            mOnPhotoCheckedChangeListener.onPhotoCheckedChanged(buttonView, isChecked);
        }
    }

    private OnPhotoCheckedChangeListener mOnPhotoCheckedChangeListener;

    public void setOnPhotoCheckedChangeListener(OnPhotoCheckedChangeListener onPhotoCheckedChangeListener) {
        mOnPhotoCheckedChangeListener = onPhotoCheckedChangeListener;
    }

    public interface OnPhotoCheckedChangeListener {
        void onPhotoCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
}
