package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.table.HistoryExpress
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressContract.java
 * @time 2018/11/26 11:06
 * @copyright(C) 2018 song
 */
interface ExpressContract {
    interface View : IBaseView {
        fun setHistoryExpress(historyExpresses: List<HistoryExpress>)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 获取未验收的快递
         */
        abstract  fun getUnCheckList()

        /**
         * 获取所有历史快递，包括已验收和未验收
         */
        abstract fun getHistoryExpresses()
    }
}