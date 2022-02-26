package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.app.MyApplication
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter
import org.greenrobot.eventbus.EventBus

/**
 * 改良mvp模式
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AboutContract.java
 * @time 2018/11/26 9:50
 * @copyright(C) 2018 song
 */
interface AboutContract {
    interface View : IBaseView {
        /**
         * 显示当前app版本
         *
         * @param version app版本
         */
        fun showCurrentVersion(version: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun getCurrentVersion()
    }
}