package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.WifiInfo
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename WifiQueryContract.java
 * @time 2018/11/26 10:41
 * @copyright(C) 2018 song
 */
interface WifiQueryContract {
    interface View : IBaseView {
        /**
         * 显示wifi信息
         * @param wifiInfos
         */
        fun showWifiInfo(wifiInfos: List<WifiInfo>)
    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 初始化wifi信息
         */
        abstract fun initWifiInfo()

        /**
         * 复制wifi密码到粘贴板
         * @param password
         */
        abstract fun copyWifiPassword(password: String)

        /**
         * 连接wifi信息
         * @param wifiInfo
         */
        abstract fun connectWifi(wifiInfo: WifiInfo)
    }
}