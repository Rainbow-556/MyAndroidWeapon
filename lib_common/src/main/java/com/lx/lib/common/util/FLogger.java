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
        Log.d(TAG_MSG, "" + msg);
    }

    public static void w(Object msg) {
        Log.w(TAG_MSG, "" + msg);
    }

    public static void i(Object msg) {
        Log.i(TAG_MSG, "" + msg);
    }

    public static void e(Object msg) {
        Log.e(TAG_MSG, "" + msg);
    }

    public static void logException(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
