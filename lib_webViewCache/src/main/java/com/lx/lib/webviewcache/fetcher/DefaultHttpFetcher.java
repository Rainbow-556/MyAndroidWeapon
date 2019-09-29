package com.lx.lib.webviewcache.fetcher;

import com.lx.lib.common.util.FLogger;
import com.lx.lib.webviewcache.WebViewCacheManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 禁用网络缓存
            conn.setUseCaches(false);
            if (WebViewCacheManager.get().isDebug()) {
                if (conn instanceof HttpsURLConnection) {
                    // 信任所有证书，否则链接抓包代理时会报证书不信任异常
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                    SSLSocketFactory sslSocketFactory = getUnsafeSSLSocketFactory();
                    if (sslSocketFactory != null) {
                        httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                    }
                }
            }
//            if (WebViewCacheManager.get().isDebug()) {
//                String responseHeader = getResponseHeader(conn);
//                FLogger.i(WebViewCacheManager.TAG, "response: " + url + ", header: " + responseHeader);
//            }
            // 调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            return conn.getInputStream();
        } catch (Exception e) {
//            e.printStackTrace();
            if (WebViewCacheManager.get().isDebug()) {
                FLogger.e(WebViewCacheManager.TAG, "DefaultHttpFetcher err:" + url + "\n" + e.getMessage());
            }
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

    private static SSLSocketFactory sslSocketFactory;

    private static synchronized SSLSocketFactory getUnsafeSSLSocketFactory() {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        }
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }
}
