package com.yydcdut.note.controller.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.listener.ObservableScrollViewCallbacks;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.ImageManager.ImageLoadingListenerAdapter;
import com.yydcdut.note.utils.ScrollUtils;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.view.FontTextView;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.scroll.ObservableScrollView;
import com.yydcdut.note.view.scroll.ScrollState;

/**
 * Created by yyd on 15-3-29.
 */
public class DetailTextFragment extends BaseFragment implements ObservableScrollViewCallbacks, View.OnClickListener {
    private static final String TAG = DetailTextFragment.class.getSimpleName();
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = false;

    private Toolbar mToolbar;
    private ImageView mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mToolBarTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;

    private RevealView mRevealView;

    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;

    private FontTextView mTitleView;
    private FontTextView mContentView;
    private TextView mCreateView;
    private TextView mEditView;


    public static DetailTextFragment newInstance() {
        return new DetailTextFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void initSetting() {
    }

    @Override
    public void getBundle(Bundle bundle) {
        mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
        mPosition = bundle.getInt(Const.PHOTO_POSITION);
        String category = bundle.getString(Const.CATEGORY_LABEL);
        mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_detail_txt, null);
    }

    @Override
    public void initUI(View view) {
        initToolBar();
        initTitle(view);
        initImage(view);
        initScrollView(view);
        initFabButton(view);
        initContentUI(view);
        initRevealColorUI();
    }

    @Override
    public void initListener(View v) {
        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });
        v.findViewById(R.id.view_trans).setOnClickListener(this);
        mFab.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mToolBarTitleView.setText("");
        getActivity().setTitle(null);
        initSize();
        mTitleView.setText(mPhotoNote.getTitle());
        mContentView.setText(mPhotoNote.getContent());
        mCreateView.setText(Utils.decodeTimeInTextDetail(mPhotoNote.getCreatedNoteTime()));
        mEditView.setText(Utils.decodeTimeInTextDetail(mPhotoNote.getEditedNoteTime()));
    }

    @Override
    public void saveSettingWhenPausing() {

    }

    /**
     * RevealColor初始化
     */
    private void initRevealColorUI() {
        mRevealView = (RevealView) getActivity().findViewById(R.id.reveal);
    }

    /**
     * 初始化content内容的UI
     *
     * @param v
     */
    private void initContentUI(View v) {
        mTitleView = (FontTextView) v.findViewById(R.id.txt_detail_content_title);
        mContentView = (FontTextView) v.findViewById(R.id.txt_detail_content);
        mCreateView = (TextView) v.findViewById(R.id.txt_detail_create_time);
        mEditView = (TextView) v.findViewById(R.id.txt_detail_edit_time);
    }

    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mToolbarColor = mToolbar.getSolidColor();
    }

    /**
     * 最上面显示的imageview和滑动的时候逐渐显示的蓝色的overlayView
     *
     * @param v
     */
    private void initImage(View v) {
        mImageView = (ImageView) v.findViewById(R.id.img_detail);
        mImageView.setVisibility(View.INVISIBLE);
        //设置图片
        ImageLoaderManager.displayImage(mPhotoNote.getSmallPhotoPathWithFile(), mImageView, new ImageLoadingListenerAdapter() {
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mImageView.setVisibility(View.VISIBLE);
            }
        });
        mOverlayView = v.findViewById(R.id.view_overlay);
    }

    /**
     * 标题
     *
     * @param v
     */
    private void initTitle(View v) {
        mToolBarTitleView = (TextView) v.findViewById(R.id.txt_detail_title);
    }

    /**
     * 计算size
     */
    private void initSize() {
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
    }

    /**
     * 初始化srollview
     *
     * @param v
     */
    private void initScrollView(View v) {
        mScrollView = (ObservableScrollView) v.findViewById(R.id.scroll_detail);
        mScrollView.setScrollViewCallbacks(this);
    }

    /**
     * 初始化FloatingActionButton
     *
     * @param v
     */
    private void initFabButton(View v) {
        mFab = v.findViewById(R.id.fab_detail);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.flexible_margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);
    }

    /**
     * floatingActionButton等往上滑的动画
     *
     * @param scrollY     scroll position in Y axis
     * @param firstScroll true when this is called for the first time in the consecutive motion events
     * @param dragging    true when the view is dragged and false when the view is scrolled in the inertia
     */
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        Log.i("xxx", "scrollY--->" + scrollY + "   firstScroll--->" + firstScroll + "   dragging--->" + dragging);
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
//        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mToolBarTitleView, 0);
        ViewHelper.setPivotY(mToolBarTitleView, 0);
        ViewHelper.setScaleX(mToolBarTitleView, scale);
        ViewHelper.setScaleY(mToolBarTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mToolBarTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mToolBarTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    /**
     * 显示floatingactionbutton的动画
     */
    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(Const.DURATION / 2).start();
            mFabIsShown = true;
        }
    }

    /**
     * 隐藏floatingactionbutton的动画
     */
    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(Const.DURATION / 2).start();
            mFabIsShown = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_trans:
                Intent intent = new Intent(getContext(), ZoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.PHOTO_POSITION, mPosition);
                bundle.putString(Const.CATEGORY_LABEL, mPhotoNote.getCategoryLabel());
                bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
                break;
            case R.id.fab_detail:
                showRevealColorViewAndStartActivity();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_DATA) {
            Bundle bundle = data.getExtras();
            String category = bundle.getString(Const.CATEGORY_LABEL);
            mPosition = bundle.getInt(Const.PHOTO_POSITION);
            mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
            mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
            updateText();
        }
        closeRevealColorView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开RevealColorView并且开启activity
     */
    private void showRevealColorViewAndStartActivity() {
        doIgnoreKeyListener();
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.reveal(p.x, p.y, getThemeColor(), mFab.getHeight() / 2, Const.DURATION, new RevealView.RevealAnimationListener() {

            @Override
            public void finish() {
                Intent intent = new Intent(getContext(), EditTextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.PHOTO_POSITION, mPosition);
                bundle.putString(Const.CATEGORY_LABEL, mPhotoNote.getCategoryLabel());
                bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_NOTHING);
                getActivity().overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
                donotIgnoreKeyListener();
            }
        });
    }

    /**
     * 关闭activity之后的动画或者onActivityResult
     */
    private void closeRevealColorView() {
        doIgnoreKeyListener();
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.hide(p.x, p.y, Color.TRANSPARENT, Const.RADIUS, Const.DURATION, new RevealView.RevealAnimationListener() {
            @Override
            public void finish() {
                donotIgnoreKeyListener();
            }
        });
    }

    private void doIgnoreKeyListener() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }

    private void donotIgnoreKeyListener() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(null);
    }

    /**
     * 更新数据
     */
    private void updateText() {
        mTitleView.setText(mPhotoNote.getTitle());
        mContentView.setText(mPhotoNote.getContent());
        mEditView.setText(Utils.decodeTimeInTextDetail(mPhotoNote.getEditedNoteTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageLoaderManager.displayImage(mPhotoNote.getSmallPhotoPathWithFile(), mImageView);
    }

}
