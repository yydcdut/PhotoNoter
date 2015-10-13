package us.pinguo.edit.sdk.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by marui on 14-3-27.
 */
public class ImageLoaderView extends ImageView {

    private DisplayImageOptions.Builder mOptionsBuilder = new DisplayImageOptions.Builder().
            cacheInMemory(true).
            cacheOnDisk(true);
    private ImageLoadingListener mListener;

    public ImageLoaderView(Context context) {
        super(context);
    }

    public ImageLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageLoaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DisplayImageOptions.Builder getOptionsBuilder() {
        return mOptionsBuilder;
    }

    public void setImageLoadingListener(ImageLoadingListener listener) {
        mListener = listener;
    }

    /**
     * 配置加载时的默认图片
     *
     * @param resId
     */
    public void setDefaultImage(int resId) {
        mOptionsBuilder.showImageOnFail(resId).showImageForEmptyUri(resId).showImageOnLoading(resId);
    }

    /**
     * 以圆角方式显示图片
     *
     * @param roundPixels
     */
    public void displayRoundCorner(int roundPixels) {
        mOptionsBuilder.displayer(new RoundedBitmapDisplayer(roundPixels));
    }

    /**
     * 以圆图方式显示图片
     */
    public void displayCircle() {
        mOptionsBuilder.displayer(new CircleBitmapDisplayer());
    }

    /**
     * 显示图片时添加FadeIn动画
     */
    public void displayWithFadeIn() {
        mOptionsBuilder.displayer(new FadeInBitmapDisplayer(400));
    }

    /**
     * 进行异步图片加载，url支持：
     * HTTP("http"),
     * HTTPS("https"),
     * FILE("file"),
     * CONTENT("content"),
     * ASSETS("assets"),
     * DRAWABLE("drawable"),等类型
     *
     * @param imgUrl
     */
    public void setImageUrl(String imgUrl) {
        if (imgUrl == null) {
            imgUrl = "";
        }
        ImageLoader.getInstance().displayImage(imgUrl, this, mOptionsBuilder.build(), mListener);
    }

    /**
     * 加载url图片，加载失败时使用备用url加载
     *
     * @param imgUrl
     * @param backupUrl
     */
    public void setImageUrl(String imgUrl, final String backupUrl) {
        if (TextUtils.isEmpty(backupUrl)) {
            setImageUrl(imgUrl);
        } else {
            ImageLoader.getInstance().displayImage(imgUrl, this, mOptionsBuilder.build(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    setImageUrl(backupUrl);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

}
