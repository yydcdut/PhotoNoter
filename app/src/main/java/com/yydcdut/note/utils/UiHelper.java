package com.yydcdut.note.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.view.View;

public class UiHelper {

    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            view.setBackgroundDrawable(d);
        }
    }

    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postInvalidateOnAnimation();
        } else {
            view.invalidate();
        }
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
