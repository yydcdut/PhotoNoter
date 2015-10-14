package com.yydcdut.note.utils;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/10/14.
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static float MIN_SCALE = 0.85f;

    @Override
    public void transformPage(View view, float position) {
        CardView cardView = (CardView) view.findViewById(R.id.card_detail_layout);


        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            cardView.setCardElevation(0);
            cardView.setRadius(0);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        } else if (position == -1 || position == 1) {
            cardView.setCardElevation(0);
            cardView.setRadius(0);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        } else if (position < 1) { // (-1,1)
            // Modify the default slide transition to
            // shrink the page as well
            cardView.setCardElevation(NoteApplication.getContext().getResources().getDimension(R.dimen.card_elevation) * 2);
            cardView.setRadius(NoteApplication.getContext().getResources().getDimension(R.dimen.card_corner) * 3);
            cardView.setCardBackgroundColor(Color.WHITE);
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            // Scale the page down (between MIN_SCALE and 1)
            view.setPivotX(Evi.sScreenWidth / 2);
            view.setPivotY(Evi.sScreenHeight - 1);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            cardView.setCardElevation(0);
            cardView.setRadius(0);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
    }
}
