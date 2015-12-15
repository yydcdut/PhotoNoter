package com.yydcdut.note.utils.ImageManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by yuyidong on 15/8/7.
 * ImageLoader管理类，如果以后
 */
public class ImageLoaderManager {

    public static void displayImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView);
    }

    public static void init(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static void displayImage(String uri, ImageView imageView, ImageLoadingListenerAdapter listener) {
        ImageLoader.getInstance().displayImage(uri, imageView, listener);
    }

    public static Bitmap loadImageSync(String uri) {
        return ImageLoader.getInstance().loadImageSync(uri);
    }

    public static void clearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }


}
