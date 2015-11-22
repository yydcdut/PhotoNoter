package com.yydcdut.note.mvp.p.setting.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.FeedbackModel;
import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.setting.IFeedbackPresenter;
import com.yydcdut.note.mvp.v.setting.IFeedbackView;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.ThreadExecutorPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by yuyidong on 15/11/13.
 */
public class FeedbackPresenterImpl implements IFeedbackPresenter, Handler.Callback {
    private Context mContext;
    private int mType;
    private IFeedbackView mFeedbackView;

    private Handler mHandler;

    private FeedbackModel mFeedbackModel;
    private ThreadExecutorPool mThreadExecutorPool;

    @Inject
    public FeedbackPresenterImpl(@ContextLife("Activity") Context context, FeedbackModel feedbackModel,
                                 ThreadExecutorPool threadExecutorPool) {
        mContext = context;
        mFeedbackModel = feedbackModel;
        mThreadExecutorPool = threadExecutorPool;

    }

    @Override
    public void attachView(IView iView) {
        mFeedbackView = (IFeedbackView) iView;
        mHandler = new Handler(this);
        if (mType == IFeedbackPresenter.TYPE_FEEDBACK) {
            mFeedbackView.showFeedbackTitle();
        } else {
            mFeedbackView.showContactTitle();
        }
    }

    @Override
    public void bindData(int type) {
        mType = type;
    }

    @Override
    public boolean checkFeendback() {
        String email = mFeedbackView.getEmail();
        if (TextUtils.isEmpty(email)) {
            mFeedbackView.showSnackBar(mContext.getResources().getString(R.string.toast_input_email));
            return false;
        }
        if (!isEmail(email)) {
            mFeedbackView.showSnackBar(mContext.getResources().getString(R.string.toast_input_email_error));
            return false;
        }
        String content = mFeedbackView.getContent();
        if (TextUtils.isEmpty(content)) {
            mFeedbackView.showSnackBar(mContext.getResources().getString(R.string.toast_input_error));
            return false;
        }
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mFeedbackView.showSnackBar(mContext.getResources().getString(R.string.toast_no_connection));
            return false;
        }
        return true;
    }

    @Override
    public void sendFeedback(final String email, final String content) {
        mFeedbackView.showLoading();
        mThreadExecutorPool.getExecutorPool().submit(new Runnable() {
            @Override
            public void run() {
                mFeedbackModel.sendFeedback(System.currentTimeMillis() + "",
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
