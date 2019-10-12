package com.lx.lib.webviewcache;

import android.support.annotation.NonNull;

import com.lx.lib.common.util.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by glennli on 2019/10/8.<br/>
 */
public final class InputStreamWrapper extends InputStream {
    private InputStream netInputStream;
    private OutputStream cacheOutputStream;
    private DiskLruCache.Editor cacheEditor;

    public InputStreamWrapper(String url, InputStream netInputStream) {
        this.netInputStream = netInputStream;
        cacheEditor = WebViewCacheManager.get().getDiskCache().getEditor(url);
        if (cacheEditor != null) {
            try {
                cacheOutputStream = cacheEditor.newOutputStream(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        // WebView会走该方法
        int c = netInputStream.read(b, off, len);
        if (c != -1 && cacheOutputStream != null) {
            cacheOutputStream.write(b, off, c);
        }
        return c;
    }

    @Override
    public int read() throws IOException {
        int c = netInputStream.read();
        if (c != -1 && cacheOutputStream != null) {
            cacheOutputStream.write(c);
        }
        return c;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (cacheEditor != null && cacheOutputStream != null) {
            int c;
            if ((c = netInputStream.read()) != -1) {
                cacheOutputStream.write(c);
                byte[] buf = new byte[4096];
                // 注：对于一些大的资源文件(300k以上），
                // 从WebView调过来的read方法可能未读完所有数据，这里需要确保将网络输入流读完。
                // 防止写到本地的资源不完整，造成加载错误。
                while ((c = netInputStream.read(buf)) != -1) {
                    cacheOutputStream.write(buf, 0, c);
                }
            }
            cacheOutputStream.close();
            cacheEditor.commit();
        }
        netInputStream.close();
    }
}
