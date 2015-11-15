package com.yydcdut.note.mvp.v.setting.impl;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.mvp.p.setting.IAboutAppPresenter;
import com.yydcdut.note.mvp.p.setting.IFeedbackPresenter;
import com.yydcdut.note.mvp.p.setting.impl.AboutAppPresenterImpl;
import com.yydcdut.note.mvp.v.setting.IAboutAppView;
import com.yydcdut.note.utils.LollipopCompat;

/**
 * Created by yuyidong on 15/9/2.
 */
public class AboutAppActivity extends BaseActivity implements IAboutAppView, View.OnClickListener {
    private IAboutAppPresenter mAboutAppPresenter;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void initUiAndListener() {
        mAboutAppPresenter = new AboutAppPresenterImpl();
        mAboutAppPresenter.attachView(this);
        initToolBarUI();
        initListener();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.about_setting));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
    }

    private void initListener() {
        findViewById(R.id.layout_ripple_update).setOnClickListener(this);
        findViewById(R.id.layout_ripple_contact).setOnClickListener(this);
        findViewById(R.id.layout_ripple_share).setOnClickListener(this);
        findViewById(R.id.layout_ripple_github).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_update:
                mAboutAppPresenter.doUpdateVersion();
                break;
            case R.id.layout_contact:
                mAboutAppPresenter.doFeedback();
                break;
            case R.id.layout_share:
                mAboutAppPresenter.doShare();
                break;
            case R.id.layout_github:
                mAboutAppPresenter.gotoGithub();
                break;
        }
    }

    @Override
    public void showVersion(String version) {
        ((TextView) findViewById(R.id.txt_version)).setText(getResources().getString(R.string.version)
                + " " + version);
    }

    @Override
    public void updateApk() {
        Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.yydcdut.note");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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
        startActivity(Intent.createChooser(sendIntent, "Share"));
    }

    @Override
    public void github() {
        Uri githubUrl = Uri.parse("https://github.com/yydcdut/PhotoNoter");
        Intent githubIntent = new Intent(Intent.ACTION_VIEW, githubUrl);
        startActivity(githubIntent);
    }
}
