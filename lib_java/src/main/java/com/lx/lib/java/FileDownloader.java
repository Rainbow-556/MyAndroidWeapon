package com.lx.lib.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by glennli on 2019/10/12.<br/>
 */
public final class FileDownloader {
    private static SSLSocketFactory sslSocketFactory;

    public static boolean download(String url, File dir) {
        if (url.startsWith("//")) {
            url = "https:" + url;
        }
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 禁用网络缓存
            conn.setUseCaches(false);
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
            // 调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            InputStream inputStream = conn.getInputStream();
            String fileName = URLEncoder.encode(url);
            FileOutputStream outputStream = new FileOutputStream(new File(dir, fileName));
            byte[] buffer = new byte[4096];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, c);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getString(String url) {
        if (url.startsWith("//")) {
            url = "https:" + url;
        }
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 禁用网络缓存
            conn.setUseCaches(false);
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
            // 调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            InputStream inputStream = conn.getInputStream();
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, c);
            }
            inputStream.close();
            byteOutputStream.close();
            return new String(byteOutputStream.toByteArray(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

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
