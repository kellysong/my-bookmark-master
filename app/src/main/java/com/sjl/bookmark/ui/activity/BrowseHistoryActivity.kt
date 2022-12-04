package com.sjl.bookmark.ui.activity

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.BrowseTrackDaoImpl
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.BrowseHistoryAdapter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.browse_history_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 浏览记录
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseHistoryActivity
 * @time 2022/12/2 18:10
 * @copyright(C) 2022 song
 */
class BrowseHistoryActivity : BaseActivity<NoPresenter>(){
    private lateinit var browseHistoryAdapter: BrowseHistoryAdapter
    private  lateinit var clearMenuItem:MenuItem;

    override fun getLayoutId(): Int {
       return R.layout.browse_history_activity
    }

    override fun initView() {

    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.menu_browse_history))
    }

    override fun initData() {
        var browseTrackDaoImpl = BrowseTrackDaoImpl(this)
        val list = browseTrackDaoImpl.findBrowseTrackByType(0)
        if (list.isNullOrEmpty()){
            tv_empty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            clearMenuItem.isVisible = false
        }else{
            tv_empty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        browseHistoryAdapter = BrowseHistoryAdapter(this, list)
        browseHistoryAdapter.setOnItemClickListener { adapter, view, position ->

            val item = browseHistoryAdapter.getItem(position)
            item?.let {
                BrowserActivity.startWithParams(mContext, it.text, it.href)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = browseHistoryAdapter
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        clearMenuItem = menu.getItem(0)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.browse_history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_history_clear) {
            //清空
                XPopup.Builder(mContext)
                .isDestroyOnDismiss(true)
                .asConfirm(null, getString(R.string.delete_browse_history_confirm),
                    getString(R.string.cancel), getString(R.string.sure), {
                        var browseTrackDaoImpl = BrowseTrackDaoImpl(this)
                        browseTrackDaoImpl.deleteAllBrowseTrack();
                        tv_empty.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        clearMenuItem.isVisible = false
                    }, null, false
                ).show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}