package com.yydcdut.note.mvp.v.note.impl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.adapter.DetailPagerAdapter;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.injector.component.DaggerActivityComponent;
import com.yydcdut.note.injector.module.ActivityModule;
import com.yydcdut.note.mvp.p.note.impl.DetailPresenterImpl;
import com.yydcdut.note.mvp.v.BaseActivity;
import com.yydcdut.note.mvp.v.note.IDetailView;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.FontTextView;
import com.yydcdut.note.view.ObservableScrollView;
import com.yydcdut.note.view.RevealView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailActivity extends BaseActivity implements IDetailView,
        ViewPager.PageTransformer, ObservableScrollView.OnScrollChangedListener {
    private static final float MIN_SCALE = 0.75f;

    @Inject
    DetailPresenterImpl mDetailPresenter;

    private boolean mIsIgnoreBackPress = false;

    private static final int INTENTION_LEFT = -1;
    private static final int INTENTION_RIGHT = 1;
    private static final int INTENTION_STOP = 0;
    private int mIntention = INTENTION_STOP;
    private static final int STATE_LEFT_IN = -2;
    private static final int STATE_LEFT_OUT = -1;
    private static final int STATE_NOTHING = 0;
    private static final int STATE_RIGHT_OUT = 1;
    private static final int STATE_RIGHT_IN = 2;
    private int mIntentionState = STATE_NOTHING;
    private float mLastTimePositionOffset = -1;

    private float mNoteBeginHeight = 0;
    private float mMapBeginHeight = 0;
    private float mTimeBeginHeight = 0;
    private float mExifBeginHeight = 0;

    private DetailPagerAdapter mDetailPagerAdapter;
    @Bind(R.id.vp_detail)
    ViewPager mViewPager;
    @Bind(R.id.scroll_detail)
    ObservableScrollView mScrollView;
    @Bind(R.id.fab_edit)
    View mFab;
    @Bind(R.id.reveal)
    RevealView mRevealView;
    @Bind(R.id.txt_detail_content_title)
    FontTextView mTitleView;/* Content TextView */
    @Bind(R.id.txt_detail_content)
    FontTextView mContentView;
    @Bind(R.id.txt_detail_create_time)
    TextView mCreateView;
    @Bind(R.id.txt_detail_edit_time)
    TextView mEditView;
    @Bind(R.id.txt_detail_exif)
    TextView mExifView;
    @Bind(R.id.layout_detail_time)
    View mDetailTimeView;
    @Bind({R.id.view_seperate1, R.id.view_seperate2, R.id.view_seperate3})
    List<View> mContentSeparateViews;
    @Bind({R.id.txt_detail_1, R.id.txt_detail_2, R.id.txt_detail_3, R.id.txt_detail_4})
    List<TextView> mControlTextViews;/* Control View */
    @Bind({R.id.view_detail_1, R.id.view_detail_2, R.id.view_detail_3, R.id.view_detail_4})
    List<View> mSeparateView;
    @Bind(R.id.bmapView)
    MapView mMapView;/* map */

    @Override
    public boolean setStatusBar() {
        return false;
    }

    @Override
    public int setContentView() {
        if (LollipopCompat.AFTER_LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        }
        return R.layout.activity_detail;
    }

    @Override
    public void initInjector() {
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((NoteApplication) getApplication()).getApplicationComponent())
                .build();
        mActivityComponent.inject(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        calculateHeight();
        int fabHeight = mFab.getHeight();
        int translateViewHeight = findViewById(R.id.view_translate).getHeight();
        int moveY = translateViewHeight - fabHeight / 2;
        int moveX = findViewById(R.id.view_translate).getWidth() - mFab.getWidth() -
                (int) getResources().getDimension(R.dimen.dimen_24dip);
        mFab.setX(moveX);
        mFab.setY(moveY);
        mFab.setVisibility(View.VISIBLE);
        int containerHeight = findViewById(R.id.layout_detail_scroll_container).getHeight();
        mMapView.getLayoutParams().height = (int) (containerHeight - getResources().getDimension(R.dimen.detail_control));
    }

    private void calculateHeight() {
        View view1 = findViewById(R.id.txt_detail_content_title);
        View view2 = findViewById(R.id.txt_detail_content);
        mNoteBeginHeight = 0;
        int noteHeight = view1.getHeight() + view2.getHeight();
        mMapBeginHeight = mNoteBeginHeight + noteHeight + getResources().getDimension(R.dimen.activity_horizontal_margin) / 2;
        int containerHeight = findViewById(R.id.layout_detail_scroll_container).getHeight();
        int mapHeight = (int) (containerHeight - getResources().getDimension(R.dimen.detail_control));
        mTimeBeginHeight = mMapBeginHeight + mapHeight + getResources().getDimension(R.dimen.activity_horizontal_margin);
        View view3 = findViewById(R.id.layout_detail_time);
        int timeHeight = view3.getHeight();
        mExifBeginHeight = mTimeBeginHeight + timeHeight + getResources().getDimension(R.dimen.activity_horizontal_margin);
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        mDetailPresenter.bindData(bundle.getString(Const.CATEGORY_LABEL),
                bundle.getInt(Const.PHOTO_POSITION),
                bundle.getInt(Const.COMPARATOR_FACTORY));
        mDetailPresenter.attachView(this);
        initToolBar();
        initMap();
        initListner();
    }

    private void initMap() {
        mMapView.showZoomControls(false);//隐藏缩放控件
    }

    private void initListner() {
        mViewPager.setPageTransformer(true, this);
        mScrollView.setOnScrollChangedListener(this);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (LollipopCompat.AFTER_LOLLIPOP) {
            int size = getStatusBarSize();
            FrameLayout.LayoutParams relativeLayout = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            relativeLayout.setMargins(0, size, 0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @OnPageChange(
            value = R.id.vp_detail,
            callback = OnPageChange.Callback.PAGE_SCROLLED
    )
    public void viewPagerScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIntention == INTENTION_STOP) {
            if (mLastTimePositionOffset == -1) {
                mLastTimePositionOffset = positionOffset;
            } else {
                mIntention = positionOffset - mLastTimePositionOffset >= 0 ? INTENTION_RIGHT : INTENTION_LEFT;
            }
        } else if (mIntention == INTENTION_RIGHT && positionOffset < 0.99) {//right
            //positionOffset从0到1
            if (position + 1 >= mDetailPagerAdapter.getCount()) {
                return;
            }
            calculateHeight();
            if (positionOffset > 0.5) {
                float alpha = (positionOffset - 0.5f) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_RIGHT_OUT) {
                    return;
                }
                mDetailPresenter.showNote(position + 1);
                mIntentionState = STATE_RIGHT_OUT;
                mScrollView.scrollTo(0, 0);
                resetTitlePosition();
            } else {
                float alpha = (0.5f - positionOffset) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_RIGHT_IN) {
                    return;
                }
                mDetailPresenter.showNote(position);
                mIntentionState = STATE_RIGHT_IN;
            }
        } else if (mIntention == INTENTION_LEFT && positionOffset > 0.01) {//left
            //positionOffset从1到0
            if (position < 0) {
                return;
            }
            calculateHeight();
            if (positionOffset > 0.5) {
                float alpha = (positionOffset - 0.5f) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_LEFT_OUT) {
                    return;
                }
                mDetailPresenter.showNote(position + 1);
                mIntentionState = STATE_LEFT_OUT;
            } else {
                float alpha = (0.5f - positionOffset) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_LEFT_IN) {
                    return;
                }
                mDetailPresenter.showNote(position);
                mIntentionState = STATE_LEFT_IN;
                mScrollView.scrollTo(0, 0);
                resetTitlePosition();
            }
        }
        if (positionOffset < 0.01 || positionOffset > 0.99) {
            //重新计算方向
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
            mIntentionState = STATE_NOTHING;
        }
    }

    private void setContentAlpha(float alpha) {
        mTitleView.setAlpha(alpha);
        mContentView.setAlpha(alpha);
        mDetailTimeView.setAlpha(alpha);
        mExifView.setAlpha(alpha);
        for (View view : mContentSeparateViews) {
            view.setAlpha(alpha);
        }
    }

    private void resetTitlePosition() {
        setTitlePosition(R.id.txt_detail_1);
    }

    private void setTitlePosition(int viewId) {
        for (TextView textView : mControlTextViews) {
            textView.setTextColor(getResources().getColor(R.color.txt_LightSlateGray));
        }
        for (View view : mSeparateView) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        switch (viewId) {
            case R.id.txt_detail_1:
                mControlTextViews.get(0).setTextColor(getResources().getColor(R.color.gray));
                mSeparateView.get(0).setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_2:
                mControlTextViews.get(1).setTextColor(getResources().getColor(R.color.gray));
                mSeparateView.get(1).setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_3:
                mControlTextViews.get(2).setTextColor(getResources().getColor(R.color.gray));
                mSeparateView.get(2).setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_4:
                mControlTextViews.get(3).setTextColor(getResources().getColor(R.color.gray));
                mSeparateView.get(3).setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
        }
    }

    @OnPageChange(
            value = R.id.vp_detail,
            callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED
    )
    public void viewPagerScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
            mIntentionState = STATE_NOTHING;
            mMapView.setVisibility(View.VISIBLE);
        } else {
            mMapView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when
            // moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1 - position);
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                    * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (y < (int) mMapBeginHeight) {
            setTitlePosition(R.id.txt_detail_1);
        } else if (y <= (int) mTimeBeginHeight) {
            setTitlePosition(R.id.txt_detail_2);
        } else if (y <= (int) mExifBeginHeight) {
            setTitlePosition(R.id.txt_detail_3);
        } else {
            setTitlePosition(R.id.txt_detail_4);
        }
    }

    @OnClick(R.id.txt_detail_1)
    public void clickTextDetail1(View v) {
        mScrollView.smoothScrollTo(0, 0);
    }

    @OnClick(R.id.txt_detail_2)
    public void clickTextDetail2(View v) {
        mScrollView.smoothScrollTo(0, (int) mMapBeginHeight + 2);
    }

    @OnClick(R.id.txt_detail_3)
    public void clickTextDetail3(View v) {
        mScrollView.smoothScrollTo(0, (int) mTimeBeginHeight + 2);
    }

    @OnClick(R.id.txt_detail_4)
    public void clickTextDetail4(View v) {
        mScrollView.smoothScrollTo(0, (int) mExifBeginHeight + 2);
    }

    @OnClick(R.id.fab_edit)
    public void clickFabEdit(View v) {
        showRevealColorViewAndStartActivity();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_DATA) {
            Bundle bundle = data.getExtras();
            mDetailPresenter.updateNote(bundle.getString(Const.CATEGORY_LABEL),
                    bundle.getInt(Const.PHOTO_POSITION), bundle.getInt(Const.COMPARATOR_FACTORY));
        }
        closeRevealColorView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开RevealColorView并且开启activity
     */
    private void showRevealColorViewAndStartActivity() {
        mIsIgnoreBackPress = true;
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.reveal(p.x, p.y, getThemeColor(), mFab.getHeight() / 2, Const.DURATION, new RevealView.RevealAnimationListener() {

            @Override
            public void finish() {
                mDetailPresenter.jump2EditTextActivity();
                mIsIgnoreBackPress = false;
            }
        });
    }

    /**
     * 关闭activity之后的动画或者onActivityResult
     */
    public void closeRevealColorView() {
        mIsIgnoreBackPress = true;
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.hide(p.x, p.y, Color.TRANSPARENT, Const.RADIUS, Const.DURATION, new RevealView.RevealAnimationListener() {
            @Override
            public void finish() {
                mIsIgnoreBackPress = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mIsIgnoreBackPress) {
            super.onBackPressed();
        }
    }

    @Override
    public void setFontSystem(boolean useSystem) {
        mTitleView.setFontSystem(useSystem);
        mContentView.setFontSystem(useSystem);
    }

    @Override
    public void setViewPagerAdapter(List<PhotoNote> list, int position, int comparator) {
        mDetailPagerAdapter = new DetailPagerAdapter(list, getSupportFragmentManager(), comparator);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void showCurrentPosition(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public BaiduMap getBaiduMap() {
        return mMapView.getMap();
    }

    @Override
    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void showNote(String title, String content, String createdTime, String editedTime) {
        mTitleView.setText(title);
        mContentView.setText(content);
        mCreateView.setText(createdTime);
        mEditView.setText(editedTime);
    }

    @Override
    public void showExif(String exif) {
        mExifView.setText(exif);
    }

    @Override
    public void jump2EditTextActivity(String label, int position, int comparator) {
        EditTextActivity.startActivityForResult(DetailActivity.this, label, position, comparator);
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        mDetailPresenter.detachView();
        super.onDestroy();
    }

}
