package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.app.MyApplication
import com.sjl.bookmark.dao.impl.HistoryExpressService
import com.sjl.bookmark.ui.contract.ExpressContract

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressPresenter.java
 * @time 2018/4/26 15:36
 * @copyright(C) 2018 song
 */
class ExpressPresenter : ExpressContract.Presenter() {
    private val historyExpressService: HistoryExpressService

    /**
     * 获取未验收的快递
     */
    override fun getUnCheckList() {
        val unCheckList = historyExpressService.unCheckList
        mView.setHistoryExpress(unCheckList)
    }

    /**
     * 获取所有历史快递，包括已验收和未验收
     */
    override fun getHistoryExpresses() {
        val historyExpresses = historyExpressService.queryHistoryExpresses()
        mView.setHistoryExpress(historyExpresses)
    }

    init {
        historyExpressService = HistoryExpressService(MyApplication.getContext())
    }
}