package com.lx.lib.java;

import java.io.File;
import java.util.ArrayList;

public class ParseHtml {
    public static void main(String[] args) {
        System.out.println("main run");
        final File dir = new File("D:\\Users\\glennli\\Desktop\\htmlData");
        FileUtil.deleteFile(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ArrayList<String> urlList = new ArrayList<>();
        urlList.add("https://m.leka.club/download/app.html");
        urlList.add("https://m.leka.club/follow_wechat/index.html");
        urlList.add("https://vip.m.fenqile.com/club/index.html");
        urlList.add("https://m.leka.club/childrens_day_activity/index.html");
        for (final String url : urlList) {
            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    MyParser.parse(url, dir);
                }
            });
        }
    }
}
