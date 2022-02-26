package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhuishu.BookDetailDto.BookDetail
import com.sjl.bookmark.entity.zhuishu.HotCommentDto.HotComment
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailContract.java
 * @time 2018/12/7 10:09
 * @copyright(C) 2018 song
 */
interface BookDetailContract {
    interface View : IBaseView {
        fun finishRefresh(bookDetail: BookDetail)
        fun finishHotComment(hotCommentList: List<HotComment>)
        fun finishRecommendBookList(recommendBookList: List<RecommendBook>)
        fun waitToBookShelf()
        fun errorToBookShelf()
        fun succeedToBookShelf()
        fun showError()
        fun complete()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun refreshBookDetail(bookId: String)

        /**
         * 添加到书架上
         *
         * @param collectBook
         */
        abstract fun addToBookShelf(collectBook: CollectBook)
    }
}