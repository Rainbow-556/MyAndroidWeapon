package com.lx.lib.webviewcache.cache;

import com.lx.lib.common.util.CommonUtil;
import com.lx.lib.common.util.disklrucache.DiskLruCache;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class DefaultDiskCache {
    private DiskLruCache mDiskLruCache;
    private File mDir;
    private int mVersion;
    private long mMaxSize;

    public DefaultDiskCache(File dir, int version, long maxSize) {
        mDir = dir;
        mVersion = version;
        mMaxSize = maxSize;
    }

    private synchronized DiskLruCache getDiskLruCache() throws Exception {
        if (mDiskLruCache == null) {
            mDiskLruCache = DiskLruCache.open(mDir, mVersion, 1, mMaxSize);
        }
        return mDiskLruCache;
    }

    public void put(String cacheKey, InputStream in) {
        OutputStream out = null;
        try {
            DiskLruCache.Editor editor = getDiskLruCache().edit(cacheKey);
            if (editor != null) {
                byte[] buffer = new byte[1024];
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
        } finally {
            CommonUtil.closeQuietly(in);
            CommonUtil.closeQuietly(out);
        }
    }

    public InputStream get(String cacheKey) {
        try {
            DiskLruCache.Snapshot snapshot = getDiskLruCache().get(cacheKey);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void clear() {
        try {
            getDiskLruCache().delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // DiskLruCache.delete()调用之后，就无法使用了，置为null，下次用时需要重新初始化
            mDiskLruCache = null;
        }
    }
}
