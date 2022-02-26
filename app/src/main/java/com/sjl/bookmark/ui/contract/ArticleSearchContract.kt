package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.entity.HotKey
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchContract.java
 * @time 2018/11/26 15:16
 * @copyright(C) 2018 song
 */
interface ArticleSearchContract {
    interface View : IBaseView {
        fun getHotKeySuccess(data: List<HotKey>)
        fun searchDataSuccess(data: List<DatasBean>)
        fun loadMoreDataSuccess(data: List<DatasBean>)
        fun showFailMsg(message: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 获取热门搜索关键字
         */
        abstract fun getHotKeyData()

        /**
         * 根据关键字搜索
         * @param key
         */
        abstract fun searchData(key: String)

        /**
         * 上拉加载更多数据
         * @param keyWord
         */
        abstract fun getMoreData(keyWord: String)
    }
}