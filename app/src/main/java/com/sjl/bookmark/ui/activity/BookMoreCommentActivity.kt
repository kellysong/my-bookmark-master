package com.sjl.bookmark.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.sjl.bookmark.R
import com.sjl.bookmark.api.ZhuiShuShenQiApi
import com.sjl.bookmark.entity.zhuishu.HotCommentDto
import com.sjl.bookmark.entity.zhuishu.HotCommentDto.HotComment
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.adapter.BookHotCommentAdapter
import com.sjl.bookmark.ui.adapter.RecyclerViewDivider
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper.OnLoadMoreListener
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.bookmore_comment_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookMoreCommentActivity.java
 * @time 2018/12/9 13:57
 * @copyright(C) 2018 song
 */
class BookMoreCommentActivity : BaseActivity<NoPresenter>() {

    private var mBookId: String? = null
    private var mPage: Int = 1
    private lateinit var mHotCommentAdapter: BookHotCommentAdapter
    private lateinit var mLoadMoreWrapper: LoadMoreWrapper<*>
    private lateinit var datas: MutableList<HotComment>
    private var pullDownFlag: Boolean = true
    override fun getLayoutId(): Int {
        return R.layout.bookmore_comment_activity
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus)
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_book_comment))
        swipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                mPage = 1
                pullDownFlag = true
                requestData()
            }
        })
    }

    override fun initData() {
        datas = ArrayList()
        mBookId = intent.getStringExtra(BookDetailActivity.Companion.EXTRA_BOOK_ID)
        rvHomeArticles.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.VERTICAL))
        rvHomeArticles.layoutManager = LinearLayoutManager(this) //必须设置否则数据不显示
        mHotCommentAdapter =
            BookHotCommentAdapter(this, R.layout.bookdetail_hot_comment_recycle_item, datas)
        mLoadMoreWrapper = LoadMoreWrapper<Any?>(mHotCommentAdapter)
        mLoadMoreWrapper.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMoreRequested() { //自动触发一次
                pullDownFlag = false
                requestData()
            }
        })
        rvHomeArticles.adapter = mLoadMoreWrapper
    }

    private fun requestData() {
        val zhuiShuShenQiApi: ZhuiShuShenQiApi = RetrofitHelper.getInstance().getApiService(
            ZhuiShuShenQiApi::class.java
        )
        mPage = 20 //目前接口不支持分页，暂时取前面20条评论
        zhuiShuShenQiApi.getMoreComment2(mBookId, mPage)
            .compose(RxSchedulers.applySingle()) //                .as(BookMoreCommentActivity.this.<HotCommentDto>bindLifecycle())
            .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe(object : Consumer<HotCommentDto> {
                @Throws(Exception::class)
                override fun accept(hotCommentDto: HotCommentDto) {
                    if (hotCommentDto.ok) {
                        val reviews: List<HotComment>? = hotCommentDto.reviews
                        if (reviews != null && reviews.size > 0) {
                            if (pullDownFlag) { //下拉
                                addData(true, reviews)
                            } else {
                                //局部刷新
                                addData(false, reviews)
                            }
                            mLoadMoreWrapper.loadMoreEnd() //结束加载

//                                mLoadMoreWrapper.loadMoreCompleted();
//                                mPage++;
                        } else {
                            mLoadMoreWrapper.loadMoreEnd()
                        }
                    } else {
                        mLoadMoreWrapper.loadMoreEnd()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    LogUtils.e("书评获取异常", throwable)
                    swipeRefreshLayout.isRefreshing = false
                    mLoadMoreWrapper.loadMoreEnd()
                }
            })
    }

    fun addData(isFirstPage: Boolean, cells: List<HotComment>) {
        if (isFirstPage) {
            datas.clear()
            datas.addAll(datas.size, cells)
            mLoadMoreWrapper.notifyDataSetChanged()
        } else {
            datas.addAll(datas.size, cells)
            mLoadMoreWrapper.notifyItemRangeChanged(datas.size, datas.size + cells.size)
        }
    }
}