package com.lx.lib.webviewcache;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.lx.lib.common.util.FLogger;
import com.lx.lib.common.util.MD5Util;
import com.lx.lib.common.util.RunUtil;
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

    public static final String TAG = "WebViewCache";
    private Context mContext;
    private boolean isInit, isEnable = true, isDebug = true;
    private Config mConfig;
    private DefaultDiskCache mDiskCache;

    public void init(Context context, Config config) {
        isInit = true;
        mContext = context.getApplicationContext();
        mConfig = config;
        mDiskCache = new DefaultDiskCache(new File(config.getCacheDirPath()), 1, config.getMaxCacheSize());
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void clear() {
        RunUtil.runOnWorkerThread(new RunUtil.Work() {
            @Override
            public Object execute() {
                mDiskCache.clear();
                return null;
            }
        });
    }

    public WebResourceResponse interceptRequest(String url) {
//        FLogger.d(TAG, "below 21 shouldInterceptRequest: " + url);
        return null;
    }

    /**
     * WebView中所有的网络请求都会走该方法(例如：ajax、js、css、html、图片等)
     *
     * @param request
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public WebResourceResponse interceptRequest(WebResourceRequest request) {
        if (!isInit || !isEnable) {
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
            if (isDebug) {
                FLogger.d(TAG, "from cache: " + url);
            }
            return makeResponse(uri, inputStream);
        }
        DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
        inputStream = fetcher.fetch("GET", url);
        if (inputStream != null) {
            mDiskCache.put(cacheKey, inputStream);
            inputStream = mDiskCache.get(cacheKey);
            if (inputStream != null) {
                if (isDebug) {
                    FLogger.d(TAG, "from net (CustomFetcher): " + url);
                }
                return makeResponse(uri, inputStream);
            }
        }
        if (isDebug) {
            FLogger.d(TAG, "from net (WebView): " + url);
        }
        return null;
    }

    private static WebResourceResponse makeResponse(Uri uri, InputStream inputStream) {
        return new WebResourceResponse(MimeTypeUtil.getMimeTypeFromUrl(uri), "utf-8", inputStream);
    }

    private boolean checkExtension(Uri uri) {
        String extension = MimeTypeUtil.getExtensionFromUrl(uri);
        return mConfig.cacheExtension.contains(extension);
    }

    public static final class Config {
        private String cacheDirPath;
        /**
         * 默认50M
         */
        private long maxCacheSize = 50 * 1024 * 1024;
        private CacheExtension cacheExtension = new CacheExtension();

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

        public Config addCacheExtension(String extension) {
            cacheExtension.add(extension);
            return this;
        }

        public Config removeCacheExtension(String extension) {
            cacheExtension.remove(extension);
            return this;
        }

        public Config maxCacheSize(long maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
            return this;
        }

        public long getMaxCacheSize() {
            return maxCacheSize;
        }
    }
}
