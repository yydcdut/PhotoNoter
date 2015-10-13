package com.yydcdut.note.controller.note;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.DetailPagerAdapter;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.DepthPageTransformerAnimation;
import com.yydcdut.note.utils.FastBlur;
import com.yydcdut.note.utils.LocalStorageUtils;
import com.yydcdut.note.utils.Utils;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailActivity extends BaseActivity {

    private DetailPagerAdapter mAdapter;

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
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_detail);
        viewPager.setPageTransformer(true, new DepthPageTransformerAnimation());
        int style = LocalStorageUtils.getInstance().getNoteStyle();
        mAdapter = new DetailPagerAdapter(getSupportFragmentManager(),
                bundle.getString(Const.CATEGORY_LABEL), bundle.getInt(Const.COMPARATOR_FACTORY),
                style);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(bundle.getInt(Const.PHOTO_POSITION));
    }

    private void initBGView() {
        final ImageView imageView = (ImageView) findViewById(R.id.img_detail_bg);

        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                FastBlur.setWallPaper(imageView);
                return true;
            }
        });
    }

}
