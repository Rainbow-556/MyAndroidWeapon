package com.lx.lib.webviewcache.cache;

import com.lx.lib.common.util.CommonUtil;
import com.lx.lib.common.util.FLogger;
import com.lx.lib.common.util.MD5Util;
import com.lx.lib.common.util.disklrucache.DiskLruCache;
import com.lx.lib.webviewcache.WebViewCacheManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class DefaultDiskCache {
    private volatile DiskLruCache mDiskLruCache;
    private File mDir;
    private int mVersion;
    private long mMaxSize;

    public DefaultDiskCache(File dir, int version, long maxSize) {
        mDir = dir;
        mVersion = version;
        mMaxSize = maxSize;
    }

    private DiskLruCache getDiskLruCache() throws Exception {
        if (mDiskLruCache == null) {
            synchronized (this) {
                if (mDiskLruCache == null) {
                    mDiskLruCache = DiskLruCache.open(mDir, mVersion, 1, mMaxSize);
                }
            }
        }
        return mDiskLruCache;
    }

    public void put(String url, InputStream in) {
        if (in == null) {
            return;
        }
        final String cacheKey = generateCacheKey(url);
        OutputStream out = null;
        try {
            DiskLruCache.Editor editor = getDiskLruCache().edit(cacheKey);
            if (editor != null) {
                final byte[] buffer = new byte[2048];
                out = editor.newOutputStream(0);
                int count;
                while ((count = in.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                editor.commit();
            }
            getDiskLruCache().flush();
        } catch (Exception e) {
            e.printStackTrace();
            boolean remove = remove(cacheKey);
            if (WebViewCacheManager.get().isDebug()) {
                FLogger.e(WebViewCacheManager.TAG, "add cache fail, remove old file success=" + remove + ", url=" + url);
            }
        } finally {
            CommonUtil.closeQuietly(in);
            CommonUtil.closeQuietly(out);
        }
    }

    public InputStream get(String url) {
        try {
            DiskLruCache.Snapshot snapshot = getDiskLruCache().get(generateCacheKey(url));
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(String url) {
        try {
            return getDiskLruCache().remove(generateCacheKey(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long size() {
        try {
            return getDiskLruCache().size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void clear() {
        try {
            getDiskLruCache().delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // DiskLruCache.delete()调用之后，就无法使用了，置为null，下次用时需要重新初始化
            mDiskLruCache = null;
        }
    }

    private static String generateCacheKey(String url) {
        return MD5Util.md5(url);
    }
}
