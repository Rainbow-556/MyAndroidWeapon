package com.lx.lib.webviewcache;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.lx.lib.common.util.CommonUtil;
import com.lx.lib.common.util.FLogger;
import com.lx.lib.common.util.RunUtil;
import com.lx.lib.webviewcache.cache.DefaultDiskCache;
import com.lx.lib.webviewcache.fetcher.DefaultHttpFetcher;
import com.lx.lib.webviewcache.util.MimeTypeUtil;
import com.lx.lib.webviewcache.util.ThreadPoolUtil;

import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

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
//        FLogger.e(WebViewCacheManager.TAG, "interceptRequest(): " + Thread.currentThread().getName());
        if (isDebug) {
//            FLogger.i(TAG, "interceptRequest: thread=" + Thread.currentThread().getName()
//                    + ", " + request.getUrl().toString() + ", headers=" + request.getRequestHeaders());
        }
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
        // read cache
//        InputStream inputStream;
        InputStream inputStream = mDiskCache.get(url);
        if (inputStream != null) {
            if (isDebug) {
                FLogger.d(TAG, "from cache: " + url);
            }
            return makeResponse(uri, inputStream);
        }
        // 异步加载
//        WebResourceResponse response = asyncLoad_2(request, uri);
//        if (response != null) {
//            return response;
//        }
        //
        DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
        inputStream = fetcher.fetch("GET", url);
        if (inputStream != null) {
            mDiskCache.put(url, inputStream);
            inputStream = mDiskCache.get(url);
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

    private WebResourceResponse asyncLoad_2(WebResourceRequest request, Uri uri) {
        return makeResponse(uri, new LxInputStream(uri));
    }

    private WebResourceResponse asyncLoad(final WebResourceRequest request, final Uri uri) {
        final PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream();
        try {
            in.connect(out);
        } catch (Exception e) {
            e.printStackTrace();
            CommonUtil.closeQuietly(out);
            CommonUtil.closeQuietly(in);
            return null;
        }
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                String url = uri.toString();
                DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
                InputStream netInput = null, cacheInput = null;
                try {
                    long start = System.currentTimeMillis();
                    netInput = fetcher.fetch("GET", url);
                    if (netInput != null) {
                        byte[] buffer = new byte[1024];
                        int count;
                        while ((count = netInput.read(buffer)) != -1) {
                            out.write(buffer, 0, count);
                        }
                    }
//                    mDiskCache.put(url, netInput);
//                    cacheInput = mDiskCache.get(url);
//                    if (cacheInput != null) {
//                        byte[] buffer = new byte[1024];
//                        int count;
//                        while ((count = cacheInput.read(buffer)) != -1) {
//                            out.write(buffer, 0, count);
//                        }
//                    }
//                    FLogger.e(TAG, "read time: " + Thread.currentThread().getName() + ", " + url
//                            + ", " + (System.currentTimeMillis() - start));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CommonUtil.closeQuietly(cacheInput);
                    CommonUtil.closeQuietly(netInput);
                    CommonUtil.closeQuietly(out);
                }
            }
        });
        LxWebResourceResponse response = new LxWebResourceResponse(MimeTypeUtil.getMimeTypeFromUrl(uri), "utf-8", in);
        return response;
    }

    private static WebResourceResponse makeResponse(Uri uri, InputStream inputStream) {
        return new LxWebResourceResponse(MimeTypeUtil.getMimeTypeFromUrl(uri), "utf-8", inputStream);
    }

    private boolean checkExtension(Uri uri) {
        String extension = MimeTypeUtil.getExtensionFromUrl(uri);
        return mConfig.cacheExtension.contains(extension);
    }

    public DefaultDiskCache getDiskCache() {
        return mDiskCache;
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
