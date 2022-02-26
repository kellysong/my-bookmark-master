package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * 书架Contract
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfContract.java
 * @time 2018/11/30 14:39
 * @copyright(C) 2018 song
 */
interface BookShelfContract {
    interface View : IBaseView {
        fun showErrorMsg(msg: String)
        fun showRecommendBook(collBookBeans: List<CollectBook>)

        /**
         * 删除刷新图书
         */
        fun refreshBook()
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 联网获取最新图书，并集合本地收藏图书进行刷新
         */
        abstract fun refreshCollectBooks()

        /**
         * 从本地获取收藏的图书放进书架
         */
        abstract fun getRecommendBook()
        abstract fun deleteBook(collectBook: CollectBook)

        /**
         * 删除所有书籍
         */
        abstract fun deleteAllBook()
    }
}