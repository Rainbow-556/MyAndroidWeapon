package com.lx.lib.common.util;

import android.util.Log;

/**
 * Created by lixiang on 2018/7/30.<br/>
 */
public final class FLogger {
    private static final String TAG_MSG = "CommonMsg";
    private static final String TAG_API_SERVICE = "ApiService";

    public static void logApiRequest(String msg) {
        Log.i(TAG_API_SERVICE, msg);
    }

    public static void d(Object msg) {
        d(TAG_MSG, String.valueOf(msg));
    }

    public static void d(String tag, Object msg) {
        Log.d(tag, String.valueOf(msg));
    }

    public static void w(Object msg) {
        w(TAG_MSG, String.valueOf(msg));
    }

    public static void w(String tag, Object msg) {
        Log.w(tag, String.valueOf(msg));
    }

    public static void i(Object msg) {
        i(TAG_MSG, String.valueOf(msg));
    }

    public static void i(String tag, Object msg) {
        Log.i(tag, String.valueOf(msg));
    }

    public static void e(Object msg) {
        e(TAG_MSG, String.valueOf(msg));
    }

    public static void e(String tag, Object msg) {
        Log.e(tag, String.valueOf(msg));
    }

    public static void logException(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
