package com.sjl.bookmark.ui.contract

import android.content.Intent
import com.sjl.bookmark.entity.table.Bookmark
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkContract.java
 * @time 2018/11/26 10:59
 * @copyright(C) 2018 song
 */
interface BookmarkContract {
    interface View : IBaseView {
        fun showBookmarkData(bookmarks: List<Bookmark>?, loadingState: Int)
        fun setItemMenuVisible(visible: Boolean)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(intent: Intent)

        /**
         * 初始化数据
         * @param sourceFile
         */
        abstract fun initBookmarkList(sourceFile:String): List<Bookmark>

        /**
         * 上拉加载
         */
        abstract fun pullRefreshUp()

        /**
         * 下拉刷新
         */
        abstract fun pullRefreshDown()
    }
}