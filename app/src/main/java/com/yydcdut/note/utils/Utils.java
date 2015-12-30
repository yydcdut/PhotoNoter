package com.yydcdut.note.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

public class Utils {

    /**
     * 屏幕宽度
     */
    public static int sScreenWidth;
    /**
     * 屏幕高度
     */
    public static int sScreenHeight;

    public static void init(Context context) {
        sScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        sScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getPaletteColor(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        Palette palette = builder.generate();
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        if (vibrantSwatch == null) {
            Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
            if (lightVibrant == null) {
                Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();
                if (darkVibrant == null) {
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    if (mutedSwatch == null) {
                        Palette.Swatch lightMuted = palette.getMutedSwatch();
                        if (lightMuted == null) {
                            Palette.Swatch darkMuted = palette.getDarkMutedSwatch();
                            if (darkMuted == null) {
                                return Color.WHITE;
                            } else {
                                return darkMuted.getRgb();
                            }
                        } else {
                            return lightMuted.getRgb();
                        }
                    } else {
                        return mutedSwatch.getRgb();
                    }
                } else {
                    return darkVibrant.getRgb();
                }
            } else {
                return lightVibrant.getRgb();
            }
        } else {
            return vibrantSwatch.getRgb();
        }
    }
}
