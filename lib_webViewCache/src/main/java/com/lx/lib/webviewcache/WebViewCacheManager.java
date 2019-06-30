package com.lx.lib.webviewcache;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.lx.lib.common.util.FLogger;
import com.lx.lib.common.util.MD5Util;
import com.lx.lib.webviewcache.cache.DefaultDiskCache;
import com.lx.lib.webviewcache.fetcher.DefaultHttpFetcher;
import com.lx.lib.webviewcache.util.MimeTypeUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class WebViewCacheManager {
    private static class Holder {
        private static final WebViewCacheManager INSTANCE = new WebViewCacheManager();
    }

    public static WebViewCacheManager get() {
        return Holder.INSTANCE;
    }

    private Context mContext;
    private boolean isInit;
    private Config mConfig;
    private DefaultDiskCache mDiskCache;

    public void init(Context context, Config config) {
        isInit = true;
        mContext = context.getApplicationContext();
        mConfig = config;
        mDiskCache = new DefaultDiskCache(new File(config.getCacheDirPath()), 1, config.getCacheSize());
    }

    public WebResourceResponse interceptRequest(String url) {
//        FLogger.d("below 21 shouldInterceptRequest: " + url);
        if (!isInit) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse interceptRequest(WebResourceRequest request) {
        if (!isInit) {
            return null;
        }
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        final Uri uri = request.getUrl();
        if (!checkExtension(uri)) {
            return null;
        }
        final String url = uri.toString();
        final String cacheKey = MD5Util.md5(url);
        InputStream inputStream = mDiskCache.get(cacheKey);
        if (inputStream != null) {
            FLogger.d("from cache: " + url);
            return makeResponse(uri, inputStream);
        }
        DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
        inputStream = fetcher.fetch("GET", url);
        if (inputStream != null) {
            mDiskCache.put(cacheKey, inputStream);
            inputStream = mDiskCache.get(cacheKey);
            if (inputStream != null) {
                FLogger.d("from net: " + url);
                return makeResponse(uri, inputStream);
            }
        }
        FLogger.d("from net 2: " + url);
        return null;
    }

    private WebResourceResponse makeResponse(Uri uri, InputStream inputStream) {
        return new WebResourceResponse(MimeTypeUtil.getMimeTypeFromUrl(uri), "utf-8", inputStream);
    }

    private boolean checkExtension(Uri uri) {
        String extension = MimeTypeUtil.getExtensionFromUrl(uri);
        return mConfig.cacheExtensionConfig.contains(extension);
    }

    public static class Config {
        private String cacheDirPath;
        /**
         * 默认100M
         */
        private long cacheSize = 100 * 1024 * 1024;
        private CacheExtensionConfig cacheExtensionConfig = new CacheExtensionConfig();

        public Config cacheDirPath(String cacheDirPath) {
            if (TextUtils.isEmpty(cacheDirPath)) {
                throw new IllegalArgumentException("cache dir path is empty!");
            }
            this.cacheDirPath = cacheDirPath;
            return this;
        }

        public String getCacheDirPath() {
            return cacheDirPath;
        }

        public CacheExtensionConfig getCacheExtensionConfig() {
            return cacheExtensionConfig;
        }

        public long getCacheSize() {
            return cacheSize;
        }
    }
}
