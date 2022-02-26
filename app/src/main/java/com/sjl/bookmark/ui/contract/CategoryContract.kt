package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.Category
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryContract.java
 * @time 2018/11/26 15:42
 * @copyright(C) 2018 song
 */
interface CategoryContract {
    interface View : IBaseView {
        fun setCategory(categories: List<Category>)

        /**
         * 显示下拉加载进度
         */
        fun showLoading()
        fun showFail(message: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun loadCategoryData()

        /**
         * 下拉刷新
         */
        abstract fun refresh()
    }
}