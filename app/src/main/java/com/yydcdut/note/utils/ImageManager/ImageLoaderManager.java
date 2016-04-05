package com.yydcdut.note.utils.ImageManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by yuyidong on 15/8/7.
 * ImageLoader管理类，如果以后
 */
public class ImageLoaderManager {
    private static DisplayImageOptions sOptions;

    private static DisplayImageOptions sGalleryOptions;

    public static void displayImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, sOptions);
    }

    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions displayImageOptions) {
        if (displayImageOptions == null) {
            displayImage(uri, imageView);
        } else {
            ImageLoader.getInstance().displayImage(uri, imageView, displayImageOptions);
        }
    }

    public static void init(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);

        sOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
//                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//                .decodingOptions(android.graphics.BitmapFactory.Options decodingOptions)//设置图片的解码配置
//.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
//设置图片加入缓存前，对bitmap进行设置
//.preProcessor(BitmapProcessor preProcessor)
//                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
//                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
    }

    public static void displayImage(String uri, ImageView imageView, ImageLoadingListenerAdapter listener) {
        ImageLoader.getInstance().displayImage(uri, imageView, sOptions, listener);
    }

    public static Bitmap loadImageSync(String uri) {
        return ImageLoader.getInstance().loadImageSync(uri, sOptions);
    }

    public static void clearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    public static void displayImageWihtoutCache(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build());
    }


    public static DisplayImageOptions getGalleryOptions() {
        if (sGalleryOptions == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            sGalleryOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .decodingOptions(options)//设置图片的解码配置
                    .build();//构建完成
        }
        return sGalleryOptions;

    }

}
