package com.sjl.bookmark.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.jakewharton.rxbinding2.view.RxView
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhihu.NewsList
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.NewsMultiDelegateAdapter
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider
import com.sjl.bookmark.ui.contract.NewsListContract
import com.sjl.bookmark.ui.presenter.NewsListPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.news_list_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.concurrent.TimeUnit

/**
 * 知乎日报列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListActivity.java
 * @time 2018/12/18 17:05
 * @copyright(C) 2018 song
 */
class NewsListActivity : BaseActivity<NewsListPresenter>(), NewsListContract.View,
    OnRefreshListener, RequestLoadMoreListener {

    private lateinit var newsMultiDelegateAdapter: NewsMultiDelegateAdapter
    private lateinit var layoutManager: LinearLayoutManager
    override fun getLayoutId(): Int {
        return R.layout.news_list_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_zhihu_daily))
        doubleClickDetect(common_toolbar)
    }

    override fun initData() {
        swipe_refresh_layout.setColorSchemeResources(R.color.blueStatus)
        swipe_refresh_layout.setOnRefreshListener(this)
        newsMultiDelegateAdapter = NewsMultiDelegateAdapter(this, R.layout.news_list_activity, null)
        mPresenter.loadNews()
        layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.VERTICAL))
        recycler.adapter = newsMultiDelegateAdapter
        newsMultiDelegateAdapter.setOnLoadMoreListener(this, recycler)
        changeToolbarTitle()
    }

    private var dateViewPosition = -1

    /**
     * 滚动改变ToolBar Title
     */
    private fun changeToolbarTitle() {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            /**
             *
             * @param recyclerView
             * @param dx
             * @param dy  dy > 0 时为向上滚动, dy < 0 时为向下滚动
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val firstVisibleItemViewType =
                    newsMultiDelegateAdapter.getItemViewType(firstVisibleItemPosition)

//                LogUtils.i("firstVisibleItemPosition:" + firstVisibleItemPosition + ",firstVisibleItemViewType:" + firstVisibleItemViewType);
                if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_HEADER) {
                    common_toolbar.setTitle(R.string.group_tile_zhihu_daily)
                } else if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_HEADER_SECOND) {
                    common_toolbar.setTitle(R.string.group_tile_today_news)
                } else if (firstVisibleItemViewType == NewsMultiDelegateAdapter.TYPE_DATE) {
                    val date = newsMultiDelegateAdapter.getItem(firstVisibleItemPosition)!!
                        .date
                    common_toolbar.title = date
                } else {
                    if (firstVisibleItemPosition < dateViewPosition) {
                        common_toolbar.setTitle(R.string.group_tile_today_news)
                    } else {
                        common_toolbar.title =
                            newsMultiDelegateAdapter.getItem(firstVisibleItemPosition)?.date
                    }
                }
            }
        })
    }

    /**
     * 双击监听
     *
     * @param view
     */
    fun doubleClickDetect(view: View?) {
        val share = RxView.clicks(view!!).share()
        share.buffer(share.debounce(200, TimeUnit.MILLISECONDS))
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(bindLifecycle())
            .subscribe({ objects ->
                if (objects.size >= 2) {
                    LogUtils.i("double click detected.")
                    //double click detected
                    //双击toolbar,平滑滚回顶部
                    recycler.smoothScrollToPosition(0)
                }
            }) { }
    }

    override fun onRefresh() {
        mPresenter.refresh()
    }

    override fun onLoadMoreRequested() { //滑动最后一个Item的时候回调onLoadMoreRequested方法
        mPresenter.loadMore()
    }

    override fun refreshNewsList(newsLists: List<NewsList>) {
        newsMultiDelegateAdapter.firstLoadFlag = mPresenter.isFirstLoadFlag
        newsMultiDelegateAdapter.setNewData(newsLists)
        swipe_refresh_layout.isRefreshing = false
        dateViewPosition = newsLists.size + 1
    }

    override fun showMoreNewsList(newsLists: List<NewsList>) {
        newsMultiDelegateAdapter.addData(newsLists)
        if (newsLists == null || newsLists.isEmpty()) {
            LogUtils.i("没有日报数据了")
            newsMultiDelegateAdapter.loadMoreEnd(false) //数据全部加载完毕,显示没有更多数据
        } else {
            newsMultiDelegateAdapter.loadMoreComplete() //注意不是加载结束，而是本次数据加载结束并且还有下页数据
        }
    }
}