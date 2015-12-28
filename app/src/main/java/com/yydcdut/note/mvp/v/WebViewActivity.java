package com.yydcdut.note.mvp.v;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

    private WebSettings mWebSettings;

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
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
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
        toolbar.setOnMenuItemClickListener(onToolBarMenuItemClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
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

    private Toolbar.OnMenuItemClickListener onToolBarMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_refresh:
                    String url = mWebView.getUrl();
                    mWebView.loadUrl(url);
                    break;
                case R.id.menu_copy_url:
                    String url2 = mWebView.getUrl();
                    copy2ClipBoard(url2);
                    break;
            }
            return true;
        }
    };

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

    private void copy2ClipBoard(String string) {
        ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData.Item item = new ClipData.Item(string);
        ClipData clipData = cbm.getPrimaryClip();
        clipData.addItem(item);
        cbm.setPrimaryClip(clipData);
    }
}
