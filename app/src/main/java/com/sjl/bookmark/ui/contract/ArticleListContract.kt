package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.Article
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListContract.java
 * @time 2018/11/26 15:32
 * @copyright(C) 2018 song
 */
interface ArticleListContract {
    interface View : IBaseView {
        fun setCategoryArticles(article: Article, loadType: Int)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun loadMore()
        abstract fun refresh()
    }
}