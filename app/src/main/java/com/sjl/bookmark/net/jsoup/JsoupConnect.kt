package com.sjl.bookmark.net.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename JsoupConnect.java
 * @time 2018/12/8 13:38
 * @copyright(C) 2018 song
 */
public class JsoupConnect {
    public int timeOut = 15 * 1000;
    private Connection conn;

    private JsoupConnect(Connection conn) {
        this.conn = conn;
    }

    public static JsoupConnect connect(String url) {
        return new JsoupConnect(Jsoup.connect(url));
    }

    public JsoupConnect timeout(int time) {
        timeOut = time;
        return this;
    }

    public Document get() throws IOException {
        return conn.timeout(timeOut).get();
    }

    public Document post() throws IOException {
        return conn.timeout(timeOut).post();

    }

    public static Document parse(String html) {
        return Jsoup.parse(html);
    }


    public static String root(String url) {
        Pattern p =
                Pattern.compile("[a-zA-z]+://[^\\s'\"]*\\.[a-zA-Z]{2,6}",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            String s = matcher.group();
            return s;
        }

        try {
            throw new Exception("没有找到网站根目录！");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String domain(String url) {
        Pattern p =
                Pattern.compile("^(http|https)://?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)", Pattern.CASE_INSENSITIVE);
//	    Pattern.compile("[^\\s'\"./:]*.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        try {
            Matcher matcher = p.matcher(url);
            if (matcher.find()) return matcher.group();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
