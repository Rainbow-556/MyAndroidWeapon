package com.lx.myandroidweapon.webviewcache;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lx.lib.common.util.FLogger;
import com.lx.lib.webviewcache.WebViewCacheManager;
import com.lx.myandroidweapon.R;

/**
 * Created by glennli on 2019/6/26.<br/>
 */
public final class WebActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = findViewById(R.id.web_view);
        initWebView();
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    private void initWebView() {
        // 清除所有WebView的内存和磁盘缓存，每次全量加载，方便调试
        mWebView.clearCache(true);
        mWebView.setWebViewClient(new WebViewClient() {
            long start;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                start = System.currentTimeMillis();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                FLogger.i(WebViewCacheManager.TAG, "load time: " + (System.currentTimeMillis() - start));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return WebViewCacheManager.get().interceptRequest(url);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return WebViewCacheManager.get().interceptRequest(request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}

