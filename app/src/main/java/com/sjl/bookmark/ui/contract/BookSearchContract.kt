package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhuishu.SearchBookDto.BooksBean
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchContract.java
 * @time 2018/11/30 16:59
 * @copyright(C) 2018 song
 */
interface BookSearchContract {
    interface View : IBaseView {
        fun finishHotWords(hotWords: List<String>)
        fun finishKeyWords(keyWords: List<String>)
        fun finishBooks(books: List<BooksBean>)
        fun errorBooks()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun searchHotWord()

        /**
         * 搜索提示
         * @param query
         */
        abstract fun searchKeyWord(query: String)

        /**
         * 搜索书籍
         * @param query
         */
        abstract fun searchBook(query: String?)
    }
}