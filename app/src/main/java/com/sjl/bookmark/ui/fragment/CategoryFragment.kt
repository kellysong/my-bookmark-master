package com.sjl.bookmark.ui.fragment

import android.content.Intent
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.ArticleTypeActivity
import com.sjl.bookmark.ui.adapter.CategoryAdapter
import com.sjl.bookmark.ui.contract.CategoryContract
import com.sjl.bookmark.ui.presenter.CategoryPresenter
import com.sjl.core.entity.EventBusDto
import com.sjl.core.mvp.BaseFragment
import kotlinx.android.synthetic.main.category_fragment.*
import java.util.*

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
    BaseQuickAdapter.OnItemClickListener, OnRefreshListener {

    private lateinit var mCategoryAdapter: CategoryAdapter
    override fun onFirstUserVisible() {
        /**请求数据 */
        mPresenter.loadCategoryData()
    }

    override fun onUserVisible() {}
    override fun onUserInvisible() {}
    override fun getLayoutId(): Int {
        return R.layout.category_fragment
    }

    override fun initView() {}
    override fun initListener() {}
    override fun initData() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
        mCategoryAdapter = CategoryAdapter(R.layout.category_knowledge_recycle_item, null)
        /**设置RecyclerView */
        rvKnowledgeSystems.layoutManager = LinearLayoutManager(context)
        rvKnowledgeSystems.adapter = mCategoryAdapter
        /**设置事件监听 */
        mCategoryAdapter.onItemClickListener = this
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    public override fun onEventComing(eventCenter: EventBusDto<*>?) {}
    override fun setCategory(categories: List<Category>) {
        mCategoryAdapter.setNewData(categories)
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun showFail(message: String?) {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onRefresh() {
        mPresenter.refresh()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val intent = Intent(mActivity, ArticleTypeActivity::class.java)
        intent.putExtra(HttpConstant.CONTENT_TITLE_KEY, mCategoryAdapter.getItem(position)!!.name)
        val children = mCategoryAdapter.getItem(position)!!
            .children
        intent.putParcelableArrayListExtra(
            HttpConstant.CONTENT_CHILDREN_DATA_KEY,
            children as ArrayList<out Parcelable?>
        )
        intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "1")
        mActivity.startActivity(intent)
    }
}