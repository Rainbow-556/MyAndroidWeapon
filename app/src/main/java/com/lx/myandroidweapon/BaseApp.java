package com.lx.myandroidweapon;

import android.app.Application;
import android.content.Context;

import com.lx.lib.webviewcache.WebViewCacheManager;

import java.io.File;

/**
 * Created by glennli on 2019/7/5.<br/>
 */
public final class BaseApp extends Application {
    private static BaseApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initWebViewCache(this);
    }

    private void initWebViewCache(Context context) {
        WebViewCacheManager.Config config = new WebViewCacheManager.Config();
        config.cacheDirPath(getExternalCacheDir() + File.separator + "LXWebViewCache");
        WebViewCacheManager.get().init(context, config);
    }

    public BaseApp get() {
        return sInstance;
    }
}
