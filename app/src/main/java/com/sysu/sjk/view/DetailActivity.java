package com.sysu.sjk.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sysu.sjk.base.BaseActivity;
import com.sysu.sjk.custom_view.RoundProgressView;
import com.sysu.sjk.utils.Logger;

/**
 * Created by sjk on 16-10-26.
 */
public class DetailActivity extends BaseActivity {

    public static final String URL_KEY = "url_key";
    public static final String TITLE_KEY = "title_key";

    Toolbar toolbar;
    TextView runningTitleTextView;
    WebView mGankDetailWebView;
    WebViewClient mWebViewClient;
    WebChromeClient mWebChromeClient;
    RoundProgressView mRoundProgressView;
    LinearLayout progressLayout;


    private String mUrl, mTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        runningTitleTextView = (TextView) findViewById(R.id.running_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        mGankDetailWebView = (WebView) findViewById(R.id.detail_webview);
        mRoundProgressView = (RoundProgressView)findViewById(R.id.detail_progress);
    }



    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        mUrl = bundle.getString(URL_KEY, null);
        mTitle = bundle.getString(TITLE_KEY, null);

        runningTitleTextView.setText(mTitle);

        mGankDetailWebView.loadUrl(mUrl);
    }

    // Go back to last page in webview
    // instead of finishing this activity directly
    @Override
    public void onBackPressed() {
        if (mGankDetailWebView.canGoBack()) {
            mGankDetailWebView.goBack();
        } else {
            finish();   // if cannot go back, then finish.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        int color = getResources().getColor(R.color.textOrIconColor);
        int len = menu.size();
        for (int i = 0; i < len; ++i) {
            MenuItem menuItem = menu.getItem(i);
            Drawable d = menuItem.getIcon();
            if (d != null) {
                d.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }

        return true;
    }

    @Override
    public void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   // directly finish the activity
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share_gank:
                        shareGank(null, null, String.format("%s:%s", mTitle, mUrl));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //Logger.log("progress: " + newProgress);
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
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.log("onPageFinished");

                mRoundProgressView.setProgress(100);
                mRoundProgressView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
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

    private void shareGank(String title, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooserIntent = Intent.createChooser(intent, "分享到哪里?");
        startActivity(chooserIntent);
    }

    private  void hideProgress() {

        mRoundProgressView.setVisibility(View.GONE);
    }

    private void showProgress() {
        mRoundProgressView.setVisibility(View.VISIBLE);
    }
}
