package com.sysu.sjk.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sysu.sjk.base.BaseActivity;
import com.sysu.sjk.custom_view.RoundProgressView;
import com.sysu.sjk.utils.Logger;

/**
 * Created by sjk on 16-10-22.
 */
public class GankDetailActivity extends BaseActivity {

    public static final String URL_KEY = "url_key";
    public static final String TITLE_KEY = "title_key";

    private String mUrl = null;
    private String mTitle = null;

    WebView mGankDetailWebView;
    WebChromeClient mWebChromeClient;
    WebViewClient mWebViewClient;
    Toolbar mGankDetailToolbar;

    RoundProgressView mRoundProgressView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_gank_detail;
    }

    @Override
    public void initView() {
        mGankDetailWebView = (WebView)findViewById(R.id.gank_detail_web_view);
        mRoundProgressView = (RoundProgressView)findViewById(R.id.gank_detail_progress);
        mGankDetailToolbar = (Toolbar) findViewById(R.id.gank_detail_toolbar);
        setSupportActionBar(mGankDetailToolbar);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        mUrl = bundle.getString(URL_KEY, null);
        mTitle = bundle.getString(TITLE_KEY, null);

        Logger.log("Go to url: " + mUrl);

        // load the page of {mUrl}
        mGankDetailWebView.loadUrl(mUrl);
    }

    @Override
    public void initListener() {
        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.log("progress: " + newProgress);
                mRoundProgressView.setProgress(newProgress);

            }
        };

        mWebViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mGankDetailWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.log("onPageStarted");
                mRoundProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.log("onPageFinished");

                mRoundProgressView.setProgress(100);
                mRoundProgressView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRoundProgressView.setVisibility(View.GONE);
                    }
                }, 500);
            }
        };

        mGankDetailWebView.setWebChromeClient(mWebChromeClient);
        mGankDetailWebView.setWebViewClient(mWebViewClient);
        mGankDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mGankDetailWebView.getSettings().setJavaScriptEnabled(true);
        mGankDetailWebView.getSettings().setSupportZoom(true);
    }
}
