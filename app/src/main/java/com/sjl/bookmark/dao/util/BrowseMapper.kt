package com.sjl.bookmark.dao.util

import java.util.LinkedHashMap

/**
 * 网页浏览断点记录映射
 *
 * 记录每篇文章的滑动记录
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseMapper.java
 * @time 2018/4/1 9:49
 * @copyright(C) 2018 song
 */
object BrowseMapper {
    private val urls: MutableMap<String, Int> = LinkedHashMap()

    /**
     * 缓存当前网页记录点
     * @param url
     * @param progress
     */
    fun put(url: String, progress: Int) {
        urls[url] = progress
    }

    /**
     * 取出网页记录点
     * @param url
     * @return
     */
    operator fun get(url: String?): Int {
        val integer = urls[url]
        return integer ?: 0
    }

    /**
     * 清空
     */
    fun clearAll() {
        urls.clear()
    }
}