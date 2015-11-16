package com.yydcdut.note.mvp.v.login.impl;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.yydcdut.note.R;
import com.yydcdut.note.controller.BaseActivity;
import com.yydcdut.note.listener.OnSnackBarActionListener;
import com.yydcdut.note.mvp.p.login.ILoginPresenter;
import com.yydcdut.note.mvp.p.login.impl.LoginPresenterImpl;
import com.yydcdut.note.mvp.v.login.ILoginView;
import com.yydcdut.note.utils.LollipopCompat;
import com.yydcdut.note.view.CircleProgressBarLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuyidong on 15-3-25.
 */
public class LoginActivity extends BaseActivity implements ILoginView, View.OnClickListener {
    private ILoginPresenter mLoginPresenter;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.layout_progress)
    CircleProgressBarLayout mCircleProgressBar;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.inject(this);
        mLoginPresenter = new LoginPresenterImpl(this);
        mLoginPresenter.attachView(this);
        initToolBarUI();
        initLoginButtonListener();
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_login));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        LollipopCompat.setElevation(toolbar, getResources().getDimension(R.dimen.ui_elevation));
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

    private void initLoginButtonListener() {
        findViewById(R.id.btn_login_qq).setOnClickListener(this);
        findViewById(R.id.btn_login_evernote).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean hasNet = mLoginPresenter.checkInternet();
        if (!hasNet) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_login_qq:
                mLoginPresenter.loginQQ();
                break;
            case R.id.btn_login_evernote:
                mLoginPresenter.loginEvernote();
                break;
        }
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.cl_login), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        mCircleProgressBar.show();
    }

    @Override
    public void hidePregressBar() {
        mCircleProgressBar.hide();
    }

    @Override
    public void finishActivityWithResult(int result) {
        setResult(result, null);
        finish();
    }

    @Override
    public void showSnackBarWithAction(String message, String action, final OnSnackBarActionListener listener) {
        Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_SHORT)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick();
                        }
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    // handle success
                    mLoginPresenter.onLoginFinished(true);
                } else {
                    // handle failure
                    mLoginPresenter.onLoginFinished(false);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}