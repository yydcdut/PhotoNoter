package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yydcdut.note.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 16/2/17.
 */
public class CameraGridLayout extends FrameLayout {
    @Bind(R.id.view_grid)
    CameraGridView mCameraGridView;

    public CameraGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_camera_grid, this, true);
        ButterKnife.bind(this, view);
    }

    public void setAspectRatio(int width, int height) {
        mCameraGridView.setAspectRatio(width, height);
    }

    public void setMargin(int top, int bottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.setMargins(0, top, 0, bottom);
        setLayoutParams(layoutParams);
    }

    public void open() {
        setVisibility(VISIBLE);
    }

    public void close() {
        setVisibility(GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}
