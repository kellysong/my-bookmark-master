package com.sjl.bookmark.dao.util

import com.sjl.bookmark.entity.zhuishu.table.RecommendBook
import com.sjl.bookmark.net.jsoup.JsoupConnect
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import org.jsoup.nodes.Document
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * 追书神器网页解析器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ZhuiShuParse.java
 * @time 2018/12/8 12:31
 * @copyright(C) 2018 song
 */
object ZhuiShuParse {
    /**
     * 解析推荐书籍，主题：喜欢这本书的也喜欢
     *
     *
     *
     *  *
     * [
 * <img src="http://statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F602085%2F602085_fc24cf7c0bc3427e8902fa64f0043b49.jpg%2F" alt="逆天邪神"></img>
](/book/542a5838a5ae10f815039a7f) * <div class="info">
     * <h4>逆天邪神</h4>
     *
     * 火星引力
    </div> *
     *
     *
     *
     *
     * @param bookId
     * @param url
     * @return
     */
    @Deprecated("")
    @Throws(Exception::class)
    fun parseRecommendBook(bookId: String?, url: String): List<RecommendBook> {
        LogUtils.i("喜欢这本书的也喜欢:$url")
        val bookLists: MutableList<RecommendBook> = ArrayList()
        val doc: Document
        doc = try {
            JsoupConnect.connect(url).get()
        } catch (e: IOException) {
            return bookLists
        }
        val elements = doc.getElementsByAttributeValue("class", "recommend-list")
        val aClass = doc.getElementsByAttributeValue("class", "content c-book-column-list")
        if (elements == null || elements.isEmpty()) {
            return bookLists
        }
        val ul = elements[0]
        val children = ul.children()
        val updateTime = TimeUtils.formatDateToStr(Date(), TimeUtils.DATE_FORMAT_1)
        for (i in children.indices) {
            val element = children[i]
            val recommendBook = RecommendBook()
            val href = element.select("a").attr("href")
            recommendBook.recommendId = href.substring(href.lastIndexOf("/") + 1)
            recommendBook.cover = element.select("img").attr("src")
            recommendBook.title = element.select("h4").text()
            recommendBook.author = element.getElementsByAttributeValue("class", "author")[0].text()
            recommendBook.bookId = bookId
            recommendBook.updateTime = updateTime
            bookLists.add(recommendBook)
        }
        return bookLists
    }

    /**
     * 解析推荐书籍，主题：喜欢这本书的也喜欢
     * <div class="content c-book-column-list">
     * [
 * <img src="//statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F863874%2F863874_915f1b4bbac541b49864cf70ae63270c.jpg%2F" alt="邪帝狂后：废材九小姐"></img>
 * <span>邪帝狂后：废材九小姐</span>
](/book/56ce9d06363f92a0072738dc?exposure=1006%23%231000%7C56ce9d06363f92a0072738dc%23%23%E9%82%AA%E5%B8%9D%E7%8B%82%E5%90%8E%EF%BC%9A%E5%BA%9F%E6%9D%90%E4%B9%9D%E5%B0%8F%E5%A7%90%23%23%E4%BD%A0%E5%8F%AF%E8%83%BD%E6%84%9F%E5%85%B4%E8%B6%A3%E7%9A%84%23%230%23%23-1%23%23-1%23%231) *
     *
     *
     * [<img src="//statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F1422444%2F1422444_82ede7ad95414c05860e29a2fdcc134e.jpg%2F" alt="天下第一妃"></img> <span>天下第一妃</span>](/book/5885d29f27ed60977b8f12e3?exposure=1006%23%231000%7C5885d29f27ed60977b8f12e3%23%23%E5%A4%A9%E4%B8%8B%E7%AC%AC%E4%B8%80%E5%A6%83%23%23%E4%BD%A0%E5%8F%AF%E8%83%BD%E6%84%9F%E5%85%B4%E8%B6%A3%E7%9A%84%23%230%23%23-1%23%23-1%23%232) [<img src="//statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F601770%2F601770_518c59e992454f63b952349906985a96.jpg%2F" alt="暗帝绝宠：废柴傲娇妻"></img> <span>暗帝绝宠：废柴傲娇妻</span>](/book/55b87d8e2c2986b91720bb9b?exposure=1006%23%231000%7C55b87d8e2c2986b91720bb9b%23%23%E6%9A%97%E5%B8%9D%E7%BB%9D%E5%AE%A0%EF%BC%9A%E5%BA%9F%E6%9F%B4%E5%82%B2%E5%A8%87%E5%A6%BB%23%23%E4%BD%A0%E5%8F%AF%E8%83%BD%E6%84%9F%E5%85%B4%E8%B6%A3%E7%9A%84%23%230%23%23-1%23%23-1%23%233) [<img src="//statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F1422313%2F1422313_258c85927c6246b09f7ec1d04934505e.jpg%2F" alt="妖娆炼丹师"></img> <span>妖娆炼丹师</span>](/book/5885d34a2f66b8be5844e615?exposure=1006%23%231000%7C5885d34a2f66b8be5844e615%23%23%E5%A6%96%E5%A8%86%E7%82%BC%E4%B8%B9%E5%B8%88%23%23%E4%BD%A0%E5%8F%AF%E8%83%BD%E6%84%9F%E5%85%B4%E8%B6%A3%E7%9A%84%23%230%23%23-1%23%23-1%23%234)</div>
     *
     * @param bookId
     * @param url
     * @return
     */
    @Throws(Exception::class)
    fun parseRecommendBook2(bookId: String?, url: String): List<RecommendBook> {
        LogUtils.i("喜欢这本书的也喜欢:$url")
        val bookLists: MutableList<RecommendBook> = ArrayList()
        val doc: Document
        doc = try {
            JsoupConnect.connect(url).get()
        } catch (e: IOException) {
            return bookLists
        }
        val elements = doc.getElementsByAttributeValue("class", "content c-book-column-list")
        if (elements == null || elements.isEmpty()) {
            return bookLists
        }
        val ul = elements[0]
        val children = ul.children()
        val updateTime = TimeUtils.formatDateToStr(Date(), TimeUtils.DATE_FORMAT_1)
        for (i in children.indices) {
            val element = children[i]
            val recommendBook = RecommendBook()
            val href = element.attr(":href")
            recommendBook.recommendId = href.substring(href.lastIndexOf("/") + 1, href.indexOf("?"))
            //https://statics.zhuishushenqi.com/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F2241600%2F2241600_0988a87ee4914feabe3a81620ad9b22a.png%2F
            recommendBook.cover = "https:" + element.select("img").attr("src")
            recommendBook.title = element.select("span").text()
            recommendBook.author = "--"
            recommendBook.bookId = bookId
            recommendBook.updateTime = updateTime
            bookLists.add(recommendBook)
        }
        return bookLists
    }
}