package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhihu.NewsList
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListContract.java
 * @time 2018/12/18 17:05
 * @copyright(C) 2018 song
 */
interface NewsListContract {
    interface View : IBaseView {
        /**
         * 刷新新闻列表
         * @param newsLists
         */
        fun refreshNewsList(newsLists: List<NewsList>)

        /**
         * 显示更多日报数据
         * @param newsLists
         */
        fun showMoreNewsList(newsLists: List<NewsList>)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 加载首页数据
         */
        abstract fun loadNews()

        /**
         * 上拉加载更多
         */
        abstract fun loadMore()

        /**
         * 下拉刷新
         */
        abstract fun refresh()
    }
}