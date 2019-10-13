package com.lx.lib.java;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.steadystate.css.dom.CSSFontFaceRuleImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by glennli on 2019/10/12.<br/>
 */
public final class MyParser {
    public static void parse(String url) {
        try {
            File dir = new File("D:\\Users\\glennli\\Desktop\\htmlData");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // HtmlUnit 模拟浏览器
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
            webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
            webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间
            HtmlPage htmlPage = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(30 * 1000);               // 等待js后台执行30秒

            // HtmlUnit解析
            // img
            final File imgDir = new File(dir, "img");
            FileUtil.deleteFile(imgDir);
            if (!imgDir.exists()) {
                imgDir.mkdirs();
            }
            List<DomElement> imgList = htmlPage.getElementsByTagName("img");
            if (imgList != null && !imgList.isEmpty()) {
                System.out.println("img HtmlUnit count=" + imgList.size());
                for (DomElement domElement : imgList) {
                    final String src = domElement.getAttribute("src");
                    if (!TextUtils.isEmpty(src)) {
                        System.out.println(src);
//                        ThreadPoolUtil.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                FileDownloader.download(src, imgDir);
//                            }
//                        });
                    }
                }
            } else {
                System.out.println("img HtmlUnit count=0");
            }
            System.out.println();

            // js
            final File jsDir = new File(dir, "js");
            FileUtil.deleteFile(jsDir);
            if (!jsDir.exists()) {
                jsDir.mkdirs();
            }
            DomNodeList<DomElement> scriptElements = htmlPage.getElementsByTagName("script");
            if (scriptElements != null && !scriptElements.isEmpty()) {
                System.out.println("script HtmlUnit count=" + scriptElements.size());
                for (DomElement domElement : scriptElements) {
                    final String src = domElement.getAttribute("src");
                    if (!TextUtils.isEmpty(src)) {
                        System.out.println(src);
//                        ThreadPoolUtil.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                FileDownloader.download(src, jsDir);
//                            }
//                        });
                    }
                }
            } else {
                System.out.println("script HtmlUnit count=0");
            }
            System.out.println();

            // css
            final File cssDir = new File(dir, "css");
            FileUtil.deleteFile(cssDir);
            if (!cssDir.exists()) {
                cssDir.mkdirs();
            }
            DomNodeList<DomElement> cssElements = htmlPage.getElementsByTagName("link");
            if (cssElements != null && !cssElements.isEmpty()) {
                System.out.println("css HtmlUnit count=" + cssElements.size());
                for (DomElement domElement : cssElements) {
                    if ("stylesheet".equalsIgnoreCase(domElement.getAttribute("rel"))) {
                        final String href = domElement.getAttribute("href");
                        if (!TextUtils.isEmpty(href)) {
                            System.out.println(href);
//                            ThreadPoolUtil.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    FileDownloader.download(href, cssDir);
//                                }
//                            });
                        }
                    }
                }
            } else {
                System.out.println("css HtmlUnit count=0");
            }
//            parseByJsoup(htmlPage.asXml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseImgInCSS(String file) {
        try {
            InputSource source = new InputSource(new FileReader(file));
            source.setEncoding("UTF-8");
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
            CSSRuleList rules = sheet.getCssRules();
            final String prefix = "url(", suffix = ")";
            for (int i = 0; i < rules.getLength(); i++) {
                final CSSRule rule = rules.item(i);
                if (rule instanceof CSSStyleRuleImpl) {
                    // 普通样式选择器，.box{}
                    final String cssText = rule.getCssText();
//                    System.out.println(cssText);

                    int start = cssText.indexOf(prefix);
                    if (start == -1) {
                        continue;
                    }
                    int temp = start + 1;
                    while (true) {
                        if (suffix.equals(String.valueOf(cssText.charAt(temp)))) {
                            break;
                        }
                        temp++;
                    }
                    String imgUrl = cssText.substring(start + prefix.length(), temp);
                    if (imgUrl.endsWith(".png") || imgUrl.endsWith(".jpg")
                            || imgUrl.endsWith(".jpeg") || imgUrl.endsWith(".gif") || imgUrl.endsWith(".webp")) {
                        System.out.println(imgUrl);
                    }
                } else if (rule instanceof CSSFontFaceRuleImpl) {
                    // @font-face{}
//                    System.out.println(rule.getCssText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseByJsoup(String html) {
        Document doc = Jsoup.parse(html);
//            System.out.println("doc------------");
//            System.out.println(doc.toString());
        // 获取所有图片元素集
//            Elements imgs = doc.select("img[src$=.png]");
        // 获取所有图片元素集
        StringBuilder imgUrls_2 = new StringBuilder();
        Elements imgTags = doc.select("img[src]");
        // 此处省略其他操作
        if (imgTags != null && !imgTags.isEmpty()) {
            System.out.println("img Jsoup count=" + imgTags.size());
            String src;
            for (Element e : imgTags) {
                src = e.attr("src");
                if (!TextUtils.isEmpty(src)) {
                    System.out.println(src);
                    imgUrls_2.append(src);
                }
            }
        } else {
            System.out.println("img Jsoup------------count=0");
        }
        return imgUrls_2.toString();
    }
}
