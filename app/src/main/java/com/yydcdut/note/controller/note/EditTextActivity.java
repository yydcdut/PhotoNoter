package com.yydcdut.note.controller.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.TException;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.bean.PhotoNote;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.model.PhotoNoteDBModel;
import com.yydcdut.note.model.UserCenter;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.utils.Evi;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.view.CircleProgressBarLayout;
import com.yydcdut.note.view.KeyBoardResizeFrameLayout;
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab2.FloatingMenuLayout;
import com.yydcdut.note.view.fab2.snack.SnackHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import us.pinguo.edit.sdk.base.widget.AnimationAdapter;

/**
 * Created by yyd on 15-4-8.
 */
public class EditTextActivity extends BaseActivity implements View.OnClickListener, Handler.Callback,
        KeyBoardResizeFrameLayout.OnkeyboardShowListener, FloatingMenuLayout.OnFloatingActionsMenuUpdateListener {
    /* Context */
    private Context mContext = EditTextActivity.this;
    /* title是否显示出来? */
    private boolean mIsEditTextShow = true;
    /* Voice的是不是显示出来的 */
    private boolean mIsVoiceOpen = false;
    /* Views */
    private Toolbar mToolbar;
    private View mLayoutTitle;
    private EditText mTitleEdit;
    private EditText mContentEdit;
    private FloatingMenuLayout mFabMenuLayout;
    private ImageView mMenuArrowImage;
    /* Fab */
    private ImageView mVoiceRippleView;
    private View mVoiceFabLayout;
    private View mVoiceTextView;
    private View mVoiceLayout;
    /* Progress Bar */
    private CircleProgressBarLayout mProgressLayout;
    /* RevealView */
    private RevealView mFabRevealView;
    private RevealView mVoiceRevealView;
    private View mFabPositionView;
    /* 数据 */
    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;
    /* 标志位，防止多次点击出现bug效果 */
    private boolean mIsHiding = false;
    private Handler mHandler;
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_NOT_SUCCESS = 2;
    /* 软键盘是否打开 */
    private long mLastTime = 0l;

    private static final String TAG_ARROW = "tag_arrow";

    @Override
    public boolean setStatusBar() {
        return true;
    }

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
        initFloating();
        initData();
        initOtherUI();
    }

    void initOtherUI() {
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
        ((KeyBoardResizeFrameLayout) findViewById(R.id.layout_root)).setOnKeyboardShowListener(this);
        mFabRevealView = (RevealView) findViewById(R.id.reveal_fab);
        mVoiceRevealView = (RevealView) findViewById(R.id.reveal_voice);
        mFabPositionView = findViewById(R.id.view_fab_location);
        mFabRevealView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFabMenuLayout.close();
                return true;
            }
        });
        mVoiceTextView = findViewById(R.id.txt_voice);
        mVoiceLayout = findViewById(R.id.layout_voice);
        mVoiceLayout.setVisibility(View.INVISIBLE);
        mVoiceLayout.setOnClickListener(null);
        mVoiceRippleView = (ImageView) findViewById(R.id.img_ripple_fab);

        mVoiceFabLayout = findViewById(R.id.layout_fab_voice_start);
    }

    private void initToolBarUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_check_white_24dp);
    }

    private void initToolBarItem() {
        int actionbarHeight = getActionBarSize();
        float dimen24dip = getResources().getDimension(R.dimen.dimen_24dip);
        int margin = (int) ((actionbarHeight - dimen24dip) / 2);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_toolbar);

        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams((int) dimen24dip, (int) dimen24dip);  //item的宽高
        mp.setMargins(margin, margin, margin, margin);//分别是margin_top那四个属性
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mp);

        mMenuArrowImage = new ImageView(mContext);
        mMenuArrowImage.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        mMenuArrowImage.setLayoutParams(lp);
        mMenuArrowImage.setTag(TAG_ARROW);
        mMenuArrowImage.setOnClickListener(this);

        linearLayout.addView(mMenuArrowImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveText();
                closeActivityAnimation(true);
                break;
        }
        return true;
    }

    private void initEditText() {
        mLayoutTitle = findViewById(R.id.layout_edit_title);
        mTitleEdit = (EditText) findViewById(R.id.et_edit_title);
        mContentEdit = (EditText) findViewById(R.id.et_edit_content);
    }

    private void initFloating() {
        mFabMenuLayout = (FloatingMenuLayout) findViewById(R.id.layout_fab_edittext);
        mFabMenuLayout.setOnFloatingActionsMenuUpdateListener(this);
        findViewById(R.id.fab_evernote_update).setOnClickListener(this);
        findViewById(R.id.fab_voice).setOnClickListener(this);
        findViewById(R.id.fab_voice_start).setOnClickListener(this);
    }

    private void initData() {
        if (null != mPhotoNote) {
            mTitleEdit.setText(mPhotoNote.getTitle());
            mContentEdit.setText(mPhotoNote.getContent());
        }
        mHandler = new Handler(this);
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
                ObjectAnimator.ofFloat(mFabMenuLayout, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(mFabMenuLayout, "scaleY", 0f, 1f)
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
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLayoutTitle.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLayoutTitle.setVisibility(View.GONE);
            }
        });
        animation.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof String) {
            switch ((String) v.getTag()) {
                case TAG_ARROW:
                    if (mIsEditTextShow) {
                        mIsEditTextShow = false;
                        closeEditTextAnimation();
                    } else {
                        mIsEditTextShow = true;
                        openEditTextAnimation();
                        mLayoutTitle.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return;
        }
        switch (v.getId()) {
            case R.id.fab_voice:
                mFabMenuLayout.close();
                revealVoice();
                break;
            case R.id.fab_evernote_update:
                mFabMenuLayout.close();
                if (UserCenter.getInstance().isLoginEvernote()) {
                    mProgressLayout.show();
                    NoteApplication.getInstance().getExecutorPool().submit(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSuccess = update2Evernote();
                            mHandler.sendEmptyMessage(isSuccess ? MSG_SUCCESS : MSG_NOT_SUCCESS);
                        }
                    });
                } else {
                    SnackHelper.make(mFabMenuLayout, getResources().getString(R.string.not_login), SnackHelper.LENGTH_SHORT)
                            .show(mFabMenuLayout);
                }
                break;
            case R.id.fab_voice_start:
                hideVoice();
                break;
        }
    }

    /**
     * 上传到Evernote
     *
     * @return
     */
    private boolean update2Evernote() {
        boolean isSuccess = true;
        try {
            EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
            List<Notebook> notebookList = noteStoreClient.listNotebooks();
            boolean hasNoteBook = false;
            Notebook appNoteBook = null;
            for (Notebook notebook : notebookList) {
                if (notebook.getName().equals(getResources().getString(R.string.app_name))) {
                    hasNoteBook = true;
                    appNoteBook = notebook;
                }
            }
            if (!hasNoteBook) {
                Notebook notebook = new Notebook();
                notebook.setName(getResources().getString(R.string.app_name));
                appNoteBook = noteStoreClient.createNotebook(notebook);
            }

            Note note = new Note();
            note.setTitle(mTitleEdit.getText().toString());
            if (appNoteBook != null) {
                note.setNotebookGuid(appNoteBook.getGuid());
            }
            InputStream in = null;
            try {
                // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
                in = new BufferedInputStream(new FileInputStream(mPhotoNote.getBigPhotoPathWithoutFile()));
                FileData data = null;
                data = new FileData(EvernoteUtil.hash(in), new File(mPhotoNote.getBigPhotoPathWithoutFile()));
                ResourceAttributes attributes = new ResourceAttributes();
                attributes.setFileName(mPhotoNote.getPhotoName());

                // Create a new Resource
                Resource resource = new Resource();
                resource.setData(data);
                resource.setMime("image/jpeg");
                resource.setAttributes(attributes);

                note.addToResources(resource);

                // Set the note's ENML content
                String content = EvernoteUtil.NOTE_PREFIX
                        + mContentEdit.getText().toString()
                        + EvernoteUtil.createEnMediaTag(resource)
                        + EvernoteUtil.NOTE_SUFFIX;

                note.setContent(content);
                try {
                    noteStoreClient.createNote(note);
                } catch (EDAMNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
                YLog.i("yuyidong", "IOException--->" + e.getMessage());
                isSuccess = false;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                }
            }
        } catch (EDAMUserException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "EDAMUserException--->" + e.getMessage());
            isSuccess = false;
        } catch (EDAMSystemException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "EDAMSystemException--->" + e.getMessage());
            isSuccess = false;
        } catch (TException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "TException--->" + e.getMessage());
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mIsHiding && System.currentTimeMillis() - mLastTime > 2000) {
            if (mIsVoiceOpen) {
                hideVoice();
                return true;
            }
            if (mFabMenuLayout.isOpen()) {
                mFabMenuLayout.close();
                return true;
            }
            mLastTime = System.currentTimeMillis();
            mFabMenuLayout.setMenuClickable(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFabMenuLayout.setMenuClickable(true);
                }
            }, 2000);
            SnackHelper.make(mFabMenuLayout, getResources().getString(R.string.toast_exit), SnackHelper.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.toast_save), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mIsHiding = true;
                            closeActivityAnimation(true);
                        }
                    }).show(mFabMenuLayout);
            return true;
        }
        if (!mIsHiding) {
            mIsHiding = true;
            closeActivityAnimation(false);
        }
        return true;
    }

    /**
     * 关闭动画
     */
    private void closeActivityAnimation(final boolean save) {
        int actionBarHeight = getActionBarSize();
        int screenHeight = Evi.sScreenHeight;
        int contentEditHeight = screenHeight - actionBarHeight * 2;
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION_ACTIVITY);
        animation.playTogether(
                ObjectAnimator.ofFloat(mToolbar, "translationY", 0, -actionBarHeight),
                ObjectAnimator.ofFloat(mLayoutTitle, "translationY", 0, -actionBarHeight * 2),
                ObjectAnimator.ofFloat(mContentEdit, "translationY", 0, contentEditHeight),
                ObjectAnimator.ofFloat(mFabMenuLayout, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(mFabMenuLayout, "scaleY", 1f, 0f)
        );
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsHiding = false;
                if (!save) {
                    setResult(RESULT_NOTHING);
                    EditTextActivity.this.finish();
                    overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.PHOTO_POSITION, mPosition);
                    bundle.putString(Const.CATEGORY_LABEL, mPhotoNote.getCategoryLabel());
                    bundle.putInt(Const.COMPARATOR_FACTORY, mComparator);
                    intent.putExtras(bundle);
                    setResult(RESULT_DATA, intent);
                    EditTextActivity.this.finish();
                }
            }
        });
        animation.start();
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_NOT_SUCCESS:
                SnackHelper.make(mFabMenuLayout, getResources().getString(R.string.toast_fail), SnackHelper.LENGTH_SHORT)
                        .show(mFabMenuLayout);
                break;
            case MSG_SUCCESS:
                SnackHelper.make(mFabMenuLayout, getResources().getString(R.string.toast_success), SnackHelper.LENGTH_SHORT)
                        .show(mFabMenuLayout);
                break;
        }
        mProgressLayout.hide();
        return false;
    }

    @Override
    public void onKeyboardShow() {
        mFabMenuLayout.close();
        mFabMenuLayout.setMenuClickable(false);
    }

    @Override
    public void onKeyboardHide() {
        mFabMenuLayout.setMenuClickable(true);
    }

    @Override
    public void onMenuExpanded() {
        Point p = getLocationInView(mFabRevealView, mFabPositionView);
        mFabRevealView.reveal(p.x, p.y, getResources().getColor(R.color.fab_reveal_black), Const.RADIUS, Const.DURATION, null);
    }

    @Override
    public void onMenuCollapsed() {
        Point p = getLocationInView(mFabRevealView, mFabPositionView);
        mFabRevealView.hide(p.x, p.y, Color.TRANSPARENT, 0, Const.DURATION, null);
    }

    private void revealVoice() {
        mVoiceLayout.setVisibility(View.VISIBLE);
        mVoiceLayout.setOnClickListener(this);
        Point p = getLocationInView(mVoiceRevealView, mFabPositionView);
        mVoiceRevealView.reveal(p.x, p.y, getResources().getColor(R.color.bg_background),
                1, Const.DURATION, new RevealView.RevealAnimationListener() {
                    @Override
                    public void finish() {
                        mVoiceFabLayout.setVisibility(View.VISIBLE);
                        mVoiceTextView.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(EditTextActivity.this, R.anim.anim_scale_small_2_big);
                        animation.setDuration(300l);
                        animation.setAnimationListener(new AnimationAdapter() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                super.onAnimationEnd(animation);
                                mVoiceRippleView.startAnimation(AnimationUtils.loadAnimation(EditTextActivity.this, R.anim.anim_scale_voice_fab));
                            }
                        });
                        mVoiceFabLayout.startAnimation(animation);
                        mVoiceTextView.setAnimation(AnimationUtils.loadAnimation(EditTextActivity.this, R.anim.anim_alpha_in));
                        mIsVoiceOpen = true;
                    }
                });
    }

    private void hideVoice() {
        mIsVoiceOpen = false;
        Animation alphaAnimation = AnimationUtils.loadAnimation(EditTextActivity.this, R.anim.anim_alpha_out);
        mVoiceTextView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mVoiceFabLayout.startAnimation(AnimationUtils.loadAnimation(EditTextActivity.this, R.anim.anim_scale_big_2_small));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mVoiceFabLayout.setVisibility(View.GONE);
                mVoiceTextView.setVisibility(View.GONE);
                Point p = getLocationInView(mVoiceRevealView, mFabPositionView);
                mVoiceRevealView.hide(p.x, p.y, Color.TRANSPARENT, 0, Const.DURATION, new RevealView.RevealAnimationListener() {
                    @Override
                    public void finish() {
                        mVoiceLayout.setOnClickListener(null);
                        mVoiceLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
