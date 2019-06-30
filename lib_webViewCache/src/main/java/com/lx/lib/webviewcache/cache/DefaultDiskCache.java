package com.lx.lib.webviewcache.cache;

import com.lx.lib.common.util.CommonUtil;
import com.lx.lib.common.util.FLogger;
import com.lx.lib.common.util.disklrucache.DiskLruCache;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class DefaultDiskCache {
    private DiskLruCache mDiskLruCache;
    private boolean isValid;

    public DefaultDiskCache(File dir, int version, long maxSize) {
        try {
            mDiskLruCache = DiskLruCache.open(dir, version, 1, maxSize);
            isValid = true;
        } catch (Exception e) {
            e.printStackTrace();
            FLogger.e("DefaultDiskCache init fail!");
        }
    }

    public void put(String cacheKey, InputStream in) {
        if (!isValid) {
            return;
        }
        OutputStream out = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(cacheKey);
            if (editor != null) {
                byte[] buffer = new byte[1024];
                out = editor.newOutputStream(0);
                int count;
                while ((count = in.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                editor.commit();
            }
            mDiskLruCache.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonUtil.closeQuietly(in);
            CommonUtil.closeQuietly(out);
        }
    }

    public InputStream get(String cacheKey) {
        if (!isValid) {
            return null;
        }
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(cacheKey);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
