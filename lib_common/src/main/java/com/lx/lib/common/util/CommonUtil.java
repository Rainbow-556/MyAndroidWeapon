package com.lx.lib.common.util;

import android.text.TextUtils;

import java.io.Closeable;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class CommonUtil {
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String safeString(String str, String defaultStr) {
        return TextUtils.isEmpty(str) ? defaultStr : str;
    }
}
