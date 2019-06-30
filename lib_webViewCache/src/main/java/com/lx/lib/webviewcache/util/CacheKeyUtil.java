package com.lx.lib.webviewcache.util;

import com.lx.lib.common.util.MD5Util;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class CacheKeyUtil {
    public static String generateKey(String str) {
        return MD5Util.md5(str);
    }
}
