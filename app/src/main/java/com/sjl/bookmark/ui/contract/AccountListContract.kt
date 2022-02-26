package com.sjl.bookmark.ui.contract

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjl.bookmark.entity.table.Account
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListContract.java
 * @time 2018/11/26 14:53
 * @copyright(C) 2018 song
 */
interface AccountListContract {
    interface View : IBaseView {
        fun initRecycler(
            linearLayoutManager: LinearLayoutManager,
            adapter: RecyclerView.Adapter<*>
        )

        fun readGo(clazz: Class<*>, operateFlag: Int, account: Account)
        fun hideEmptyView()
        fun showEmptyView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun setPosition(position: Int)

        /**
         * 下拉刷新
         */
        abstract fun pullRefreshDown()
        abstract fun onEventComing(eventBusDto: EventBusDto<*>)
    }
}