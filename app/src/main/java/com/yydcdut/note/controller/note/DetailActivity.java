package com.yydcdut.note.controller.note;

import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.adapter.DetailPagerAdapter;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.FilePathUtils;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.FontTextView;
import com.yydcdut.note.view.ObservableScrollView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        ViewPager.PageTransformer, ObservableScrollView.OnScrollChangedListener, View.OnClickListener {
    private static final float MIN_SCALE = 0.75f;

    private int mComparator;
    private String mCategoryLabel;
    private List<PhotoNote> mPhotoNoteList;

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
    private float mTimeBeginHeight = 0;
    private float mExifBeginHeight = 0;

    private ViewPager mViewPager;
    private DetailPagerAdapter mDetailPagerAdapter;

    private ObservableScrollView mScrollView;
    private FloatingActionButton mFab;

    /* Content TextView */
    private FontTextView mTitleView;
    private FontTextView mContentView;
    private TextView mCreateView;
    private TextView mEditView;
    private TextView mExifView;
    private View mContentLayoutView;

    /* Title TextView */
    private TextView[] mTextViews;
    private View[] mSeparateView;

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
    }

    private void calculateHeight() {
        View view1 = findViewById(R.id.txt_detail_content_title);
        View view2 = findViewById(R.id.txt_detail_content);
        mNoteBeginHeight = 0;
        int noteHeight = view1.getHeight() + view2.getHeight();
        View view3 = findViewById(R.id.layout_detail_time);
        mTimeBeginHeight = mNoteBeginHeight + noteHeight + getResources().getDimension(R.dimen.activity_horizontal_margin) / 2;
        int timeHeight = view3.getHeight();
        View view4 = findViewById(R.id.txt_detail_exif);
        mExifBeginHeight = mTimeBeginHeight + timeHeight + getResources().getDimension(R.dimen.activity_horizontal_margin);
        int exifHeight = view4.getHeight();
    }

    @Override
    public void initUiAndListener() {
        Bundle bundle = getIntent().getExtras();
        mPhotoNoteList = PhotoNoteDBModel.getInstance().findByCategoryLabel(bundle.getString(Const.CATEGORY_LABEL),
                bundle.getInt(Const.COMPARATOR_FACTORY));
        initToolBar();
        initViewPager(bundle);
        initTitleView();
        initContentView();
        initListner();
        setData(mViewPager.getCurrentItem());
        mFab = (FloatingActionButton) findViewById(R.id.fab_edit);
    }

    private void initListner() {
        mScrollView.setOnScrollChangedListener(this);
        for (TextView textView : mTextViews) {
            textView.setOnClickListener(this);
        }
    }

    private void initViewPager(Bundle bundle) {
        mViewPager = (ViewPager) findViewById(R.id.vp_detail);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, this);
        mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
        mCategoryLabel = bundle.getString(Const.CATEGORY_LABEL);
        mDetailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), mCategoryLabel, mComparator);
        mViewPager.setAdapter(mDetailPagerAdapter);
        mViewPager.setCurrentItem(bundle.getInt(Const.PHOTO_POSITION));
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    private void initTitleView() {
        mTextViews = new TextView[4];
        TextView textView1 = (TextView) findViewById(R.id.txt_detail_1);
        TextView textView2 = (TextView) findViewById(R.id.txt_detail_2);
        TextView textView3 = (TextView) findViewById(R.id.txt_detail_3);
        TextView textView4 = (TextView) findViewById(R.id.txt_detail_4);
        mTextViews[0] = textView1;
        mTextViews[1] = textView2;
        mTextViews[2] = textView3;
        mTextViews[3] = textView4;
        mSeparateView = new View[4];
        mSeparateView[0] = findViewById(R.id.view_detail_1);
        mSeparateView[1] = findViewById(R.id.view_detail_2);
        mSeparateView[2] = findViewById(R.id.view_detail_3);
        mSeparateView[3] = findViewById(R.id.view_detail_4);
    }

    private void initContentView() {
        mTitleView = (FontTextView) findViewById(R.id.txt_detail_content_title);
        mContentView = (FontTextView) findViewById(R.id.txt_detail_content);
        mCreateView = (TextView) findViewById(R.id.txt_detail_create_time);
        mEditView = (TextView) findViewById(R.id.txt_detail_edit_time);
        mExifView = (TextView) findViewById(R.id.txt_detail_exif);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_detail);
        mContentLayoutView = findViewById(R.id.layout_detail_add);
    }

    private void setData(int index) {
        PhotoNote photoNote = mPhotoNoteList.get(index);
        /* 设置文字 */
        mTitleView.setText(photoNote.getTitle());
        mContentView.setText(photoNote.getContent());
        mCreateView.setText(decodeTimeInTextDetail(photoNote.getCreatedNoteTime()));
        mEditView.setText(decodeTimeInTextDetail(photoNote.getEditedNoteTime()));
        try {
            mExifView.setText(getExif(photoNote));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getExif(PhotoNote photoNote) throws IOException {
        String enter = "\n";
        StringBuilder sb = new StringBuilder();
        ExifInterface exifInterface = new ExifInterface(photoNote.getBigPhotoPathWithoutFile());
        String fDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        sb.append(getResources().getString(R.string.detail_dateTime))
                .append(getExifData(fDateTime))
                .append(enter);
        String fOrientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        sb.append(getResources().getString(R.string.detail_orientation))
                .append(fOrientation)
                .append(enter);
        String fFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        sb.append(getResources().getString(R.string.detail_flash))
                .append(fFlash.equals("0") ? getResources().getString(R.string.detail_flash_close) : getResources().getString(R.string.detail_flash_open))
                .append(enter);
        String fImageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        if (fImageWidth.equals("0")) {
            int[] size = FilePathUtils.getPictureSize(photoNote.getBigPhotoPathWithoutFile());
            sb.append(getResources().getString(R.string.detail_image_width))
                    .append(size[0])
                    .append(enter);
            sb.append(getResources().getString(R.string.detail_image_length))
                    .append(size[1])
                    .append(enter);
        } else {
            sb.append(getResources().getString(R.string.detail_image_width))
                    .append(fImageWidth)
                    .append(enter);
            String fImageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            sb.append(getResources().getString(R.string.detail_image_length))
                    .append(fImageLength)
                    .append(enter);
        }
        String fWhiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        sb.append(getResources().getString(R.string.detail_white_balance))
                .append(fWhiteBalance.equals("0") ? getResources().getString(R.string.detail_wb_auto) : getResources().getString(R.string.detail_wb_manual))
                .append(enter);
        String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if (longitude == null || longitude.equals("null")) {
            sb.append(getResources().getString(R.string.detail_longitude))
                    .append(getResources().getString(R.string.detail_unknown))
                    .append(enter);
        } else {
            String[] longitudeSs = longitude.split(",");
            double longitudesD = 0;
            longitudesD += Double.parseDouble(longitudeSs[0].split("/")[0]);
            longitudesD += (((int) (Double.parseDouble(longitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(longitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;
            sb.append(getResources().getString(R.string.detail_longitude))
                    .append(longitudesD + "")
                    .append(enter);
        }
        String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        if (latitude == null || latitude.equals("null")) {
            sb.append(getResources().getString(R.string.detail_latitude))
                    .append(getResources().getString(R.string.detail_unknown))
                    .append(enter);
        } else {
            String[] latitudeSs = latitude.split(",");
            double latitudesD = 0;
            latitudesD += Double.parseDouble(latitudeSs[0].split("/")[0]);
            latitudesD += (((int) (Double.parseDouble(latitudeSs[1].split("/")[0]) * 100)) + Double.parseDouble(latitudeSs[2].split("/")[0]) / 60 / 10000) / 60 / 100;
            sb.append(getResources().getString(R.string.detail_latitude))
                    .append(latitudesD + "")
                    .append(enter);
        }
        String fMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        sb.append(getResources().getString(R.string.detail_make))
                .append(getExifData(fMake))
                .append(enter);
        String fModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        sb.append(getResources().getString(R.string.detail_model))
                .append(getExifData(fModel))
                .append(enter);
        String fAperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
        sb.append(getResources().getString(R.string.detail_aperture)).append(getExifData(fAperture)).append(enter);
        String fExposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        sb.append(getResources().getString(R.string.detail_exposure_time))
                .append(getExifData(fExposureTime))
                .append(enter);
        String fFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        sb.append(getResources().getString(R.string.detail_focal_length))
                .append(getExifData(fFocalLength))
                .append(enter);
        String fISOSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        sb.append(getResources().getString(R.string.detail_iso))
                .append(getExifData(fISOSpeedRatings))
                .append(enter);
        return sb.toString();
    }

    private String getExifData(String data) {
        if (TextUtils.isEmpty(data) || data.equals("null")) {
            return getResources().getString(R.string.detail_unknown);
        } else {
            return data;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIntention == INTENTION_STOP) {
            if (mLastTimePositionOffset == -1) {
                mLastTimePositionOffset = positionOffset;
            } else {
                mIntention = positionOffset - mLastTimePositionOffset >= 0 ? INTENTION_RIGHT : INTENTION_LEFT;
            }
        } else if (mIntention == INTENTION_RIGHT && positionOffset < 0.99) {//right
            //positionOffset从0到1
            if (position + 1 >= mPhotoNoteList.size()) {
                return;
            }
            calculateHeight();
            if (positionOffset > 0.5) {
                float alpha = (positionOffset - 0.5f) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_RIGHT_OUT) {
                    return;
                }
                setData(position + 1);
                mIntentionState = STATE_RIGHT_OUT;
                mScrollView.scrollTo(0, 0);
                resetTitlePostion();
            } else {
                float alpha = (0.5f - positionOffset) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_RIGHT_IN) {
                    return;
                }
                setData(position);
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
                setData(position + 1);
                mIntentionState = STATE_LEFT_OUT;
            } else {
                float alpha = (0.5f - positionOffset) / 0.5f;
                setContentAlpha(alpha);
                if (mIntentionState == STATE_LEFT_IN) {
                    return;
                }
                setData(position);
                mIntentionState = STATE_LEFT_IN;
                mScrollView.scrollTo(0, 0);
                resetTitlePostion();
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
        mContentLayoutView.setAlpha(alpha);
    }

    private void resetTitlePostion() {
        setTitlePosition(R.id.txt_detail_1);
    }

    private void setTitlePosition(int viewId) {
        for (TextView textView : mTextViews) {
            textView.setTextColor(getResources().getColor(R.color.txt_LightSlateGray));
        }
        for (View view : mSeparateView) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        switch (viewId) {
            case R.id.txt_detail_1:
                mTextViews[0].setTextColor(getResources().getColor(R.color.gray));
                mSeparateView[0].setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_2:
                mTextViews[1].setTextColor(getResources().getColor(R.color.gray));
                mSeparateView[1].setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_3:
                mTextViews[2].setTextColor(getResources().getColor(R.color.gray));
                mSeparateView[2].setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
            case R.id.txt_detail_4:
                mTextViews[3].setTextColor(getResources().getColor(R.color.gray));
                mSeparateView[3].setBackgroundColor(getResources().getColor(R.color.white_smoke));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIntention = INTENTION_STOP;
            mLastTimePositionOffset = -1;
            mIntentionState = STATE_NOTHING;
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
        if (y < (int) mTimeBeginHeight) {
            setTitlePosition(R.id.txt_detail_1);
        } else if (y <= (int) mExifBeginHeight) {
            setTitlePosition(R.id.txt_detail_3);
        } else {
            setTitlePosition(R.id.txt_detail_4);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_detail_1:
                mScrollView.smoothScrollTo(0, 0);
                break;
            case R.id.txt_detail_2:
                mScrollView.smoothScrollTo(0, 0);
                break;
            case R.id.txt_detail_3:
                mScrollView.smoothScrollTo(0, (int) mTimeBeginHeight + 2);
                break;
            case R.id.txt_detail_4:
                mScrollView.smoothScrollTo(0, (int) mExifBeginHeight + 2);
                break;
        }
    }

    private String decodeTimeInTextDetail(long time) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        sb.append(getChineseMonth(Integer.parseInt(sdfMonth.format(time))));
        SimpleDateFormat sdfDayAndYear = new SimpleDateFormat(" dd. yyyy ");
        sb.append(sdfDayAndYear.format(time));
        sb.append("at");
        SimpleDateFormat sdfHour = new SimpleDateFormat(" hh:mm:ss a");
        sb.append(sdfHour.format(time));
        return sb.toString();
    }

    /**
     * 月份的转化
     * todo  写到xml中
     *
     * @param month
     * @return
     */
    private String getChineseMonth(int month) {
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
            default:
                return "bug!";
        }
    }
}
