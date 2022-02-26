package com.sjl.bookmark.ui.fragment

import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.table.Account
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider
import com.sjl.bookmark.ui.contract.AccountListContract
import com.sjl.bookmark.ui.presenter.AccountListPresenter
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.account_fragment.*
import org.greenrobot.eventbus.EventBus

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AccountListFragment.java
 * @time 2018/3/7 14:24
 * @copyright(C) 2018 song
 */
class AccountListFragment : BaseFragment<AccountListPresenter>(), AccountListContract.View {

    private var position = 0
    private lateinit var mHandler: Handler
    override fun onFirstUserVisible() {
        mPresenter.onFirstUserVisible()
    }

    override fun onUserVisible() {
        mPresenter.onUserVisible()
    }

    override fun onUserInvisible() {}
    override fun getLayoutId(): Int {
        return R.layout.account_fragment
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        val arguments = arguments
        position = arguments!!.getInt("position") //当前fragment索引
        LogUtils.i("position=$position")
        mPresenter.setPosition(position)
        mHandler = Handler()
    }

    /**
     * eventbus事件到来
     *
     * @param eventCenter
     */
    public override fun onEventComing(eventCenter: EventBusDto<*>?) {
        mPresenter.onEventComing(eventCenter!!)
    }

    override fun initRecycler(
        linearLayoutManager: LinearLayoutManager,
        adapter: RecyclerView.Adapter<*>
    ) {
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus) //设置下拉圆圈的颜色
        swipeRefreshLayout.setOnRefreshListener { //下拉刷新
            LogUtils.i("正在刷新账号列表数据")
            mHandler.postDelayed({
                mPresenter.pullRefreshDown()
                swipeRefreshLayout.isRefreshing = false
            }, 1000)
        }
        recyclerView!!.addItemDecoration(
            RecyclerViewDivider(
                mActivity,
                LinearLayoutManager.VERTICAL
            )
        )
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun readGo(clazz: Class<*>, operateFlag: Int, account: Account) {
        val intent = Intent(mActivity, clazz)
        intent.putExtra("CREATE_MODE", operateFlag)
        intent.putExtra("accountId", account.id) //根据id查询账号明细
        startActivityForResult(intent, INDEX_FRAGMENT_REQUEST_CODE)
    }

    override fun hideEmptyView() {
        swipeRefreshLayout.visibility = View.VISIBLE
        exception.visibility = View.GONE
    }

    override fun showEmptyView() {
        swipeRefreshLayout.visibility = View.GONE
        exception.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("requestCode=$requestCode,resultCode=$resultCode")
        if (requestCode == INDEX_FRAGMENT_REQUEST_CODE) {
            if (resultCode == EDIT_SUCCESS) {
                val eventBusDto: EventBusDto<*> =
                    EventBusDto<Any?>(position, AppConstant.ACCOUNT_REFRESH_EVENT_CODE, true)
                EventBus.getDefault().post(eventBusDto)
            }
        }
    }

    companion object {
        private const val INDEX_FRAGMENT_REQUEST_CODE = 2
        const val EDIT_SUCCESS = 2
    }
}