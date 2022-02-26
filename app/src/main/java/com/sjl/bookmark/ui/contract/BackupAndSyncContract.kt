package com.sjl.bookmark.ui.contract

import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BackupAndSyncContract.java
 * @time 2018/11/26 16:13
 * @copyright(C) 2018 song
 */
interface BackupAndSyncContract {
    /**
     * 备份与同步view
     *
     *
     */
    interface View : IBaseView {
        /**
         * 显示加载框
         * @param msg 提示信息
         */
        fun showLoading(msg: String)

        /**
         * 隐藏加载框
         * @param errorMsg
         */
        fun hideLoading(errorMsg: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun setClickPreferenceKey(key: String)
        abstract override fun init()
    }
}