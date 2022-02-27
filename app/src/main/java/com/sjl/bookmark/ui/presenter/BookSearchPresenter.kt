package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.api.ZhuiShuShenQiApi
import com.sjl.bookmark.entity.zhuishu.HotWordDto
import com.sjl.bookmark.entity.zhuishu.KeyWordDto
import com.sjl.bookmark.entity.zhuishu.SearchBookDto
import com.sjl.bookmark.entity.zhuishu.SearchBookDto.BooksBean
import com.sjl.bookmark.ui.contract.BookSearchContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchPresenter.java
 * @time 2018/11/30 16:58
 * @copyright(C) 2018 song
 */
class BookSearchPresenter : BookSearchContract.Presenter() {
    override fun searchHotWord() {
        val apiService: ZhuiShuShenQiApi = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        apiService.hotWordPackage.map(object : Function<HotWordDto, List<String>> {
            @Throws(Exception::class)
            override fun apply(hotWordDto: HotWordDto): List<String> {
                return hotWordDto.hotWords
            }
        }).compose(RxSchedulers.applySingle()).`as`(bindLifecycle())
            .subscribe(object : Consumer<List<String>> {
                @Throws(Exception::class)
                override fun accept(strings: List<String>) {
                    mView.finishHotWords(strings)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("获取搜索热词失败", throwable)
                }
            })
    }

    override fun searchKeyWord(query: String) {
        val apiService: ZhuiShuShenQiApi = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        apiService.getKeyWordPacakge(query).map(object : Function<KeyWordDto, List<String>> {
            @Throws(Exception::class)
            override fun apply(keyWordDto: KeyWordDto): List<String> {
                return keyWordDto.keywords
            }
        }).compose(RxSchedulers.applySingle()).`as`(bindLifecycle())
            .subscribe(object : Consumer<List<String>> {
                @Throws(Exception::class)
                override fun accept(strings: List<String>) {
                    LogUtils.i("关键字自动补全，匹配数量" + strings.size)
                    mView.finishKeyWords(strings)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("关键字自动补全，查询失败", throwable)
                }
            })
    }

    override fun searchBook(query: String?) {
        val apiService: ZhuiShuShenQiApi = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        apiService.getSearchBookPackage(query)
            .map(object : Function<SearchBookDto, List<BooksBean>> {
                @Throws(Exception::class)
                override fun apply(searchBookDto: SearchBookDto): List<BooksBean> {
                    return searchBookDto.books
                }
            }).compose(RxSchedulers.applySingle()).`as`(bindLifecycle())
            .subscribe(object : Consumer<List<BooksBean>> {
                @Throws(Exception::class)
                override fun accept(bean: List<BooksBean>) {
                    LogUtils.i("书籍查询，匹配数量" + bean.size)
                    mView.finishBooks(bean)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("书籍查询失败", throwable)
                    mView.errorBooks()
                }
            })
    }
}