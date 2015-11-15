package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.yydcdut.note.NoteApplication;
import com.yydcdut.note.R;
import com.yydcdut.note.model.FeedbackModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.IFeedbackPresenter;
import com.yydcdut.note.mvp.v.setting.IFeedbackView;
import com.yydcdut.note.utils.NetworkUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuyidong on 15/11/13.
 */
public class FeedbackPresenterImpl implements IFeedbackPresenter, Handler.Callback {
    private Context mContext;
    private int mType;
    private IFeedbackView mFeedbackView;

    private Handler mHandler;

    public FeedbackPresenterImpl(int type) {
        mType = type;
    }

    @Override
    public void attachView(IView iView) {
        mFeedbackView = (IFeedbackView) iView;
        mContext = NoteApplication.getContext();
        mHandler = new Handler(this);
        if (mType == IFeedbackPresenter.TYPE_FEEDBACK) {
            mFeedbackView.showFeedbackTitle();
        } else {
            mFeedbackView.showContactTitle();
        }
    }

    @Override
    public boolean checkFeendback() {
        String email = mFeedbackView.getEmail();
        if (TextUtils.isEmpty(email)) {
            mFeedbackView.showSnackbar(mContext.getResources().getString(R.string.toast_input_email));
            return false;
        }
        if (!isEmail(email)) {
            mFeedbackView.showSnackbar(mContext.getResources().getString(R.string.toast_input_email_error));
            return false;
        }
        String content = mFeedbackView.getContent();
        if (TextUtils.isEmpty(content)) {
            mFeedbackView.showSnackbar(mContext.getResources().getString(R.string.toast_input_error));
            return false;
        }
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mFeedbackView.showSnackbar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void sendFeedback(final String email, final String content) {
        mFeedbackView.showLoading();
        NoteApplication.getInstance().getExecutorPool().submit(new Runnable() {
            @Override
            public void run() {
                FeedbackModel.getInstance().sendFeedback(System.currentTimeMillis() + "",
                        email + "<---联系方式   " +
                                (mType == IFeedbackPresenter.TYPE_FEEDBACK ? "Feedback" : "Contact") +
                                "   反馈内容--->" + content);
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void detachView() {
        mHandler = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        mFeedbackView.hideLoadingAndFinish();
        return false;
    }

    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
