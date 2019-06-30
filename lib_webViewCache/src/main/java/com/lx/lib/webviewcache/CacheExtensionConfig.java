package com.lx.lib.webviewcache;

import java.util.HashSet;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class CacheExtensionConfig {
    public static final String PNG = "png";
    public static final String JS = "js";
    public static final String CSS = "css";
    private HashSet<String> cacheExtensions = new HashSet<>(10);

    public CacheExtensionConfig() {
        cacheExtensions.add(PNG);
        cacheExtensions.add(JS);
        cacheExtensions.add(CSS);
    }

    public CacheExtensionConfig add(String extension) {
        cacheExtensions.add(extension.toLowerCase());
        return this;
    }

    public CacheExtensionConfig remove(String extension) {
        cacheExtensions.remove(extension.toLowerCase());
        return this;
    }

    public boolean contains(String extension) {
        return cacheExtensions.contains(extension);
    }
}
