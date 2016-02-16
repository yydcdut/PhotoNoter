package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.AppCompat;

/**
 * Created by yuyidong on 16/2/3.
 */
public class AutoFitPreviewView extends FrameLayout implements
        SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    private AutoFitSurfaceView mAutoFitSurfaceView;
    private AutoFitTextureView mAutoFitTextureView;

    private PreviewSurface mHolderSurface;
    private PreviewSurface mTextureSurface;

    public AutoFitPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_preview, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (AppCompat.AFTER_ICE_CREAM) {
            mAutoFitTextureView = (AutoFitTextureView) findViewById(R.id.ttv_camera);
            mAutoFitTextureView.setVisibility(VISIBLE);
        } else {
            mAutoFitSurfaceView = (AutoFitSurfaceView) findViewById(R.id.sv_camera);
            mAutoFitSurfaceView.setVisibility(VISIBLE);
        }
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        if (mAutoFitTextureView != null) {
            mAutoFitTextureView.setAspectRatio(width, height);
            mAutoFitTextureView.setSurfaceTextureListener(this);
        } else {
            mAutoFitSurfaceView.setAspectRatio(width, height);
            mAutoFitSurfaceView.getHolder().addCallback(this);
        }
    }

    public int getAspectWidth() {
        if (mAutoFitTextureView != null) {
            return mAutoFitTextureView.getMeasuredWidth();
        } else {
            return mAutoFitSurfaceView.getMeasuredWidth();
        }
    }

    public int getAspectHeight() {
        if (mAutoFitTextureView != null) {
            return mAutoFitTextureView.getMeasuredHeight();
        } else {
            return mAutoFitSurfaceView.getMeasuredHeight();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolderSurface = new PreviewSurface(holder);
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceAvailable(mHolderSurface, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceDestroy();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mTextureSurface = new PreviewSurface(surface);
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceAvailable(mTextureSurface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceDestroy();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private SurfaceListener mSurfaceListener;

    public void setSurfaceListener(SurfaceListener surfaceListener) {
        mSurfaceListener = surfaceListener;
    }

    public interface SurfaceListener {
        void onSurfaceAvailable(PreviewSurface surface, int width, int height);

        void onSurfaceDestroy();
    }

    public class PreviewSurface {
        private SurfaceHolder mSurfaceHolder = null;
        private SurfaceTexture mSurfaceTexture = null;

        public PreviewSurface(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        public PreviewSurface(SurfaceTexture surfaceTexture) {
            mSurfaceTexture = surfaceTexture;
        }

        public SurfaceHolder getSurfaceHolder() {
            return mSurfaceHolder;
        }

        public SurfaceTexture getSurfaceTexture() {
            return mSurfaceTexture;
        }
    }
}
