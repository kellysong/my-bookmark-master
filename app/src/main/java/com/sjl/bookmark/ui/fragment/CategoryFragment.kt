package com.sjl.bookmark.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.ArticleTypeActivity
import com.sjl.bookmark.ui.adapter.CategoryLeftAdapter
import com.sjl.bookmark.ui.adapter.CategoryRightAdapter
import com.sjl.bookmark.ui.adapter.TopItemDecoration
import com.sjl.bookmark.ui.contract.CategoryContract
import com.sjl.bookmark.ui.presenter.CategoryPresenter
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import kotlinx.android.synthetic.main.category_fragment.*
import kotlinx.android.synthetic.main.empty_view.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryFragment.java
 * @time 2018/3/21 11:26
 * @copyright(C) 2018 song
 */
class CategoryFragment : BaseFragment<CategoryPresenter>(), CategoryContract.View,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mCategoryLeftAdapter: CategoryLeftAdapter
    private lateinit var mCategoryRightAdapter: CategoryRightAdapter

    override fun onFirstUserVisible() {
        /**请求数据 */
        mPresenter.loadCategoryData()
    }

    override fun onUserVisible() {
        mCategoryLeftAdapter.refresh()
    }
    override fun onUserInvisible() {}
    override fun getLayoutId(): Int {
        return R.layout.category_fragment
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
        swipeRefreshLayout.setOnRefreshListener(this)

        mCategoryLeftAdapter = CategoryLeftAdapter(null)
        /**设置左侧RecyclerView */
        rvLeftKnowledgeSystems.layoutManager = LinearLayoutManager(context)
        rvLeftKnowledgeSystems.adapter = mCategoryLeftAdapter
        mCategoryLeftAdapter.setOnItemLongClickListener { adapter, view, position ->
            val category = mCategoryLeftAdapter.data[position]
            val intent = Intent(mActivity, ArticleTypeActivity::class.java)
            intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, category.name)
            val children = category.children
            intent.putParcelableArrayListExtra(HttpConstant.CONTENT_CHILDREN_DATA_KEY, children as ArrayList<out Parcelable?>)
            intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "1")
            mActivity.startActivity(intent)
            false
        }
        mCategoryRightAdapter = CategoryRightAdapter(null)
        /**设置右侧RecyclerView */
        rvRightKnowledgeSystems.layoutManager = LinearLayoutManager(context)
        //右侧recyclerview悬浮置顶效果
        val top = TopItemDecoration(context as Activity,R.color.gray_200,R.color.secondary_text).apply {
            this.tagListener = {position ->
                val category = mCategoryLeftAdapter.data[position]
                category.name.toString()
            }
        }
        rvRightKnowledgeSystems.addItemDecoration(top)
        rvRightKnowledgeSystems.adapter = mCategoryRightAdapter

        recyclerViewLinkage()
    }

    /**
     * 左右两个RecyclerView联动
     */
    private fun recyclerViewLinkage() {
        val rightLinearLayoutManager = rvRightKnowledgeSystems.layoutManager as LinearLayoutManager
        //左边联动右边
        mCategoryLeftAdapter.setOnItemClickListener { adapter, view, position ->

            mCategoryLeftAdapter.setChoose(position)
            rightLinearLayoutManager.scrollToPositionWithOffset(position, 0)
        }
        //右边联动左边
        rvRightKnowledgeSystems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemPosition = rightLinearLayoutManager.findFirstVisibleItemPosition()
                if (firstItemPosition != -1) {
                    rvLeftKnowledgeSystems.smoothScrollToPosition(firstItemPosition)
                    mCategoryLeftAdapter.setChoose(firstItemPosition)
                }
            }

        })
    }

    public override fun onEventComing(eventCenter: EventBusDto<*>?) {}

    override fun setCategory(categories: List<Category>) {
        view_divider.visibility = View.VISIBLE
        ll_empty_view.visibility = View.GONE
        ll_container.visibility = View.VISIBLE

        mCategoryLeftAdapter.setNewData(categories)
        mCategoryRightAdapter.setNewData(categories)
        swipeRefreshLayout.isRefreshing = false

    }

    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun showFail(message: String?) {
        swipeRefreshLayout.isRefreshing = false
        ll_empty_view.visibility = View.VISIBLE
        ll_container.visibility = View.GONE

    }

    override fun onRefresh() {
        mPresenter.refresh()
    }

}