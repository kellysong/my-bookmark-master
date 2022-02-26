package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.ExpressCompany
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyContract.java
 * @time 2018/11/26 11:14
 * @copyright(C) 2018 song
 */
interface ExpressCompanyContract {
    interface View : IBaseView
    abstract class Presenter : BasePresenter<View>() {
        /**
         * 不建议接口有返回值，尽量逻辑在Presenter层处理
         * @return
         */
        abstract fun initCompany(): List<ExpressCompany>
    }
}