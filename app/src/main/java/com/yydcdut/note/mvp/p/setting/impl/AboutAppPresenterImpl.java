package com.yydcdut.note.mvp.p.setting.impl;

import com.yydcdut.note.mvp.IView;
import com.yydcdut.note.mvp.p.setting.IAboutAppPresenter;
import com.yydcdut.note.mvp.v.setting.IAboutAppView;
import com.yydcdut.note.utils.PhoneUtils;

/**
 * Created by yuyidong on 15/11/13.
 */
public class AboutAppPresenterImpl implements IAboutAppPresenter {
    private IAboutAppView mAboutAppView;

    @Override
    public void attachView(IView iView) {
        mAboutAppView = (IAboutAppView) iView;
        mAboutAppView.showVersion(PhoneUtils.getVersion());
    }

    @Override
    public void detachView() {

    }

    @Override
    public void doUpdateVersion() {
        mAboutAppView.updateApk();
    }

    @Override
    public void doFeedback() {
        mAboutAppView.jump2FeedbackActivity();
    }

    @Override
    public void doShare() {
        mAboutAppView.share();
    }

    @Override
    public void gotoGithub() {
        mAboutAppView.github();
    }

}
