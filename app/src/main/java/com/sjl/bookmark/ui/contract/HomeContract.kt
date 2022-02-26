package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.entity.TopBanner
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomeContract.java
 * @time 2018/11/26 15:24
 * @copyright(C) 2018 song
 */
interface HomeContract {
    interface View : IBaseView {
        /**
         * 显示下拉加载进度
         */
        fun showLoading()

        /**
         * 隐藏下拉加载进度
         */
        fun hideLoading()

        /**
         * 设置头部轮播图
         *
         * @param banners
         */
        fun setHomeBanners(banners: List<TopBanner>)

        /**
         * 设置文章列表
         *
         * @param article
         * @param loadType
         */
        fun setHomeArticles(article: Article, loadType: Int)
        fun collectArticleSuccess(position: Int, bean: DatasBean)
        fun showFaild(message: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 初始化首页数据
         */
        abstract fun loadHomeData()

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