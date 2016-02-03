package com.yydcdut.note.presenters.setting.impl;

import android.content.Context;
import android.text.TextUtils;

import com.yydcdut.note.R;
import com.yydcdut.note.injector.ContextLife;
import com.yydcdut.note.model.rx.RxFeedBack;
import com.yydcdut.note.presenters.setting.IFeedbackPresenter;
import com.yydcdut.note.utils.NetworkUtils;
import com.yydcdut.note.utils.PhoneUtils;
import com.yydcdut.note.views.IView;
import com.yydcdut.note.views.setting.IFeedbackView;

import org.json.JSONException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yuyidong on 15/11/13.
 */
public class FeedbackPresenterImpl implements IFeedbackPresenter {
    private Context mContext;
    private int mType;
    private IFeedbackView mFeedbackView;

    private RxFeedBack mRxFeedBack;

    @Inject
    public FeedbackPresenterImpl(@ContextLife("Activity") Context context, RxFeedBack rxFeedBack) {
        mContext = context;
        mRxFeedBack = rxFeedBack;
    }

    @Override
    public void attachView(IView iView) {
        mFeedbackView = (IFeedbackView) iView;
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
        try {
            mRxFeedBack.setType(mType)
                    .setEmail(mFeedbackView.getEmail())
                    .setContent(mFeedbackView.getContent())
                    .setDeviceInfo(PhoneUtils.getDeviceInfo(mContext))
                    .setFeedBackId(System.currentTimeMillis() + "")
                    .doObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Map<String, String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Map<String, String> stringStringMap) {
                            mFeedbackView.hideLoadingAndFinish();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            //todo 出错怎么办
        }
    }

    @Override
    public void detachView() {
    }

    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
