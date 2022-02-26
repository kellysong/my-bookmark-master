package com.sjl.bookmark.dao.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网页浏览断点记录映射
 * <p>记录每篇文章的滑动记录</p>
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseMapper.java
 * @time 2018/4/1 9:49
 * @copyright(C) 2018 song
 */
public class BrowseMapper {
    private static Map<String, Integer> urls = new LinkedHashMap<String, Integer>();


    /**
     * 缓存当前网页记录点
     * @param url
     * @param progress
     */
    public static void put(String url, Integer progress) {
        urls.put(url, progress);
    }

    /**
     * 取出网页记录点
     * @param url
     * @return
     */
    public static int get(String url) {
        Integer integer = urls.get(url);
        if (integer == null) {
            return 0;
        } else {
            return integer;
        }
    }

    /**
     * 清空
     */
    public static void clearAll() {
        urls.clear();
    }
}
