package com.lx.lib.webviewcache.util;

import android.net.Uri;
import android.text.TextUtils;

import com.lx.lib.webviewcache.CacheExtension;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class MimeTypeUtil {
    public static String getMimeTypeFromUrl(Uri uri) {
        final String extension = getExtensionFromUrl(uri);
        switch (extension) {
            case CacheExtension.PNG:
                return "image/png";
            case CacheExtension.JPG:
            case CacheExtension.JPEG:
                return "image/jpeg";
            case CacheExtension.WEBP:
                return "image/webp";
            case CacheExtension.GIF:
                return "image/gif";
            case CacheExtension.ICO:
                return "image/x-icon";
            case CacheExtension.JS:
                return "application/javascript";
            case CacheExtension.CSS:
                return "text/css";
            case CacheExtension.HTML:
            case CacheExtension.HTM:
                return "text/html";
            case CacheExtension.JSON:
                return "application/json";
            case CacheExtension.TTF:
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
