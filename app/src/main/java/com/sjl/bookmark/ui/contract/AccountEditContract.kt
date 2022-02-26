package com.sjl.bookmark.ui.contract

import android.content.Intent
import android.util.ArrayMap
import com.sjl.bookmark.entity.table.Account
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * 改良mvp模式
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountEditContract.java
 * @time 2018/11/26 10:16
 * @copyright(C) 2018 song
 */
interface AccountEditContract {
    interface View : IBaseView {
        fun initSpinner(data: ArrayMap<String, List<String>>)
        fun initCreateModel(position: Int)
        fun initViewModel(account: Account?)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(intent: Intent)

        /**
         * 新增或者修改账号信息
         *
         * @param account
         * @return 0修改, 1新增
         */
        abstract fun saveAccount(account: Account): Long

        /**
         * 删除账号信息
         */
        abstract fun deleteAccount()
    }
}