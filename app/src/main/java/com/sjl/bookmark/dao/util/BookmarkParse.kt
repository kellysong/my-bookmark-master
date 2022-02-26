package com.sjl.bookmark.dao.util

import android.content.Context
import org.jsoup.Jsoup
import com.sjl.bookmark.entity.table.Bookmark
import com.sjl.core.util.SerializeUtils
import com.sjl.bookmark.dao.impl.BookmarkService
import android.text.TextUtils
import com.sjl.core.util.log.LogUtils
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.util.ArrayList

/**
 * 书签解析类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkParse.java
 * @time 2018/2/7 14:13
 * @copyright(C) 2018 song
 */
class BookmarkParse {
    /**
     * 读取书签文件
     *
     * @param context
     * @param fileName
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readBookmarkHtml(context: Context, fileName: String?) {
        val inputStream = context.assets.open(fileName)
        val str = inputStreamToStr(inputStream)
        val doc = Jsoup.parse(str)
        val bookmarkList: MutableList<Bookmark> = ArrayList()
        parseBookmarkHtml(bookmarkList, doc)
        LogUtils.i("解析结果集合大小：" + bookmarkList.size)
        SerializeUtils.serialize("bookmark", bookmarkList)
        val bookmarkService = BookmarkService.getInstance(context)
        bookmarkService.deleteAllBookmark()
        bookmarkService.saveBookmarkLists(bookmarkList)
    }

    /**
     * 更新书签
     *
     * @param context 上下文
     * @param file    书签文件
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun updateBookmark(context: Context?, file: File?): Boolean {
        return try {
            val inputStream: InputStream = FileInputStream(file)
            val str = inputStreamToStr(inputStream)
            val doc = Jsoup.parse(str)
            val bookmarkList: MutableList<Bookmark> = ArrayList()
            parseBookmarkHtml(bookmarkList, doc)
            LogUtils.i("本次更新书签集合大小：" + bookmarkList.size)
            val bookmarkService = BookmarkService.getInstance(context)
            bookmarkService.deleteAllBookmark()
            bookmarkService.saveBookmarkLists(bookmarkList)
            inputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 解析google浏览器书签算法
     *
     * @param bookmarkList
     * @param doc
     */
    private fun parseBookmarkHtml(bookmarkList: MutableList<Bookmark>, doc: Document) {
        val dls = doc.select("DL")
        var dlSize = dls.size
        LogUtils.i("dls节点个数：$dlSize") //89
        val h3s = doc.select("DT").select("H3")
        val h3Size = h3s.size
        LogUtils.i("h3节点个数：$h3Size") //88
        val bookmark: Bookmark? = null
        if (dlSize > 0) {
            dls.remove(dls[0])
            dlSize = dls.size
            //DT->H3和DL同级
            if (h3Size == dlSize) {
                for (i in 1 until h3Size) { // i= 1是为了过滤Bookmarks
                    val element = h3s[i]
                    val title = element.text()
                    val dl = dls[i]
                    val children = dl.children()
                    val existDl = children.select("DL")
                    if (existDl.`is`("DL")) { //还嵌套有dL
                        val iterator = children.iterator()
                        while (iterator.hasNext()) {
                            val next = iterator.next()
                            val tagName = next.tagName()
                            if (tagName == "p") {
                                iterator.remove()
                                continue
                            }
                            val finalSize = next.children().size
                            if (finalSize > 1) { //不是直接书签，即书签文件夹
                                iterator.remove()
                            }
                        }
                        val elements = children.select("A")
                        processBookmark(title, elements, bookmark, bookmarkList)
                        continue
                    } else { //直接书签
                        val filterDl = dl.select("DT > A")
                        processBookmark(title, filterDl, bookmark, bookmarkList)
                    }
                }
            } else {
                LogUtils.w("解析错误")
            }
        } else {
            LogUtils.w("没有相关数据")
        }
    }

    /**
     * 加工书签
     *
     * @param title
     * @param filterDl
     * @param bookmark
     * @param bookmarkList
     */
    private fun processBookmark(
        title: String,
        filterDl: Elements,
        bookmark: Bookmark?,
        bookmarkList: MutableList<Bookmark>
    ) {
        var bookmark = bookmark
        val filterDlSize = filterDl.size
        if (filterDlSize == 0) {
            return
        }
        // LogUtils.i("h3标题：" + title);
        // LogUtils.i("filterDl：" + filterDl.text());//查找某个父元素下的直接子元素
        var base64 = ""
        bookmark = Bookmark(0, title)
        bookmarkList.add(bookmark)
        for (j in 0 until filterDlSize) {
            val a = filterDl[j]
            val text = a.text()
            val hrefAttr = a.attr("HREF")
            val iconAttr = a.attr("ICON")
            if (!TextUtils.isEmpty(iconAttr)) {
                base64 = iconAttr.split(",").toTypedArray()[1]
            }
            bookmark = Bookmark(title, hrefAttr, base64, text)
            bookmarkList.add(bookmark)
        }
    }

    /**
     * 输入流转字符串
     *
     * @param in 输入流
     * @return
     */
    private fun inputStreamToStr(`in`: InputStream): String {
        val stringBuilder = StringBuilder()
        val bf = BufferedReader(InputStreamReader(`in`))
        var line: String?
        try {
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}