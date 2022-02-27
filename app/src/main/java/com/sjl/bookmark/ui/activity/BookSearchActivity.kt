package com.sjl.bookmark.ui.activity

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhuishu.SearchBookDto.BooksBean
import com.sjl.bookmark.ui.adapter.*
import com.sjl.bookmark.ui.contract.BookSearchContract
import com.sjl.bookmark.ui.presenter.BookSearchPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.*
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.book_search_activity.*
import kotlinx.android.synthetic.main.fragment_refresh_list.*
import me.gujun.android.taggroup.TagGroup.OnTagClickListener

/**
 * 书籍搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookSearchActivity.java
 * @time 2018/11/30 16:58
 * @copyright(C) 2018 song
 */
class BookSearchActivity : BaseActivity<BookSearchPresenter>(),
    BookSearchContract.View {


    /*    @BindView(R.id.search_rv_history)
        RecyclerView mRvHistory;*/

    private lateinit var mKeyWordAdapter: BookKeyWordAdapter
    private lateinit var mSearchAdapter: SearchBookAdapter

    /**
     * true显示关键字搜索自动补全列表，false显示搜索书籍列表
     */
    private var contentFlag: Boolean = true
    private var isTag: Boolean = false
    private var mHotTagList: List<String>? = null
    private var mTagStart: Int = 0
    override fun getLayoutId(): Int {
        return R.layout.book_search_activity
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            refresh_layout.background = ContextCompat.getDrawable(this, R.color.white)
        }
    }

    override fun initListener() {
        //退出
        search_iv_back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })

        //输入框
        search_et_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if ((s.toString().trim { it <= ' ' } == "")) {
                    //隐藏delete按钮和关键字显示内容
                    if (search_iv_delete.visibility == View.VISIBLE) {
                        search_iv_delete.visibility = View.INVISIBLE
                        refresh_layout.visibility = View.INVISIBLE
                        //删除全部视图
                        mKeyWordAdapter.datas.clear()
                        mSearchAdapter.datas.clear()
                        refresh_rv_content.removeAllViews()
                    }
                    return
                }
                //由原来隐藏变可见，显示delete按钮
                if (search_iv_delete.visibility == View.INVISIBLE) {
                    search_iv_delete.visibility = View.VISIBLE
                    refresh_layout.visibility = View.VISIBLE
                    //默认是显示完成状态
                    refresh_layout.showFinish()
                }
                //搜索
                val query: String = s.toString().trim { it <= ' ' }
                if (isTag) {
                    contentFlag = false
                    refresh_layout.showLoading()
                    mPresenter.searchBook(query)
                    isTag = false
                } else {
                    //传递
                    contentFlag = true
                    mPresenter.searchKeyWord(query)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        //键盘的搜索
        search_et_input.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                //修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    contentFlag = false
                    searchBook()
                    return true
                }
                return false
            }
        })

        //进行搜索
        search_iv_search.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                contentFlag = false
                searchBook()
            }
        })

        //删除字
        search_iv_delete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                search_et_input.setText("")
                ViewUtils.toggleKeyboard(this@BookSearchActivity)
            }
        })


        //Tag的点击事件
        search_tg_hot.setOnTagClickListener(object : OnTagClickListener {
            override fun onTagClick(tag: String) {
                isTag = true
                search_et_input.setText(tag)
            }
        })

        //Tag的刷新事件
        search_book_tv_refresh_hot.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                refreshTag()
            }
        })
    }

    override fun initData() {
        mKeyWordAdapter = BookKeyWordAdapter(this, R.layout.search_book_keyword_recycle_item, null)
        mSearchAdapter = SearchBookAdapter(this, R.layout.search_book_recycle_item, null)
        refresh_rv_content.layoutManager = LinearLayoutManager(this)
        refresh_rv_content.addItemDecoration(
            RecyclerViewDivider(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        //点击关键字查书
        mKeyWordAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                //显示正在加载
                contentFlag = false
                refresh_layout.showLoading()
                val book: String? = mKeyWordAdapter.getItem(position)
                mPresenter.searchBook(book)
                ViewUtils.toggleKeyboard(this@BookSearchActivity)
            }

            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })

        //书本的点击事件
        mSearchAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val item: BooksBean = mSearchAdapter.getItem(position)
                val bookId: String = item._id
                BookDetailActivity.startActivity(this@BookSearchActivity, bookId)
            }

            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })

        //默认隐藏
        refresh_layout.visibility = View.GONE
        //获取热词
        mPresenter.searchHotWord()
    }

    override fun finishHotWords(hotWords: List<String>) {
        mHotTagList = hotWords
        refreshTag()
    }

    override fun finishKeyWords(keyWords: List<String>) {
        if (keyWords == null || keyWords.size == 0) refresh_layout!!.visibility = View.INVISIBLE
        //BookKeyWordAdapter、SearchBookAdapter共用一个RecyclerView,先设置适配器在刷新
        if (contentFlag) {
            //设置mRefreshLayout、mRecyclerView显示，否则搜索书籍搜索列表时导致mRecyclerView隐藏
            refresh_layout.visibility = View.VISIBLE
            refresh_rv_content.visibility = View.VISIBLE
            refresh_rv_content.adapter = mKeyWordAdapter
            mKeyWordAdapter.refreshItems(keyWords)
        }
    }

    override fun finishBooks(books: List<BooksBean>) {
        if (books == null || books.size == 0) {
            refresh_layout!!.showEmpty()
        } else {
            //显示完成
            refresh_layout!!.showFinish()
        }
        //加载
        if (!contentFlag) {
            refresh_rv_content.adapter = mSearchAdapter
            mSearchAdapter.refreshItems(books)
        }
    }

    override fun errorBooks() {
        refresh_layout!!.showEmpty()
    }

    /**
     * 换一批
     */
    private fun refreshTag() {
        if (mHotTagList == null) {
            return
        }
        var last: Int = mTagStart + TAG_LIMIT
        if (mHotTagList!!.size <= last) {
            mTagStart = 0
            if (mHotTagList!!.size < TAG_LIMIT) {
                last = mTagStart + mHotTagList!!.size
            } else {
                last = mTagStart + TAG_LIMIT
            }
            showShortToast(getString(R.string.no_load_more))
        }
        val tags: List<String> = mHotTagList!!.subList(mTagStart, last)
        search_tg_hot.setTags(tags) //设置到控件TagGroup
        mTagStart += TAG_LIMIT
    }

    /**
     * 书籍查询
     */
    private fun searchBook() {
        val query: String = search_et_input!!.text.toString().trim { it <= ' ' }
        if (!(query == "")) {
            refresh_layout.visibility = View.VISIBLE
            //显示正在加载
            refresh_layout.showLoading()
            mPresenter.searchBook(query)
            ViewUtils.toggleKeyboard(this)
        }
    }

    companion object {
        private val TAG_LIMIT: Int = 8
    }
}