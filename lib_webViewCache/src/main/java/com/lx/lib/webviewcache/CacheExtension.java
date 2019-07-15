package com.lx.lib.webviewcache;

import android.text.TextUtils;

import java.util.HashSet;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class CacheExtension {
    // 图片资源
    public static final String PNG = "png";
    public static final String WEBP = "webp";
    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String GIF = "gif";
    public static final String ICO = "ico";
    // 代码
    public static final String JS = "js";
    public static final String CSS = "css";
    public static final String JSON = "json";
    public static final String HTML = "html";
    public static final String HTM = "htm";
    // 字体
    public static final String TTF = "ttf";
    public static final String WOFF = "woff";

    private HashSet<String> cacheExtensions = new HashSet<>(15);

    CacheExtension() {
        cacheExtensions.add(PNG);
        cacheExtensions.add(WEBP);
        cacheExtensions.add(JPG);
        cacheExtensions.add(JPEG);
        cacheExtensions.add(GIF);
        cacheExtensions.add(JS);
        cacheExtensions.add(CSS);
        cacheExtensions.add(ICO);
        cacheExtensions.add(TTF);
        cacheExtensions.add(WOFF);
    }

    public void add(String extension) {
        if (!TextUtils.isEmpty(extension)) {
            cacheExtensions.add(extension.toLowerCase());
        }
    }

    public void remove(String extension) {
        if (!TextUtils.isEmpty(extension)) {
            cacheExtensions.remove(extension.toLowerCase());
        }
    }

    public boolean contains(String extension) {
        return cacheExtensions.contains(extension);
    }
}
