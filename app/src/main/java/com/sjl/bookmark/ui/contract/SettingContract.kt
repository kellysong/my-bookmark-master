package com.sjl.bookmark.ui.contract

import android.content.Intent
import android.preference.Preference
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingContract.java
 * @time 2018/11/26 16:29
 * @copyright(C) 2018 song
 */
interface SettingContract {
    interface View : IBaseView {
        fun openChangeThemeActivity()
        fun readyGo(clazz: Class<*>, intent: Intent)

        /**
         * 显示加载框
         *
         * @param type 0进度条对话框，1圆形加载对话框
         * @param msg  提示信息
         */
        fun showLoading(type: Int, msg: String)

        /**
         * 隐藏加载框
         *
         * @param ret
         * @param errorMsg
         */
        fun hideLoading(ret: Boolean, errorMsg: String)
        fun update(percent: Int)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract override fun init()
        abstract fun setClickPreferenceKey(preference: Preference, key: String)
        abstract fun resetGestureFlag()
    }
}