package com.yydcdut.note.controller.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.yydcdut.note.view.RevealView;
import com.yydcdut.note.view.fab.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by yyd on 15-4-8.
 */
public class EditTextActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
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
    /* Progress Bar */
    private CircleProgressBarLayout mProgressLayout;
    /* 数据 */
    private PhotoNote mPhotoNote;
    private int mPosition;
    private int mComparator;
    /* 标志位，防止多次点击出现bug效果 */
    private boolean mIsHiding = false;
    private Handler mHandler;

    private static final String TAG_ARROW = "tag_arrow";
    private static final String TAG_UPDATE = "tag_update";

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
        initFloatingButton();
        initRevealView();
        initData();
        mProgressLayout = (CircleProgressBarLayout) findViewById(R.id.layout_progress);
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_toolbar);

        ViewGroup.MarginLayoutParams mp2 = new ViewGroup.MarginLayoutParams((int) dimen24dip, (int) dimen24dip);  //item的宽高
        mp2.setMargins(margin, margin, 0, margin);//分别是margin_top那四个属性
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mp2);
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.ic_backup_white_24dp);
        imageView.setLayoutParams(lp2);
        imageView.setTag(TAG_UPDATE);
        imageView.setOnClickListener(this);

        linearLayout.addView(imageView);

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
                case TAG_UPDATE:
                    if (UserCenter.getInstance().isLoginEvernote()) {
                        mProgressLayout.show();
                        NoteApplication.getInstance().getExecutorPool().submit(new Runnable() {
                            @Override
                            public void run() {
                                update2Evernote();
                                mHandler.sendEmptyMessage(1);
                            }
                        });
                    }
                    break;
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

    private void update2Evernote() {
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
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (EDAMUserException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "EDAMUserException--->" + e.getMessage());
        } catch (EDAMSystemException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "EDAMSystemException--->" + e.getMessage());
        } catch (TException e) {
            e.printStackTrace();
            YLog.i("yuyidong", "TException--->" + e.getMessage());
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

    @Override
    public boolean handleMessage(Message msg) {
        mProgressLayout.hide();
        return false;
    }
}
