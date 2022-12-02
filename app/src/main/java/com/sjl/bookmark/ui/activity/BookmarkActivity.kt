package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.Bookmark
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.BookmarkSearchActivity
import com.sjl.bookmark.ui.adapter.BookmarkAdapter
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider
import com.sjl.bookmark.ui.contract.BookmarkContract
import com.sjl.bookmark.ui.listener.OnItemSelectListener
import com.sjl.bookmark.ui.presenter.BookmarkPresenter
import com.sjl.bookmark.widget.popupview.BookmarkMenuPopupView
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.toolbar_scroll.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkActivity.java
 * @time 2018/1/29 16:13
 * @copyright(C) 2018 song
 */
class BookmarkActivity : BaseActivity<BookmarkPresenter>(), BookmarkContract.View {

    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mBookmarkAdapter: BookmarkAdapter
    private var menuItem: MenuItem? = null
    private var menuItemVisible = true


    private var mCurrentPosition = 0
    private var mSuspensionHeight = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_bookmark
    }

    public override fun initView() {
        common_toolbar.title = I18nUtils.getString(R.string.google_bookmark)
        setSupportActionBar(common_toolbar)
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * oncreate方法之后触发
     *
     * @param menu
     * @return
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuItem = menu.getItem(0)
        if (!menuItemVisible) {
            common_toolbar.title = I18nUtils.getString(R.string.title_search_result)
            menuItem?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bookmark_menu, menu)
        return true
    }
    var bookmarkMenuPopupView:BookmarkMenuPopupView ?= null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify item_bookmark_title parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_search) {
            startActivity(Intent(this, BookmarkSearchActivity::class.java))
            return true
        } else if (id == R.id.action_bookmark_source) {
            if (bookmarkMenuPopupView == null){
                bookmarkMenuPopupView = BookmarkMenuPopupView(this,common_toolbar)
            }
            bookmarkMenuPopupView?.run {
                setOnSelectListener(OnItemSelectListener { position, item ->
                        mPresenter.reset()
                        val bookmarks =   mPresenter.initBookmarkList(item.sourceFile).toMutableList()
                        mBookmarkAdapter.setData(bookmarks)

                    })
                show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun initListener() {
        common_toolbar.setNavigationOnClickListener { finish() }
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
        swipeRefreshLayout.setOnRefreshListener { //下拉刷新
            mPresenter.pullRefreshDown()
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //LogUtils.i("StateChanged = " + newState);
                if (suspension_bar == null) {
                    return
                }
                mSuspensionHeight = suspension_bar.height
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //下面是悬浮标题
                if (suspension_bar == null || mCurrentPosition > mBookmarkAdapter.itemCount) {
                    return
                }
                if (mBookmarkAdapter.getItemViewType(mCurrentPosition) == BookmarkAdapter.TYPE_HEADER) {
                    val view = mLinearLayoutManager.findViewByPosition(mCurrentPosition)
                    if (view != null) {
                        if (view.top <= mSuspensionHeight) {
                            suspension_bar.y = -(mSuspensionHeight - view.top).toFloat()
                        } else {
                            suspension_bar.y = 0f
                        }
                    }
                }
                if (mCurrentPosition != mLinearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
                    suspension_bar.y = 0f
                    updateSuspensionBar()
                }


                //下面是更多加载条
//                LogUtils.i("onScrolled");
                val lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition + 1 == mBookmarkAdapter.itemCount) { //上拉加载更多
                    val isRefreshing = swipeRefreshLayout!!.isRefreshing
                    if (isRefreshing) {
                        mBookmarkAdapter.notifyItemRemoved(mBookmarkAdapter.itemCount)
                        return
                    }
                    mPresenter.pullRefreshUp()
                }
            }
        })
    }

    private fun updateSuspensionBar() {
        tv_title.text = mBookmarkAdapter.getTitle(mCurrentPosition)
    }

    public override fun initData() {
        mPresenter.init(intent)
        val bookmarks = mPresenter.initBookmarkList("").toMutableList()
        mBookmarkAdapter = BookmarkAdapter(this, bookmarks)
        mBookmarkAdapter.setLoadingState(mPresenter.getLoadState(bookmarks))
        mLinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLinearLayoutManager

        //添加动画
        recyclerView.itemAnimator = DefaultItemAnimator()


        //添加分割线
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//api25之后才有
        recyclerView.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.VERTICAL))


        /*    PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
        //设置只有RecyclerItem.ITEM_HEADER的item显示标签
        pinnedHeaderDecoration.registerTypePinnedHeader(0, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });


       mRecyclerView.addItemDecoration(pinnedHeaderDecoration);*/if (bookmarks.size > 0) {
            val bookmark = bookmarks[0]
            val type = bookmark.type
            if (type == 1) { //修复没有悬浮标题数据时吗，遮挡条目问题
                bookmarks.add(0, Bookmark(0, bookmark.title)) //追加一条悬浮标题数据
            }
            suspension_bar.visibility = View.VISIBLE //修复没有数据时显示悬浮条目问题
            updateSuspensionBar()
        } else {
            suspension_bar.visibility = View.GONE
        }
        recyclerView.adapter = mBookmarkAdapter
    }

    override fun showBookmarkData(bookmarks: List<Bookmark>?, loadingState: Int) { //上拉加载更多
        LogUtils.i("当前加载状态：$loadingState")
        mBookmarkAdapter.setLoadingState(loadingState) //注意此处
        mBookmarkAdapter.setData(bookmarks)
        swipeRefreshLayout.isRefreshing = false
        //加载更多的效果可以通过item_foot.xml自定义，滑动到最后一项时显示该item并执行加载更多，当加载数据完毕时需要将该item移除掉
        mBookmarkAdapter.notifyItemRemoved(mBookmarkAdapter.itemCount)
    }

    override fun setItemMenuVisible(visible: Boolean) {
        menuItemVisible = visible
    }
}