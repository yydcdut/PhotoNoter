package com.yydcdut.note.controller.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
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
import com.yydcdut.note.view.VoiceRippleView;
import com.yydcdut.note.view.fab2.FloatingMenuLayout;
import com.yydcdut.note.view.fab2.snack.SnackHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import us.pinguo.edit.sdk.base.widget.AnimationAdapter;

/**
 * Created by yyd on 15-4-8.
 */
public class EditTextActivity extends BaseActivity implements View.OnClickListener, Handler.Callback,
        KeyBoardResizeFrameLayout.OnkeyboardShowListener, FloatingMenuLayout.OnFloatingActionsMenuUpdateListener {
    private static final String TAG = EditTextActivity.class.getSimpleName();
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
    private VoiceRippleView mVoiceRippleView;
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

    /* 语音听写 */
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private String mContentString = null;

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
     * @param activity
     * @param categoryLabel
     * @param photoNotePosition
     * @param comparator
     */
    public static void startActivityForResult(Activity activity, String categoryLabel, int photoNotePosition, int comparator) {
        Intent intent = new Intent(activity, EditTextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CATEGORY_LABEL, categoryLabel);
        bundle.putInt(Const.PHOTO_POSITION, photoNotePosition);
        bundle.putInt(Const.COMPARATOR_FACTORY, comparator);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_NOTHING);
        activity.overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
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
        mVoiceRippleView = (VoiceRippleView) findViewById(R.id.img_ripple_fab);

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
                ObjectAnimator.ofFloat(mFabMenuLayout, "translationY", contentEditHeight, 0)
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
                if (mIat == null) {
                    // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
                    mIat = SpeechRecognizer.createRecognizer(EditTextActivity.this, mInitListener);
                }
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
                stopVoice();
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
    public void onBackPressed() {
        if (!mIsHiding && System.currentTimeMillis() - mLastTime > 2000) {
            if (mIsVoiceOpen) {
                stopVoice();
                hideVoice();
                return;
            }
            if (mFabMenuLayout.isOpen()) {
                mFabMenuLayout.close();
                return;
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
                            saveText();
                            closeActivityAnimation(true);
                        }
                    }).show(mFabMenuLayout);
            return;
        }
        if (!mIsHiding) {
            mIsHiding = true;
            closeActivityAnimation(false);
        }
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
                ObjectAnimator.ofFloat(mFabMenuLayout, "translationY", 0, contentEditHeight)
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
                                mVoiceRippleView.startAnimation();
                                startVoice();
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
                mVoiceRippleView.pauseAnimation();
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

    int ret = 0; // 函数调用返回值

    private void startVoice() {
        setParam();
        mContentString = mContentEdit.getText().toString();
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            YLog.e(TAG, "听写失败,错误码：" + ret);
        }
    }

    private void stopVoice() {
        if (mIat != null) {
            mIat.stopListening();
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            hideVoice();
            Toast.makeText(EditTextActivity.this, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            hideVoice();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            mContentEdit.setText(mContentString + getResultString(results));
            if (isLast) {
                YLog.i(TAG, "onResult   isLast--->" + isLast);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "返回音频数据：" + data.length + "   volume---->" + volume);
            mVoiceRippleView.setVoice(((float) volume / 50f) / 1.0f);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        private String getResultString(RecognizerResult results) {
            String text = parseIatResult(results.getResultString());

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            return resultBuffer.toString();
        }
    };

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            YLog.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                YLog.d(TAG, "初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "1");
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVoiceRippleView.stopAnimation();
        if (mIat != null) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }
}
