package com.sjl.bookmark.ui.activity

import android.app.ProgressDialog
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.dao.impl.DaoFactory
import com.sjl.bookmark.entity.zhuishu.BookDetailDto.BookDetail
import com.sjl.bookmark.entity.zhuishu.HotCommentDto.HotComment
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.BookDetailActivity
import com.sjl.bookmark.ui.activity.BookReadActivity
import com.sjl.bookmark.ui.adapter.*
import com.sjl.bookmark.ui.contract.BookDetailContract
import com.sjl.bookmark.ui.presenter.BookDetailPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.net.RxBus
import com.sjl.core.net.RxSchedulers
import com.sjl.core.net.RxVoid
import com.sjl.core.util.datetime.TimeUtils
import com.sjl.core.util.log.LogUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.book_detail_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 书籍详情
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookDetailActivity.java
 * @time 2018/12/2 21:07
 * @copyright(C) 2018 song
 */
class BookDetailActivity : BaseActivity<BookDetailPresenter>(),
    BookDetailContract.View {

    private lateinit var mHotCommentAdapter: BookHotCommentAdapter
    private var mBookListAdapter: BookListAdapter? = null
    private var mCollBookBean: CollectBook? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mBookId: String? = null
    private var isBriefOpen: Boolean = false
    private var isCollected: Boolean = false
    override fun getLayoutId(): Int {
        return R.layout.book_detail_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_book_detail))
        //简介，可伸缩的TextView
        book_detail_tv_brief.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (isBriefOpen) {
                    book_detail_tv_brief.maxLines = 4
                    isBriefOpen = false
                } else {
                    book_detail_tv_brief.maxLines = 8
                    isBriefOpen = true
                }
            }
        })

        //追更和放弃
        book_list_tv_chase.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (isCollected) { //已经收藏有，点击放弃
                    //从数据库删除
                    DaoFactory.getCollectBookDao().deleteCollBookInRx(mCollBookBean)
                        .compose(RxSchedulers.applySingle())
                        .`as`(bindLifecycle())
                        .subscribe(object : Consumer<RxVoid?> {
                            @Throws(Exception::class)
                            override fun accept(rxVoid: RxVoid?) {
                                LogUtils.i("删除收藏成功")
                                val activity: BaseActivity<*>? =
                                    getActivity(BookReadActivity::class.java)
                                if (activity != null) {
                                    activity.finish()
                                }
                                RxBus.getInstance().post(false) //更新书架
                            }
                        }, object : Consumer<Throwable?> {
                            @Throws(Exception::class)
                            override fun accept(throwable: Throwable?) {
                                LogUtils.e("删除收藏异常", throwable)
                            }
                        })
                    book_list_tv_chase.text =
                        resources.getString(R.string.nb_book_detail_chase_update)

                    //修改背景
                    val drawable: Drawable =
                        resources.getDrawable(R.drawable.selector_btn_book_list)
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        book_list_tv_chase.background = drawable
                    } else {
                        book_list_tv_chase.setBackgroundDrawable(drawable)
                    }
                    //设置图片
                    book_list_tv_chase.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            this@BookDetailActivity,
                            R.mipmap.ic_book_list_add
                        ), null,
                        null, null
                    )
                    isCollected = false
                } else { //没有收藏，添加收藏
                    mPresenter.addToBookShelf((mCollBookBean)!!)
                    book_list_tv_chase.text = resources.getString(R.string.nb_book_detail_give_up)

                    //修改背景
                    val drawable: Drawable =
                        resources.getDrawable(R.drawable.shape_common_gray_corner)
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        book_list_tv_chase.background = drawable
                    } else {
                        book_list_tv_chase.setBackgroundDrawable(drawable)
                    }
                    //设置图片
                    /**
                     * setCompoundDrawables 设置图片的宽高是通过的画的drawable的宽高决定的，
                     * 所以，必须先使用Drawable.setBounds设置Drawable的宽高，图片才会显示,故采用setCompoundDrawablesWithIntrinsicBounds方便处理
                     */
                    book_list_tv_chase.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            this@BookDetailActivity,
                            R.mipmap.ic_book_list_delete
                        ), null,
                        null, null
                    )
                    isCollected = true
                }
            }
        })
        //开始阅读
        book_detail_tv_read.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
//                startActivityForResult(new Intent(BookDetailActivity.this, BookReadActivity.class)
//                        .putExtra(BookReadActivity.EXTRA_IS_COLLECTED, isCollected)
//                        .putExtra(BookReadActivity.EXTRA_COLL_BOOK, mCollBookBean), REQUEST_READ);
                startActivity(
                    Intent(this@BookDetailActivity, BookReadActivity::class.java)
                        .putExtra(BookReadActivity.Companion.EXTRA_IS_COLLECTED, isCollected)
                        .putExtra(BookReadActivity.Companion.EXTRA_COLL_BOOK, mCollBookBean)
                )
            }
        })

        //更多评论
        book_detail_tv_more_comment!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val bundle: Bundle = Bundle()
                bundle.putString(EXTRA_BOOK_ID, mBookId)
                openActivity(BookMoreCommentActivity::class.java, bundle)
            }
        })
    }

    override fun initData() {
        mBookId = intent.getStringExtra(EXTRA_BOOK_ID)
        requestData()
    }

    private fun requestData() {
        refresh_layout!!.showLoading()
        mPresenter!!.refreshBookDetail((mBookId)!!)
    }

    /*
    * 复用Activity时的生命周期回调
    */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID)
        if (TextUtils.isEmpty(mBookId)) {
            return
        }
        nsv_content.fling(0) //滑动到顶部
        nsv_content.smoothScrollTo(0, 0)
        requestData()
    }

    override fun finishRefresh(bookDetail: BookDetail) {
        //封面
        Glide.with(this)
            .load(HttpConstant.ZHUISHU_IMG_BASE_URL + bookDetail.cover)
            .placeholder(R.drawable.ic_book_loading)
            .error(R.mipmap.ic_load_error)
            .centerCrop()
            .into((book_detail_iv_cover))

        //书名
        book_detail_tv_title.text = bookDetail.title
        //作者
        book_detail_tv_author.text = bookDetail.author
        //类型
        book_detail_tv_type.text = "|" + bookDetail.majorCate

        //总字数
        book_detail_tv_word_count.text = resources.getString(
            R.string.nb_book_word,
            bookDetail.wordCount / 10000
        )
        //更新时间
        book_detail_tv_lately_update.text = TimeUtils.dateConvert(
            bookDetail.updated,
            TimeUtils.DATE_FORMAT_7
        )
        //追书人数
        book_detail_tv_follower_count.text = bookDetail.followerCount.toString() + ""
        //存留率
        book_detail_tv_retention.text = bookDetail.retentionRatio + "%"
        //日更字数
        book_detail_tv_day_word_count.text = bookDetail.serializeWordCount.toString() + ""
        //简介
        book_detail_tv_brief.text = bookDetail.longIntro
        mCollBookBean = DaoFactory.getCollectBookDao().getCollectBook(bookDetail._id)

        //判断是否收藏
        if (mCollBookBean != null) {
            isCollected = true
            book_list_tv_chase.text = resources.getString(R.string.nb_book_detail_give_up)
            //修改背景
            val drawable: Drawable = resources.getDrawable(R.drawable.shape_common_gray_corner)
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                book_list_tv_chase.background = drawable
            } else {
                book_list_tv_chase.setBackgroundDrawable(drawable)
            } //设置图片
            book_list_tv_chase.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, R.mipmap.ic_book_list_delete), null,
                null, null
            )
            book_detail_tv_read.setText(R.string.nb_book_detail_continue_read)
        } else {
            isCollected = false
            book_list_tv_chase.text = resources.getString(R.string.nb_book_detail_chase_update)
            //修改背景
            val drawable: Drawable = resources.getDrawable(R.drawable.selector_btn_book_list)
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                book_list_tv_chase.background = drawable
            } else {
                book_list_tv_chase.setBackgroundDrawable(drawable)
            } //设置图片
            book_list_tv_chase.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this@BookDetailActivity, R.mipmap.ic_book_list_add), null,
                null, null
            )
            book_detail_tv_read.setText(R.string.nb_book_detail_start_read)
            mCollBookBean = bookDetail.collBookBean
        }
    }

    override fun finishHotComment(hotCommentList: List<HotComment>) {
        if (hotCommentList.isEmpty()) {
            return
        }
        mHotCommentAdapter = BookHotCommentAdapter(
            this,
            R.layout.bookdetail_hot_comment_recycle_item,
            hotCommentList
        )
        book_detail_rv_hot_comment.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                //RecyclerView与外部ScrollView滑动冲突
                return false
            }
        }
        book_detail_rv_hot_comment.addItemDecoration(
            RecyclerViewDivider(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        book_detail_rv_hot_comment.isNestedScrollingEnabled =
            false //禁止RecyclerView嵌套滑动，防止NestedScrollView滑动不流畅
        book_detail_rv_hot_comment.adapter = mHotCommentAdapter
    }

    override fun finishRecommendBookList(recommendBookList: List<RecommendBook>) {
        if (recommendBookList == null || recommendBookList.isEmpty()) {
            return
        }
        //推荐书籍
        mBookListAdapter =
            BookListAdapter(this, R.layout.bookdetail_like_recycle_item, recommendBookList)
        book_detail_rv_recommend_book_list!!.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                //RecyclerView与外部ScrollView滑动冲突
                return false
            }
        }
        book_detail_rv_recommend_book_list!!.addItemDecoration(
            RecyclerViewDivider(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        book_detail_rv_recommend_book_list.adapter = mBookListAdapter
        mBookListAdapter?.setOnItemClickListener(object :
            MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val dataItem: RecommendBook = mBookListAdapter!!.getItem(position)
                startActivity(this@BookDetailActivity, dataItem.recommendId)
            }

            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })
    }

    override fun waitToBookShelf() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.setTitle(getString(R.string.book_detail_add_shelf_hint))
        }
        mProgressDialog?.show()
    }

    override fun errorToBookShelf() {
        mProgressDialog?.dismiss()
        showShortToast(getString(R.string.book_detail_add_shelf_hint2))
    }

    override fun succeedToBookShelf() {
        mProgressDialog?.dismiss()
        showShortToast(getString(R.string.book_detail_add_shelf_hint3))
        //更新书架
        RxBus.getInstance().post(true)
    }

    override fun showError() {
        refresh_layout.showError()
    }

    override fun complete() {
        refresh_layout.showFinish()
    }

    companion object {
        val RESULT_IS_COLLECTED: String = "result_is_collected"
        val EXTRA_BOOK_ID: String = "extra_book_id"
        fun startActivity(context: Context, bookId: String?) {
            val intent: Intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(EXTRA_BOOK_ID, bookId)
            context.startActivity(intent)
        }
    }
}