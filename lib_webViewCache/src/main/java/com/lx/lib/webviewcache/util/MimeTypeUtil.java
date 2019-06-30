package com.lx.lib.webviewcache.util;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class MimeTypeUtil {
    public static String getMimeTypeFromUrl(Uri uri) {
        final String extension = getExtensionFromUrl(uri);
        switch (extension) {
            case "png":
                return "image/png";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "webp":
                return "image/webp";
            case "gif":
                return "image/gif";
            case "ico":
                return "image/x-icon";
            case "js":
                return "application/javascript";
            case "css":
                return "text/css";
            case "html":
            case "htm":
                return "text/html";
            case "json":
                return "application/json";
            case "ttf":
                return "font/ttf";
            default:
                return "";
        }
    }

    public static String getExtensionFromUrl(Uri uri) {
        String segment = uri.getLastPathSegment();
        if (TextUtils.isEmpty(segment)) {
            return "";
        }
        int index = segment.lastIndexOf(".");
        int len;
        if (index <= 0 || index == (len = segment.length()) - 1) {
            return "";
        }
        return segment.substring(index + 1, len).toLowerCase();
    }
}
