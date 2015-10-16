package com.yydcdut.note.controller.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab.FloatingActionButton;

/**
 * Created by yyd on 15-4-8.
 */
public class EditTextActivity extends BaseActivity implements View.OnClickListener {

    /* Context */
    private Context mContext = EditTextActivity.this;
    /* title是否显示出来? */
    private boolean mIsEditTextShow = true;
    /* Views */
    private Toolbar mToolbar;
    private View mLayoutTitle;
    private EditText mTitleEdit;
    private EditText mContentEdit;
    private FloatingActionButton mFab;
    private RevealView mRevealView;
    private ImageView mMenuArrowImage;
    /* 数据 */
    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;
    /* 标志位，防止多次点击出现bug效果 */
    private boolean mIsHiding = false;

    private byte[] mTag = new byte[0];

    @Override
    public int setContentView() {
        return R.layout.activity_edit;
    }

    /**
     * 启动Activity
     *
     * @param fragment
     * @param categoryLabel
     * @param photoNotePosition
     * @param comparator
     */
    public static void startActivityForResult(Fragment fragment, String categoryLabel, int photoNotePosition, int comparator) {
        Intent intent = new Intent(fragment.getContext(), EditTextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, categoryLabel);
        bundle.putInt(Const.PHOTO_POSITION, photoNotePosition);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, REQUEST_NOTHING);
        fragment.getActivity().overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        String category = bundle.getString(Const.CATEGORY_LABEL);
        mPosition = bundle.getInt(Const.PHOTO_POSITION);
        mComparator = bundle.getInt(Const.COMPARATOR_FACTORY);
        mPhotoNote = PhotoNoteDBModel.getInstance().findByCategoryLabel(category, mComparator).get(mPosition);
    }

    @Override
    public void initUiAndListener() {
        getBundle();
        initToolBarUI();
        initToolBarItem();
        initEditText();
        initFloatingButton();
        initListener();
        initRevealView();
        initData();
    }

    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
    }

    private void initToolBarItem() {
        int actionbarHeight = getActionBarSize();
        float dimen24dip = getResources().getDimension(R.dimen.dimen_24dip);
        int margin = (int) ((actionbarHeight - dimen24dip) / 2);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_toolbar);
        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams((int) dimen24dip, (int) dimen24dip);  //item的宽高
        mp.setMargins(margin, margin, margin, margin);//分别是margin_top那四个属性
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mp);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        mMenuArrowImage = new ImageView(mContext);
        mMenuArrowImage.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        mMenuArrowImage.setLayoutParams(lp);
        mMenuArrowImage.setTag(mTag);

        relativeLayout.addView(mMenuArrowImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityAnimation();
                break;
        }
        return true;
    }

    private void initEditText() {
        mLayoutTitle = findViewById(R.id.layout_edit_title);
        mTitleEdit = (EditText) findViewById(R.id.et_edit_title);
        mContentEdit = (EditText) findViewById(R.id.et_edit_content);
    }

    private void initFloatingButton() {
        mFab = (FloatingActionButton) findViewById(R.id.fab_finish);
    }

    private void initListener() {
        mMenuArrowImage.setOnClickListener(this);
        mFab.setOnClickListener(this);
    }

    private void initRevealView() {
        mRevealView = (RevealView) findViewById(R.id.reveal);
    }

    private void initData() {
        if (null != mPhotoNote) {
            mTitleEdit.setText(mPhotoNote.getTitle());
            mContentEdit.setText(mPhotoNote.getContent());
        }
    }

    @Override
    public void startActivityAnimation() {
        int actionBarHeight = getActionBarSize();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int contentEditHeight = screenHeight - actionBarHeight * 2;
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(mToolbar, "translationY", -actionBarHeight, 0),
                ObjectAnimator.ofFloat(mLayoutTitle, "translationY", -actionBarHeight * 2, 0),
                ObjectAnimator.ofFloat(mContentEdit, "translationY", contentEditHeight, 0),
                ObjectAnimator.ofFloat(mFab, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(mFab, "scaleY", 0f, 1f)
        );
        animation.start();
    }


    private void openEditTextAnimation() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION);
        animation.playTogether(
                ObjectAnimator.ofFloat(mMenuArrowImage, "rotationX", 180f, 0f),
                ObjectAnimator.ofFloat(mMenuArrowImage, "rotationY", 180f, 0f),
                ObjectAnimator.ofFloat(mTitleEdit, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(mLayoutTitle, "Y", 0f, getActionBarSize())

        );
        animation.start();
    }

    private void closeEditTextAnimation() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION);
        animation.playTogether(
                ObjectAnimator.ofFloat(mMenuArrowImage, "rotationX", 0f, 180f),
                ObjectAnimator.ofFloat(mMenuArrowImage, "rotationY", 0f, 180f),
                ObjectAnimator.ofFloat(mTitleEdit, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(mLayoutTitle, "Y", getActionBarSize(), 0f)
        );
        animation.addListener(mAnimatorListenr);
        animation.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof byte[]) {
            if (mIsEditTextShow) {
                mIsEditTextShow = false;
                closeEditTextAnimation();
            } else {
                mIsEditTextShow = true;
                openEditTextAnimation();
                mLayoutTitle.setVisibility(View.VISIBLE);
            }
            return;
        }
        switch (v.getId()) {
            case R.id.fab_finish:
                saveText();
                showRevealColorViewAndcloseActivity();
                break;
        }
    }

    /**
     * title的隐藏动画
     */
    private Animator.AnimatorListener mAnimatorListenr = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mLayoutTitle.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mLayoutTitle.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mIsHiding) {
            mIsHiding = true;
            closeActivityAnimation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 非保存关闭
     */
    private void closeActivityAnimation() {
        int actionBarHeight = getActionBarSize();
        int screenHeight = Evi.sScreenHeight;
        int contentEditHeight = screenHeight - actionBarHeight * 2;
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(mToolbar, "translationY", 0, -actionBarHeight),
                ObjectAnimator.ofFloat(mLayoutTitle, "translationY", 0, -actionBarHeight * 2),
                ObjectAnimator.ofFloat(mContentEdit, "translationY", 0, contentEditHeight),
                ObjectAnimator.ofFloat(mFab, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(mFab, "scaleY", 1f, 0f)
        );
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsHiding = false;
                setResult(RESULT_NOTHING);
                EditTextActivity.this.finish();
                overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }

    /**
     * 打开RevealColorView并且关闭activity
     */
    private void showRevealColorViewAndcloseActivity() {
        final Point p = getLocationInView(mRevealView, mFab);
        mRevealView.reveal(p.x, p.y, getThemeColor(), mFab.getHeight() / 2, Const.DURATION, new RevealView.RevealAnimationListener() {

            @Override
            public void finish() {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt(Const.PHOTO_POSITION, mPosition);
                bundle.putString(Const.CATEGORY_LABEL, mPhotoNote.getCategoryLabel());
                bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
                intent.putExtras(bundle);
                setResult(RESULT_DATA, intent);
                EditTextActivity.this.finish();
                overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            }
        });
    }

    /**
     * 保存数据
     */
    private void saveText() {
        mPhotoNote.setTitle(mTitleEdit.getText().toString());
        mPhotoNote.setContent(mContentEdit.getText().toString());
        mPhotoNote.setEditedNoteTime(System.currentTimeMillis());
        PhotoNoteDBModel.getInstance().update(mPhotoNote);
    }

}
