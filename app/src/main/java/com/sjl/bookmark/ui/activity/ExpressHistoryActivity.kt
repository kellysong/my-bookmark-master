package com.sjl.bookmark.ui.activity

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.HistoryExpress
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.HistoryExpressAdapter
import com.sjl.bookmark.ui.contract.ExpressContract
import com.sjl.bookmark.ui.presenter.ExpressPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.AppUtils
import kotlinx.android.synthetic.main.express_history_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * 快递历史记录
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressHistoryActivity.java
 * @time 2018/5/2 18:09
 * @copyright(C) 2018 song
 */
class ExpressHistoryActivity : BaseActivity<ExpressPresenter>(),
    ExpressContract.View {

    private val historyExpresses: MutableList<HistoryExpress> = ArrayList()
    private lateinit var historyExpressAdapter: HistoryExpressAdapter
    override fun getLayoutId(): Int {
        return R.layout.express_history_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_history_record))
    }

    override fun initData() {
        historyExpressAdapter =
            HistoryExpressAdapter(this, R.layout.history_express_recycle_item, historyExpresses)
        rv_history_list.layoutManager = LinearLayoutManager(this)
        rv_history_list.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        rv_history_list.adapter = historyExpressAdapter
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getHistoryExpresses()
    }

    override fun setHistoryExpress(historyExpresses: List<HistoryExpress>) {
        if (AppUtils.isEmpty(historyExpresses)) {
            rv_history_list.visibility = View.GONE
            tv_empty.visibility = View.VISIBLE
        } else {
            rv_history_list.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            this.historyExpresses.clear()
            this.historyExpresses.addAll(historyExpresses)
            historyExpressAdapter.notifyDataSetChanged()
        }
    }
}