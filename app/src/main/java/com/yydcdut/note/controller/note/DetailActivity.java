package com.yydcdut.note.controller.note;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.DetailPagerAdapter;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.utils.ZoomOutPageTransformer;

import java.util.List;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private List<PhotoNote> mPhotoNoteList;
    private int[] mColorArray = new int[3];
    private ImageView mBackgroundImage;
    private static final int INTENTION_LEFT = -1;
    private static final int INTENTION_RIGHT = 1;
    private static final int INTENTION_STOP = 0;
    private int mIntention = INTENTION_STOP;

    private ViewPager mViewPager;
    private DetailPagerAdapter mDetailPagerAdapter;

    @Override
    public int setContentView() {
        if (Utils.AFTER_LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        }
        return R.layout.activity_detail;
    }

    @Override
    public void initUiAndListener() {
        Bundle bundle = getIntent().getExtras();
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(bundle.getString(Const.CATEGORY_LABEL),
                bundle.getInt(Const.COMPARATOR_FACTORY));
        initToolBar();
        initViewPager(bundle);
        initBGView();
    }

    @Override
    public void startActivityAnimation() {

    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewPager(Bundle bundle) {
        mViewPager = (ViewPager) findViewById(R.id.vp_detail);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(),
                bundle.getString(Const.CATEGORY_LABEL), bundle.getInt(Const.COMPARATOR_FACTORY));
        mViewPager.setAdapter(mDetailPagerAdapter);
        mViewPager.setCurrentItem(bundle.getInt(Const.PHOTO_POSITION));
    }


    private void initBGView() {
        mBackgroundImage = (ImageView) findViewById(R.id.img_detail_bg);
    }

    private float mLastTimePositionOffset = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIntention == INTENTION_STOP) {
            if (mLastTimePositionOffset == -1) {
                mLastTimePositionOffset = positionOffset;
            } else {
                mIntention = positionOffset - mLastTimePositionOffset >= 0 ? INTENTION_RIGHT : INTENTION_LEFT;
            }
        } else if (mIntention == INTENTION_RIGHT) {//right
            int r2 = Color.red(mColorArray[2]);
            int r1 = Color.red(mColorArray[1]);
            int g2 = Color.green(mColorArray[2]);
            int g1 = Color.green(mColorArray[1]);
            int b2 = Color.blue(mColorArray[2]);
            int b1 = Color.blue(mColorArray[1]);
            int deltaR = r1 - r2;
            int deltaG = g1 - g2;
            int deltaB = b1 - b2;
            int newR = (int) (r1 - deltaR * positionOffset);
            int newG = (int) (g1 - deltaG * positionOffset);
            int newB = (int) (b1 - deltaB * positionOffset);
            int newColor = Color.rgb(newR, newG, newB);
            mBackgroundImage.setBackgroundColor(newColor);
        } else {//left
            int r0 = Color.red(mColorArray[0]);
            int r1 = Color.red(mColorArray[1]);
            int g0 = Color.green(mColorArray[0]);
            int g1 = Color.green(mColorArray[1]);
            int b0 = Color.blue(mColorArray[0]);
            int b1 = Color.blue(mColorArray[1]);
            int deltaR = r1 - r0;
            int deltaG = g1 - g0;
            int deltaB = b1 - b0;
            int newR = (int) (r1 - deltaR * (1 - positionOffset));
            int newG = (int) (g1 - deltaG * (1 - positionOffset));
            int newB = (int) (b1 - deltaB * (1 - positionOffset));
            int newColor = Color.rgb(newR, newG, newB);
            mBackgroundImage.setBackgroundColor(newColor);
        }
    }

    @Override
    public void onPageSelected(int position) {
        getPaletteColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
            View view = mDetailPagerAdapter.getItemView(mViewPager.getCurrentItem());
            if (view == null) {
                return;
            }
            CardView cardView = (CardView) view.findViewById(R.id.card_detail_layout);
            cardView.setCardElevation(0);
            cardView.setRadius(0);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
    }

    //todo 暂时的办法，最终写到数据库中
    private void getPaletteColor(int position) {
        Bitmap bitmap1 = ImageLoaderManager.loadImageSync(mPhotoNoteList.get(position).getSmallPhotoPathWithFile());
        Palette.generateAsync(bitmap1, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (null != swatch) {
                    mColorArray[1] = swatch.getRgb();
                }
            }
        });
        if (position != 0) {
            Bitmap bitmap0 = ImageLoaderManager.loadImageSync(mPhotoNoteList.get(position - 1).getSmallPhotoPathWithFile());
            Palette.generateAsync(bitmap0, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (null != swatch) {
                        mColorArray[0] = swatch.getRgb();
                    }
                }
            });
        }
        if (position != mPhotoNoteList.size() - 1) {
            Bitmap bitmap2 = ImageLoaderManager.loadImageSync(mPhotoNoteList.get(position + 1).getSmallPhotoPathWithFile());
            Palette.generateAsync(bitmap2, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    if (null != swatch) {
                        mColorArray[2] = swatch.getRgb();
                    }
                }
            });
        }

    }

}
