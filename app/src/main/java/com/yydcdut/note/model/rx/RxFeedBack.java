package com.yydcdut.note.model.rx;

import com.yydcdut.note.mvp.p.setting.IFeedbackPresenter;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yuyidong on 15/12/1.
 */
public class RxFeedBack {
    private static final String FEEDBACK = "Feedback";
    private static final String CONTACT = "Contact";
    private String mType;
    private String mFeedbackId;
    private String mContent;
    private String mEmail;

    public RxFeedBack setType(int type) {
        switch (type) {
            case IFeedbackPresenter.TYPE_CONTACT:
                mType = FEEDBACK;
                break;
            case IFeedbackPresenter.TYPE_FEEDBACK:
                mType = CONTACT;
                break;
            default:
                mType = "????";
                break;
        }
        return this;
    }

    public RxFeedBack setFeedBackId(String feedback_id) {
        mFeedbackId = feedback_id;
        return this;
    }

    public RxFeedBack setContent(String content) {
        mContent = content;
        return this;
    }

    public RxFeedBack setEmail(String email) {
        mEmail = email;
        return this;
    }

    public void doOb() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                StringBuilder sb = new StringBuilder(mEmail);
                sb.append("<---联系方式   ")
                        .append(mType)
                        .append("   反馈内容--->")
                        .append(mContent);
                subscriber.onNext(sb.toString());
            }
        });
    }


}
