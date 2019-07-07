package com.lx.lib.webviewcache.fetcher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2019/6/29.
 */
public final class DefaultHttpFetcher {
    public InputStream fetch(String method, String url) {
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod(method.toUpperCase());
            // 用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
//            conn.setRequestProperty("action", NETWORK_GET);
            // 禁用网络缓存
            conn.setUseCaches(false);
            // 在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            // conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法
            conn.connect();
//            if (WebViewCacheManager.get().isDebug()) {
//                String responseHeader = getResponseHeader(conn);
//                FLogger.i(WebViewCacheManager.TAG, "response: " + url + ", header: " + responseHeader);
//            }
            // 调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            InputStream is = conn.getInputStream();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (int i = 0; i < size; i++) {
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            builder.append(responseHeaderKey);
            builder.append(":");
            builder.append(responseHeaderValue);
            builder.append("\n");
        }
        return builder.toString();
    }
}
