package com.yydcdut.note.mvp.v;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.AppCompat;
import com.yydcdut.note.utils.Const;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yuyidong on 15/12/27.
 */
public class WebViewActivity extends BaseActivity {

    @Bind(R.id.web)
    WebView mWebView;
    @Bind(R.id.pb_webview)
    ProgressBar mProgressBar;

    @Override
    public boolean setStatusBar() {
        return true;
    }

    @Override
    public int setContentView() {
        return R.layout.activity_webview;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        initToolBarUI(bundle.getString(Const.WEBVIEW_TITLE));
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(bundle.getString(Const.WEBVIEW_URL));
    }

    private void initToolBarUI(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
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

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
