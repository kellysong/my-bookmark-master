package com.sjl.bookmark.ui.activity

import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.WifiInfo
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.contract.WifiQueryContract
import com.sjl.bookmark.ui.presenter.WifiQueryPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import com.zhy.adapter.abslistview.CommonAdapter
import com.zhy.adapter.abslistview.ViewHolder
import kotlinx.android.synthetic.main.activity_wifi_query.*
import kotlinx.android.synthetic.main.toolbar_scroll.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * wifi密码查询
 * add by Kelly on 20170302
 */
class WifiQueryActivity : BaseActivity<WifiQueryPresenter>(),
    WifiQueryContract.View {

    override fun getLayoutId(): Int {
        return R.layout.activity_wifi_query
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_wifi_query))
    }

    override fun initData() {
        EventBus.getDefault().register(this)
    }

    /**
     * ctivity界面被显示出来的时候执行的，用户可见，包括有一个activity在他上面，但没有将它完全覆盖，用户可以看到部分activity但不能与它交互
     */
    override fun onStart() {
        super.onStart()
        mPresenter!!.initWifiInfo()
    }

    /**
     * 只能有一个参数
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun noRoot(msg: String) {
        LogUtils.i("msg1:$msg")
        ll_empty.visibility = View.VISIBLE
        text_hint.setText(R.string.not_root_hint_txt)
        Toast.makeText(this, R.string.not_root_hint, Toast.LENGTH_LONG).show()
    }

    /*测试=======================start*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun noRoot2(msg: String) {
        LogUtils.i("msg2:$msg")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun no1(msg: Map<*, *>?) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun no2(msg: Map<*, *>?) {
    }

    /*测试=======================end*/
    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun showWifiInfo(wifiInfos: List<WifiInfo>) {
        if (wifiInfos != null && wifiInfos.size > 0) {
            listview.visibility = View.VISIBLE
            ll_empty.visibility = View.GONE
            // 列表倒序
            Collections.reverse(wifiInfos)
            listview.adapter = object :
                CommonAdapter<WifiInfo>(this, R.layout.wifi_query_list_item, wifiInfos) {
                override fun convert(viewHolder: ViewHolder, item: WifiInfo, position: Int) {
                    viewHolder.setText(
                        R.id.item_name,
                        getString(R.string.iten_name_hint) + item.name
                    )
                    viewHolder.setText(
                        R.id.item_password,
                        getString(R.string.item_pasword_hint) + item.password
                    )
                    viewHolder.setText(
                        R.id.item_type,
                        getString(R.string.item_encryption_type) + item.encryptType
                    )
                }
            }
            listview.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {
                override fun onItemLongClick(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ): Boolean {
                    mPresenter!!.copyWifiPassword(wifiInfos.get(position).password)
                    return true
                }
            }
            listview.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    mPresenter!!.connectWifi(wifiInfos.get(position))
                }
            }
        } else {
            listview.visibility = View.GONE
            ll_empty.visibility = View.VISIBLE
        }
    }
}