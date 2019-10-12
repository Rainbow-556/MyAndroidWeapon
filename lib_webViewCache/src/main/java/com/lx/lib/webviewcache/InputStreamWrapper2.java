package com.lx.lib.webviewcache;

import android.support.annotation.NonNull;

import com.lx.lib.common.util.disklrucache.DiskLruCache;
import com.lx.lib.webviewcache.fetcher.DefaultHttpFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by glennli on 2019/10/9.<br/>
 */
public final class InputStreamWrapper2 extends InputStream {
    private static final int NO_DATA = -1;
    private String url;
    private InputStream netInputStream;
    private boolean isNetFail;
    private OutputStream cacheOutputStream;
    private DiskLruCache.Editor cacheEditor;

    public InputStreamWrapper2(String url) {
        this.url = url;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        // WebView会走该方法，该方法也是由interceptRequest()运行的线程中执行
        if (netInputStream == null && !isNetFail) {
//            if (WebViewCacheManager.get().isDebug()) {
//                Thread currentThread = Thread.currentThread();
//                FLogger.w(WebViewCacheManager.TAG, "InputStreamWrapper2.read(): thread=" + currentThread.getName()
//                        + "-" + currentThread.getId()
//                        + ", " + url);
//            }
            DefaultHttpFetcher fetcher = new DefaultHttpFetcher();
            InputStream in = fetcher.fetch("GET", url);
            if (in != null) {
                isNetFail = false;
                netInputStream = in;
            } else {
                isNetFail = true;
            }
        }
        if (netInputStream != null) {
            final int c = netInputStream.read(b, off, len);
            if (cacheEditor == null) {
                cacheEditor = WebViewCacheManager.get().getDiskCache().getEditor(url);
                if (cacheEditor != null) {
                    try {
                        cacheOutputStream = cacheEditor.newOutputStream(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (c != NO_DATA && cacheOutputStream != null) {
                cacheOutputStream.write(b, off, c);
            }
            return c;
        }
        return NO_DATA;
    }

    @Override
    public int read() throws IOException {
        if (netInputStream == null) {
            return NO_DATA;
        }
        final int c = netInputStream.read();
        if (c != NO_DATA && cacheOutputStream != null) {
            cacheOutputStream.write(c);
        }
        return c;
    }

    @Override
    public void close() throws IOException {
        if (netInputStream == null) {
            return;
        }
        if (cacheEditor != null && cacheOutputStream != null) {
            int c;
            if ((c = netInputStream.read()) != NO_DATA) {
                cacheOutputStream.write(c);
                byte[] buf = new byte[4096];
                // 注：对于一些大的资源文件(300k以上），
                // 从WebView调过来的read方法可能未读完所有数据，这里需要确保将网络输入流读完。
                // 防止写到本地的资源不完整，造成加载错误。
                while ((c = netInputStream.read(buf)) != NO_DATA) {
                    cacheOutputStream.write(buf, 0, c);
                }
            }
            cacheOutputStream.close();
            cacheEditor.commit();
        }
        netInputStream.close();
    }
}
