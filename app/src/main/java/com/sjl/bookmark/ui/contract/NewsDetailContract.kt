package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhihu.NewsDetailDto
import com.sjl.bookmark.entity.zhihu.NewsExtraDto
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailContract.java
 * @time 2018/12/21 11:28
 * @copyright(C) 2018 song
 */
interface NewsDetailContract {
    interface View : IBaseView {
        /**
         * 新闻额外信息
         * @param newsExtra
         */
        fun showNewsExtra(newsExtra: NewsExtraDto)

        /**
         * 显示详情
         * @param newsDetail
         */
        fun showNewsDetail(newsDetail: NewsDetailDto)

        /**
         * 显示加载错误信息
         * @param errorMsg
         */
        fun showError(errorMsg: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 分享新闻
         * @param content
         * @param imgUrl
         */
        abstract fun shareNews(content: String, imgUrl: String)

        /**
         * 加载新闻额外信息
         * @param id
         */
        abstract fun loadNewsExtra(id: String?)

        /**
         * 加载新闻详情
         * @param id
         */
        abstract fun loadNewsDetail(id: String?)
    }
}