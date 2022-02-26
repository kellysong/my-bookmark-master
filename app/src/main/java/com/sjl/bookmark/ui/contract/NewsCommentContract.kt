package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.zhihu.NewsCommentDto
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentContract.java
 * @time 2018/12/24 15:44
 * @copyright(C) 2018 song
 */
interface NewsCommentContract {
    interface View : IBaseView {
        /**
         * 显示新闻评论
         * @param newsCommentDto
         */
        fun showNewsComment(newsCommentDto: NewsCommentDto)

        /**
         * 显示加载错误信息
         * @param errorMsg
         */
        fun showError(errorMsg: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 加载长评论
         * @param id
         */
        abstract fun loadLongComment(id: String)

        /**
         * 加载短评论
         * @param id
         */
        abstract fun loadShortComment(id: String)
    }
}