package com.sjl.bookmark.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.BrowserActivity
import com.sjl.bookmark.ui.adapter.ArticleAdapter
import com.sjl.bookmark.ui.contract.ArticleListContract
import com.sjl.bookmark.ui.presenter.ArticleListPresenter
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.article_list_fragment.*

/**
 * 文章列表
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListFragment.java
 * @time 2018/3/23 14:00
 * @copyright(C) 2018 song
 */
class ArticleListFragment : BaseFragment<ArticleListPresenter>(), ArticleListContract.View,
    BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener,
    OnRefreshListener, RequestLoadMoreListener {

    private var cid = 0
    private lateinit var mArticleAdapter: ArticleAdapter
    override fun onFirstUserVisible() {}
    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    override fun getLayoutId(): Int {
        return R.layout.article_list_fragment
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        val arguments = arguments
        cid = arguments!!.getInt(HttpConstant.CONTENT_CID_KEY)
        LogUtils.i("当前文章种类id$cid")
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
        /**设置RecyclerView */
        val linearLayoutManager = LinearLayoutManager(context)
        rvArticleList.layoutManager = linearLayoutManager
        mArticleAdapter = ArticleAdapter(R.layout.articlelist_recycle_item, null)
        /**隐藏文章类型 */
        mArticleAdapter.setChapterNameVisible(false)
        rvArticleList.adapter = mArticleAdapter
        /**设置事件监听 */
        mArticleAdapter.onItemClickListener = this
        mArticleAdapter.onItemChildClickListener = this
        swipeRefreshLayout!!.setOnRefreshListener(this)
        mArticleAdapter.setOnLoadMoreListener(this, rvArticleList)
        /**请求数据 */
        mPresenter.loadCategoryArticles(cid)
    }

    public override fun onEventComing(eventCenter: EventBusDto<*>?) {}
    override fun setCategoryArticles(article: Article, loadType: Int) {
        setLoadDataResult(mArticleAdapter, swipeRefreshLayout, article.datas, loadType)
    }

    protected fun setLoadDataResult(
        articleAdapter: BaseQuickAdapter<DatasBean, BaseViewHolder>,
        refreshLayout: SwipeRefreshLayout,
        list: MutableList<DatasBean>,
        loadType: Int
    ) {
        when (loadType) {
            HttpConstant.LoadType.TYPE_REFRESH_SUCCESS -> {
                if (list != null && list.size > 0) {
                    articleAdapter.setNewData(list)
                }
                refreshLayout.isRefreshing = false
            }
            HttpConstant.LoadType.TYPE_REFRESH_ERROR -> refreshLayout.isRefreshing = false
            HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS -> if (list != null && list.size > 0) {
                articleAdapter.addData(list)
            }
            HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR -> articleAdapter.loadMoreFail()
            else -> {}
        }
        if (list == null || list.isEmpty() || list.size < HttpConstant.PAGE_SIZE) {
            LogUtils.i("没有文章数据了")
            articleAdapter.loadMoreEnd(false) //数据全部加载完毕
        } else {
            //成功获取更多数据
            LogUtils.i("加载完成")
            articleAdapter.loadMoreComplete() //成功获取更多数据
        }
    }

    /**
     * 下拉刷新
     */
    override fun onRefresh() {
        mPresenter.refresh()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {}
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val item = mArticleAdapter.getItem(position)
        mArticleAdapter.addBrowseTrack(item?.id.toString(), position)
        BrowserActivity.startWithParams(
            mActivity, mArticleAdapter.getItem(position)?.title,
            mArticleAdapter.getItem(position)?.link
        )
    }

    /**
     * 上拉加载
     */
    override fun onLoadMoreRequested() {
        mPresenter.loadMore()
    }
}