package com.yydcdut.note.views.login.impl;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.yydcdut.note.R;
import com.yydcdut.note.presenters.login.impl.LoginPresenterImpl;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.views.BaseActivity;
import com.yydcdut.note.views.login.ILoginView;
import com.yydcdut.note.widget.CircleProgressBarLayout;
import com.yydcdut.note.widget.fab2.snack.OnSnackBarActionListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuyidong on 15-3-25.
 */
public class LoginActivity extends BaseActivity implements ILoginView {
    @Inject
    LoginPresenterImpl mLoginPresenter;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.layout_progress)
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
        ButterKnife.bind(this);
        mLoginPresenter.attachView(this);
        initToolBarUI();
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        mIPresenter = mLoginPresenter;
    }

    private void initToolBarUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_login));
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

    @OnClick(R.id.btn_login_qq)
    public void clickQQ(View v) {
        if (mLoginPresenter.checkInternet()) {
            mLoginPresenter.loginQQ();
        }
    }

    @OnClick(R.id.btn_login_evernote)
    public void clickEvernote(View v) {
        if (mLoginPresenter.checkInternet()) {
            mLoginPresenter.loginEvernote();
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
    public void hideProgressBar() {
        mCircleProgressBar.hide();
    }

    @Override
    public void finishActivityWithResult(int result) {
        setResult(result, null);
        finish();
    }

    @Override
    public void showSnackBarWithAction(String message, String action, final OnSnackBarActionListener listener) {
        Snackbar.make(findViewById(R.id.cl_login), message, Snackbar.LENGTH_SHORT)
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
                    mLoginPresenter.onEvernoteLoginFinished(true);
                } else {
                    // handle failure
                    mLoginPresenter.onEvernoteLoginFinished(false);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}