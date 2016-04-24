package com.yydcdut.note.presenters.note.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.iflytek.cloud.SpeechUtility;
import com.yydcdut.note.BuildConfig;
import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxPhotoNote;
import com.yydcdut.note.model.rx.RxUser;
import com.yydcdut.note.presenters.note.IEditTextPresenter;
import com.yydcdut.note.utils.PermissionUtils;
import com.yydcdut.note.utils.YLog;
import com.yydcdut.note.utils.permission.Permission;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.note.IEditTextView;
import com.yydcdut.note.widget.fab2.snack.OnSnackBarActionListener;

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

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/15.
 */
public class EditTextPresenterImpl implements IEditTextPresenter, PermissionUtils.OnPermissionCallBacks {
    private static final String TAG = EditTextPresenterImpl.class.getSimpleName();
    private Context mContext;
    private Activity mActivity;
    private RxPhotoNote mRxPhotoNote;
    private RxUser mRxUser;
    private IEditTextView mEditTextView;
    /* 数据 */
    private int mCategoryId;
    private int mPosition;
    private int mComparator;

    private Handler mHandler;

    /* 语音听写 */
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults;
    private String mContentString = null;
    int ret = 0; // 函数调用返回值

    /* 标志位，防止多次点击出现bug效果 */
    private boolean mIsFinishing = false;
    private long mLastTime = 0l;
    /* Voice的是不是显示出来的 */
    private boolean mIsVoiceOpen = false;

    @Inject
    public EditTextPresenterImpl(@ContextLife("Activity") Context context, Activity activity,
                                 RxPhotoNote rxPhotoNote, RxUser rxUser) {
        mRxPhotoNote = rxPhotoNote;
        mHandler = new Handler();
        mContext = context;
        mActivity = activity;
        mRxUser = rxUser;
        mIatResults = new LinkedHashMap<>();
        /*
         * 语音
         * 是一个单例，所以可以这么搞
         */
        SpeechUtility.createUtility(context, "appid=" + BuildConfig.SPEECH_ID);
    }

    @Override
    public void attachView(IView iView) {
        mEditTextView = (IEditTextView) iView;
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNote -> {
                    mEditTextView.setNoteTitle(photoNote.getTitle());
                    mEditTextView.setNoteContent(photoNote.getContent());
                });
    }

    @Override
    public void bindData(int categoryId, int position, int comparator) {
        mCategoryId = categoryId;
        mPosition = position;
        mComparator = comparator;
    }

    @Override
    public void saveText() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                .map(photoNoteList -> photoNoteList.get(mPosition))
                .subscribe(photoNote -> {
                    photoNote.setTitle(mEditTextView.getNoteTitle());
                    photoNote.setContent(mEditTextView.getNoteContent());
                    photoNote.setEditedNoteTime(System.currentTimeMillis());
                    mRxPhotoNote.updatePhotoNote(photoNote).subscribe();
                });
    }

    @Override
    public void update2Evernote() {
        mRxUser.isLoginEvernote()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mRxPhotoNote.findByCategoryId(mCategoryId, mComparator)
                                .map(photoNoteList -> photoNoteList.get(mPosition))
                                .doOnSubscribe(() -> mEditTextView.showProgressBar())
                                .map(photoNote1 -> doUpdate2Evernote(photoNote1.getBigPhotoPathWithoutFile(), photoNote1.getPhotoName()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aBoolean1 -> {
                                    if (aBoolean1) {
                                        mEditTextView.showSnakeBar(mContext.getResources().getString(R.string.toast_success));
                                    } else {
                                        mEditTextView.showSnakeBar(mContext.getResources().getString(R.string.toast_fail));
                                    }
                                    mEditTextView.hideProgressBar();
                                });
                    } else {
                        mEditTextView.showSnakeBar(mContext.getResources().getString(R.string.not_login));
                    }
                });
    }

    @Override
    public void finishActivity(boolean saved) {
        mIsFinishing = true;
        mEditTextView.finishActivityWithAnimation(saved, mCategoryId, mPosition, mComparator);
    }

    @Override
    public void startVoice() {
        doVoiceInput();
    }

    @Permission(PermissionUtils.CODE_AUDIO)
    private void doVoiceInput() {
        boolean has = PermissionUtils.hasPermission4Audio(mContext);
        if (has) {
            mIsVoiceOpen = true;
            if (mIat == null) {
                // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
                mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
            }
            //todo 如果是title怎么办
            mContentString = mEditTextView.getNoteContent();
            setVoiceParam();
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                YLog.e(TAG, "听写失败,错误码：" + ret);
            }
        } else {
            PermissionUtils.requestPermissions(mActivity, mContext.getString(R.string.permission_audio),
                    PermissionUtils.PERMISSION_AUDIO, PermissionUtils.CODE_AUDIO, null);
        }
    }

    @Override
    public void stopVoice() {
        if (mIat != null) {
            mIat.stopListening();
        }
        mIsVoiceOpen = false;
        mEditTextView.hideVoiceAnimation();
    }

    @Override
    public void onBackPressEvent() {
        if (!mIsFinishing && System.currentTimeMillis() - mLastTime > 2000) {
            if (mIsVoiceOpen) {
                stopVoice();
                return;
            }
            if (mEditTextView.isFabMenuLayoutOpen()) {
                mEditTextView.closeFabMenuLayout();
                return;
            }
            mLastTime = System.currentTimeMillis();
            mEditTextView.setFabMenuLayoutClickable(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEditTextView.setFabMenuLayoutClickable(true);
                }
            }, 2000);
            mEditTextView.showSnakeBarWithAction(mContext.getResources().getString(R.string.toast_exit),
                    mContext.getResources().getString(R.string.toast_save), new OnSnackBarActionListener() {
                        @Override
                        public void onClick() {
                            mIsFinishing = true;
                            saveText();
                            finishActivity(true);
                        }
                    });
            return;
        }

        if (!mIsFinishing) {
            mIsFinishing = true;
            finishActivity(false);
        }
    }

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
            mEditTextView.hideVoiceAnimation();
            mEditTextView.showToast(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            mEditTextView.hideVoiceAnimation();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            mEditTextView.setNoteContent(mContentString + getResultString(results));
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            mEditTextView.setRippleVoice((float) volume);
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

        private String parseIatResult(String json) {
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
    };

    @Override
    public void detachView() {
        if (mIat != null) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    private boolean doUpdate2Evernote(String bigPhotoPathWithoutFile, String photoName) {
        mRxUser.initEvernoteSession();
        boolean isSuccess = true;
        try {
            EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
            List<Notebook> notebookList = noteStoreClient.listNotebooks();
            boolean hasNoteBook = false;
            Notebook appNoteBook = null;
            for (Notebook notebook : notebookList) {
                if (notebook.getName().equals(mContext.getResources().getString(R.string.app_name))) {
                    hasNoteBook = true;
                    appNoteBook = notebook;
                }
            }
            if (!hasNoteBook) {
                Notebook notebook = new Notebook();
                notebook.setName(mContext.getResources().getString(R.string.app_name));
                appNoteBook = noteStoreClient.createNotebook(notebook);
            }

            Note note = new Note();
            note.setTitle(mEditTextView.getNoteTitle());
            if (appNoteBook != null) {
                note.setNotebookGuid(appNoteBook.getGuid());
            }
            InputStream in = null;
            try {
                // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
                in = new BufferedInputStream(new FileInputStream(bigPhotoPathWithoutFile));
                FileData data = null;
                data = new FileData(EvernoteUtil.hash(in), new File(bigPhotoPathWithoutFile));
                ResourceAttributes attributes = new ResourceAttributes();
                attributes.setFileName(photoName);

                // Create a new Resource
                Resource resource = new Resource();
                resource.setData(data);
                resource.setMime("image/jpeg");
                resource.setAttributes(attributes);

                note.addToResources(resource);

                // Set the note's ENML content
                String content = EvernoteUtil.NOTE_PREFIX
                        + mEditTextView.getNoteContent()
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

    /**
     * 参数设置
     *
     * @return
     */
    public void setVoiceParam() {
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

    @Override
    public void onPermissionsGranted(List<String> permissions) {
    }

    @Override
    public void onPermissionsDenied(List<String> permissions) {
        PermissionUtils.requestPermissions(mActivity, mContext.getString(R.string.permission_audio),
                PermissionUtils.PERMISSION_AUDIO, PermissionUtils.CODE_AUDIO, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
