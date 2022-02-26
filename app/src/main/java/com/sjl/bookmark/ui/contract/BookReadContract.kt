package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhuishu.table.BookChapter
import com.sjl.bookmark.widget.reader.bean.TxtChapter
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookReadContract.java
 * @time 2018/12/4 15:51
 * @copyright(C) 2018 song
 */
interface BookReadContract {
    interface View : IBaseView {
        fun showCategory(bookChapterList: List<BookChapter>)
        fun finishChapter()
        fun errorChapter()
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 加载书籍章节目录
         * @param bookId
         */
        abstract fun loadCategory(bookId: String)

        /**
         * 加载章节内容
         * @param bookId
         * @param bookChapterList
         */
        abstract fun loadChapter(bookId: String, bookChapterList: List<TxtChapter>)
    }
}