package com.lx.lib.webviewcache;

import android.webkit.WebResourceResponse;

import com.lx.lib.common.util.FLogger;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by glennli on 2019/9/4.<br/>
 */
public final class LxWebResourceResponse extends WebResourceResponse {
    public LxWebResourceResponse(String mimeType, String encoding, InputStream data) {
        super(mimeType, encoding, data);
    }

    @Override
    public Map<String, String> getResponseHeaders() {
//        FLogger.i(WebViewCacheManager.TAG, "LxWebResourceResponse.getResponseHeaders()");
        return super.getResponseHeaders();
    }

    @Override
    public InputStream getData() {
//        FLogger.e(WebViewCacheManager.TAG, "LxWebResourceResponse.getData(): " + Thread.currentThread().getName());
        return super.getData();
    }
}
