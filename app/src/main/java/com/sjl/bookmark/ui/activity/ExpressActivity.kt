package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.express_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * ExpressActivity和ExpressHistoryActivity共用一个ExpressContract、ExpressPresenter
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressActivity.java
 * @time 2018/4/26 14:26
 * @copyright(C) 2018 song
 */
class ExpressActivity : BaseActivity<ExpressPresenter>(), ExpressContract.View {

    private val unCheckList: MutableList<HistoryExpress> = ArrayList()
    private lateinit var adapter: HistoryExpressAdapter
    override fun getLayoutId(): Int {
        return R.layout.express_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_my_express))
    }

    override fun initData() {
        adapter = HistoryExpressAdapter(this, R.layout.history_express_recycle_item, unCheckList)
        rv_un_check.layoutManager = LinearLayoutManager(this)
        rv_un_check.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_un_check.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.express_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSearch) { //查件
            startActivity(Intent(this, ExpressSearchActivity::class.java))
        } else if (item.itemId == R.id.menuHistory) { //历史记录
            startActivity(Intent(this, ExpressHistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getUnCheckList()
    }

    override fun setHistoryExpress(historyExpresses: List<HistoryExpress>) {
        if (AppUtils.isEmpty(historyExpresses)) {
            rv_un_check.visibility = View.GONE
            tv_empty.visibility = View.VISIBLE
        } else {
            rv_un_check.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            unCheckList.clear()
            unCheckList.addAll(historyExpresses)
            adapter.notifyDataSetChanged()
        }
    }
}