package com.yydcdut.note.views.setting.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.presenters.setting.IFeedbackPresenter;
import com.yydcdut.note.presenters.setting.impl.AboutAppPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.WebViewActivity;
import com.yydcdut.note.views.setting.IAboutAppView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 15/9/2.
 */
public class AboutAppActivity extends BaseActivity implements IAboutAppView {
    @Inject
    AboutAppPresenterImpl mAboutAppPresenter;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mAboutAppPresenter;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAboutAppPresenter.attachView(this);
        initToolBarUI();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.about_setting));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        AppCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.layout_update)
    public void clickUpdate(View v) {
        mAboutAppPresenter.doUpdateVersion();
    }

    @OnClick(R.id.layout_contact)
    public void clickContact(View v) {
        mAboutAppPresenter.doFeedback();
    }

    @OnClick(R.id.layout_share)
    public void clickShare(View v) {
        mAboutAppPresenter.doShare();
    }

    @OnClick(R.id.layout_github)
    public void clickGitHub(View v) {
        mAboutAppPresenter.gotoGithub();
    }

    @Override
    public void showVersion(String version) {
        ((TextView) findViewById(R.id.txt_version)).setText(getResources().getString(R.string.version)
                + " " + version);
    }

    @Override
    public void updateApk() {
        //http://www.wandoujia.com/apps/com.yydcdut.note
        Intent intent = new Intent(this, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.WEBVIEW_TITLE, getResources().getString(R.string.app_name));
        bundle.putString(Const.WEBVIEW_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.yydcdut.note");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void jump2FeedbackActivity() {
        Intent data = new Intent(this, FeedbackActivity.class);
        data.putExtra(IFeedbackPresenter.TYPE, IFeedbackPresenter.TYPE_CONTACT);
        startActivity(data);
    }

    @Override
    public void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.about_share_content));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share)));
    }

    @Override
    public void viewGitHub() {
        Intent intent = new Intent(this, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.WEBVIEW_TITLE, "Github");
        bundle.putString(Const.WEBVIEW_URL, "https://github.com/yydcdut/PhotoNoter");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
