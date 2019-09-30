package com.lx.lib.webviewcache;

import android.net.Uri;

import com.lx.lib.common.util.FLogger;
import com.lx.lib.webviewcache.cache.DefaultDiskCache;
import com.lx.lib.webviewcache.fetcher.DefaultHttpFetcher;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by glennli on 2019/9/29.<br/>
 */
public final class LxInputStream extends InputStream {
    private static final int NO_DATA = -1;
    private String url;
    private InputStream realInputStream;

    public LxInputStream(Uri uri) {
        url = uri.toString();
    }

    @Override
    public int read() throws IOException {
        if (realInputStream == null) {
            FLogger.e(WebViewCacheManager.TAG, "LxInputStream.read(): "
                    + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
            final DefaultDiskCache diskCache = WebViewCacheManager.get().getDiskCache();
//            InputStream cacheInputStream = diskCache.get(url);
//            if (cacheInputStream != null) {
//                realInputStream = cacheInputStream;
//            } else {
//                DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
//                InputStream netInputStream = fetcher.fetch("GET", url);
//                if (netInputStream != null) {
//                    diskCache.put(url, netInputStream);
//                    realInputStream = diskCache.get(url);
//                }
//            }
            DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
            InputStream netInputStream = fetcher.fetch("GET", url);
            if (netInputStream != null) {
                diskCache.put(url, netInputStream);
                realInputStream = diskCache.get(url);
            }
        }
        return realInputStream == null ? NO_DATA : realInputStream.read();
    }
}
