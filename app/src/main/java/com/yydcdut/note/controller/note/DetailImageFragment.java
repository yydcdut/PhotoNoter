package com.yydcdut.note.controller.note;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseFragment;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.ImageManager.ImageLoaderManager;
import com.yydcdut.note.utils.Utils;
import com.yydcdut.note.view.FontTextView;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab.FloatingActionButton;

/**
 * Created by yyd on 15-3-30.
 */
public class DetailImageFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = DetailImageFragment.class.getSimpleName();
    private FloatingActionButton mFloatingButton;
    private Toolbar mToolbar;
    private boolean mIsFloatingVisible;
    private ImageView mPhotoImage;

    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;

    private RevealView mRevealView;

    private FontTextView mTitleView;
    private FontTextView mContentView;
    private TextView mCreateView;
    private TextView mEditView;

    public static DetailImageFragment newInstance() {
        return new DetailImageFragment();
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
        return inflater.inflate(R.layout.frag_detail_image, null);
    }

    @Override
    public void initUI(View view) {
        initContentUI(view);
        initFloating(view);
        initPhotoImage(view);
        initToolBar();
        initRevealColorUI();
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

    private void initFloating(View view) {
        mFloatingButton = (FloatingActionButton) view.findViewById(R.id.fab_detail);
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mFloatingButton.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mIsFloatingVisible) {
                            return;
                        }
                        try {
                            int spaceImageHeight = getResources().getDimensionPixelSize(R.dimen.space_image_height);
                            mFloatingButton.setTranslationY(spaceImageHeight - mFloatingButton.getHeight() / 2);
                            mFloatingButton.setVisibility(View.VISIBLE);
                            fabStartOutAnimation(mFloatingButton);
                            mIsFloatingVisible = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }
        });
    }

    private void initPhotoImage(View view) {
        mPhotoImage = (ImageView) view.findViewById(R.id.img_detail_image);
    }

    /**
     * RevealColor初始化
     */
    private void initRevealColorUI() {
        mRevealView = (RevealView) getActivity().findViewById(R.id.reveal);
    }

    @Override
    public void initListener(final View view) {
        mFloatingButton.setOnClickListener(this);
        mPhotoImage.setOnClickListener(this);
    }

    @Override
    public void initData() {
        ImageLoaderManager.displayImage(mPhotoNote.getSmallPhotoPathWithFile(), mPhotoImage);
        mTitleView.setText(mPhotoNote.getTitle());
        mContentView.setText(mPhotoNote.getContent());
        mCreateView.setText(Utils.decodeTimeInImageDetail(mPhotoNote.getCreatedNoteTime()));
        mEditView.setText(Utils.decodeTimeInImageDetail(mPhotoNote.getEditedNoteTime()));
    }

    @Override
    public void saveSettingWhenPausing() {

    }

    private void initToolBar() {
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        getActivity().setTitle("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_detail:
                showRevealColorView();
                break;
            case R.id.img_detail_image:
                Intent intent = new Intent(getContext(), ZoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.PHOTO_POSITION, mPosition);
                bundle.putString(Const.CATEGORY_LABEL, mPhotoNote.getCategoryLabel());
                bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
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

    private void fabStartOutAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        );
        animation.start();
    }


    private void showRevealColorView() {
        doIgnoreKeyListener();
        final Point p = getLocationInView(mRevealView, mFloatingButton);
        mRevealView.reveal(p.x, p.y, getThemeColor(), mFloatingButton.getHeight() / 2, Const.DURATION, new RevealView.RevealAnimationListener() {

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

    private void closeRevealColorView() {
        doIgnoreKeyListener();
        final Point p = getLocationInView(mRevealView, mFloatingButton);
        mRevealView.hide(p.x, p.y, Color.TRANSPARENT, Const.RADIUS, Const.DURATION, new RevealView.RevealAnimationListener() {
            @Override
            public void finish() {
                donotIgnoreKeyListener();
            }
        });
    }

    private void doIgnoreKeyListener() {
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return true;
                }
            });
        }
    }

    private void donotIgnoreKeyListener() {
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(null);
        }
    }

    /**
     * 更新数据
     */
    private void updateText() {
        mTitleView.setText(mPhotoNote.getTitle());
        mContentView.setText(mPhotoNote.getContent());
        mEditView.setText(Utils.decodeTimeInImageDetail(mPhotoNote.getEditedNoteTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageLoaderManager.displayImage(mPhotoNote.getSmallPhotoPathWithFile(), mPhotoImage);
    }
}
