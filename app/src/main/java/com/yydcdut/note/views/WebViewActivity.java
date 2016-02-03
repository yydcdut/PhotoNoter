package com.yydcdut.note.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
                    /**
                     * 01-18 19:44:12.821 11610-11610/com.yydcdut.note E/AndroidRuntime: FATAL EXCEPTION: main
                     Process: com.yydcdut.note, PID: 11610
                     java.lang.NullPointerException
                     //                     at com.yydcdut.note.v.WebViewActivity.copy2ClipBoard(WebViewActivity.java:149)
                     //                     at com.yydcdut.note.v.WebViewActivity.access$000(WebViewActivity.java:25)
                     //                     at com.yydcdut.note.v.WebViewActivity$1.onMenuItemClick(WebViewActivity.java:102)
                     //                     at android.support.v7.widget.Toolbar$1.onMenuItemClick(Toolbar.java:172)
                     //                     at android.support.v7.widget.ActionMenuView$MenuBuilderCallback.onMenuItemSelected(ActionMenuView.java:760)
                     //                     at android.support.v7.view.menu.MenuBuilder.dispatchMenuItemSelected(MenuBuilder.java:811)
                     //                     at android.support.v7.view.menu.MenuItemImpl.invoke(MenuItemImpl.java:152)
                     //                     at android.support.v7.view.menu.MenuBuilder.performItemAction(MenuBuilder.java:958)
                     //                     at android.support.v7.view.menu.MenuBuilder.performItemAction(MenuBuilder.java:948)
                     //                     at android.support.v7.view.menu.MenuPopupHelper.onItemClick(MenuPopupHelper.java:191)
                     //                     at android.widget.AdapterView.performItemClick(AdapterView.java:299)
                     //                     at android.widget.AbsListView.performItemClick(AbsListView.java:1154)
                     //                     at android.widget.AbsListView$PerformClick.run(AbsListView.java:3031)
                     //                     at android.widget.AbsListView$3.run(AbsListView.java:3915)
                     //                     at android.os.Handler.handleCallback(Handler.java:808)
                     //                     at android.os.Handler.dispatchMessage(Handler.java:103)
                     //                     at android.os.Looper.loop(Looper.java:193)
                     //                     at android.app.ActivityThread.main(ActivityThread.java:5315)
                     //                     at java.lang.reflect.Method.invokeNative(Native Method)
                     //                     at java.lang.reflect.Method.invoke(Method.java:515)
                     //                     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:836)
                     //                     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:652)
                     //                     at dalvik.system.NativeStart.main(Native Method)
                     */
                    try {
                        copy2ClipBoard(url2);
                    } catch (Exception e) {
                    }
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
        if (TextUtils.isEmpty(string)) {
            return;
        }
        ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData.Item item = new ClipData.Item(string);
        ClipData clipData = cbm.getPrimaryClip();
        clipData.addItem(item);
        cbm.setPrimaryClip(clipData);
    }
}
