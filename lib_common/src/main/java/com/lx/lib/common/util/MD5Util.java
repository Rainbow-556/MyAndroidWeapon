package com.lx.lib.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Created by lixiang on 2019/6/30.
 */
public final class MD5Util {
    public static String md5(String content) {
        byte[] hash;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            hash = messageDigest.digest(content.getBytes());
        } catch (Exception e) {
            return "";
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toLowerCase();
    }

    public static String md5(File file) {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        try {
            // 创建MD5转换器和文件流
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, messageDigest);
            byte[] buffer = new byte[1024];
            // DigestInputStream实际上在流处理文件时就在内部就进行了一定的处理
            while (dis.read(buffer) > 0) {
            }
            // 通过DigestInputStream对象得到一个最终的MessageDigest对象。
            messageDigest = dis.getMessageDigest();
            // 通过messageDigest拿到结果，也是字节数组，包含16个元素
            byte[] array = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            StringBuilder hex = new StringBuilder(array.length * 2);
            for (byte b : array) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            CommonUtil.closeQuietly(fis);
            CommonUtil.closeQuietly(dis);
        }
    }
}
