package com.sjl.bookmark.ui.contract

import android.content.Intent
import com.sjl.bookmark.entity.ExpressDetail
import com.sjl.bookmark.entity.ExpressSearchInfo
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailContract.java
 * @time 2018/11/26 11:19
 * @copyright(C) 2018 song
 */
interface ExpressDetailContract {
    interface View : IBaseView {
        /**
         * 显示快递来源
         * @param expressSearchInfo
         */
        fun showExpressSource(expressSearchInfo: ExpressSearchInfo)

        /**
         * 显示物流详情
         * @param expressDetail
         */
        fun showExpressDetail(expressDetail: ExpressDetail)

        /**
         * 显示错误信息
         */
        fun showErrorInfo()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(intent: Intent)

        /**
         * 获取快递备注信息
         *
         * @param postId
         * @return
         */
        abstract fun getExpressRemark(postId: String): String?

        /**
         * 查询快递明细
         *
         * @param searchInfo
         */
        abstract fun queryExpressDetail(searchInfo: ExpressSearchInfo)

        /**
         * 更新本地快递信息
         * @param searchInfo
         * @param expressDetail
         */
        abstract fun updateExpressDetail(searchInfo: ExpressSearchInfo, expressDetail: ExpressDetail?)

        /**
         * 判断本地是否缓存有快递信息
         * @param postId
         * @return
         */
        abstract fun checkExistExpress(postId: String): Boolean

        /**
         * 更新快递单备注信息
         * @param postId
         * @param remark
         */
        abstract fun updateExpressRemark(postId: String, remark: String)
    }
}