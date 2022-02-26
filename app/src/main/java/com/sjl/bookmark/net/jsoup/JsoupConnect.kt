package com.sjl.bookmark.net.jsoup

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.regex.Pattern

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename JsoupConnect.java
 * @time 2018/12/8 13:38
 * @copyright(C) 2018 song
 */
class JsoupConnect private constructor(private val conn: Connection) {
    var timeOut = 15 * 1000
    fun timeout(time: Int): JsoupConnect {
        timeOut = time
        return this
    }

    @Throws(IOException::class)
    fun get(): Document {
        return conn.timeout(timeOut).get()
    }

    @Throws(IOException::class)
    fun post(): Document {
        return conn.timeout(timeOut).post()
    }

    companion object {
        fun connect(url: String?): JsoupConnect {
            return JsoupConnect(Jsoup.connect(url))
        }

        fun parse(html: String?): Document {
            return Jsoup.parse(html)
        }

        fun root(url: String?): String? {
            val p = Pattern.compile(
                "[a-zA-z]+://[^\\s'\"]*\\.[a-zA-Z]{2,6}",
                Pattern.CASE_INSENSITIVE
            )
            val matcher = p.matcher(url)
            if (matcher.find()) {
                return matcher.group()
            }
            try {
                throw Exception("没有找到网站根目录！")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun domain(url: String?): String? {
            val p = Pattern.compile(
                "^(http|https)://?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)",
                Pattern.CASE_INSENSITIVE
            )
            //	    Pattern.compile("[^\\s'\"./:]*.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
            try {
                val matcher = p.matcher(url)
                if (matcher.find()) return matcher.group()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}