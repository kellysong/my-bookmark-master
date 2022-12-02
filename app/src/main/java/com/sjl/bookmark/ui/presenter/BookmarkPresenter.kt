package com.sjl.bookmark.ui.presenter

import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import com.sjl.bookmark.dao.impl.BookmarkService
import com.sjl.bookmark.entity.table.Bookmark
import com.sjl.bookmark.ui.activity.BookmarkSearchActivity
import com.sjl.bookmark.ui.adapter.BookmarkAdapter
import com.sjl.bookmark.ui.contract.BookmarkContract
import com.sjl.core.util.log.LogUtils
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkPresenter.java
 * @time 2018/1/29 15:44
 * @copyright(C) 2018 song
 */
class BookmarkPresenter : BookmarkContract.Presenter() {
    private var handler: Handler? = null
    private lateinit var bookmarkList: MutableList<Bookmark>
    private var isHasMore = true //false没有更多数据，true还有
    private var isLock = false
    private var currentPage = 1
    private var keyWord = ""
    private var sourceFile = ""
    override fun init(intent: Intent) {
        handler = Handler()
        bookmarkList = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            keyWord = bundle.getString(BookmarkSearchActivity.KEY_SEARCH_TEXT).toString()
            if (!TextUtils.isEmpty(keyWord)) {
                mView.setItemMenuVisible(false)
            }
        }
    }

    fun reset(){
        bookmarkList.clear()
        currentPage = 1
    }
    /**
     * 初始化数据
     */
    override fun initBookmarkList(sourceFile:String): List<Bookmark> {
        this.sourceFile = sourceFile
        LogUtils.i("当前页码：currentPage=$currentPage,搜索字符：$keyWord")
        val bookmarks = BookmarkService.getInstance(mContext).queryBookmarkByPage(this.sourceFile,keyWord, keyWord, currentPage, PAGE_SIZE)
        bookmarkList.addAll(bookmarks)
        return bookmarks
    }

    /**
     * 上拉加载
     */
    override fun pullRefreshUp() {
        LogUtils.i("滚动加锁标志：isLock=$isLock")
        synchronized(this) {
            if (!isLock) { //锁定防止多次滚动
                isLock = true
                if (isHasMore) {
                    currentPage++
                    handler?.postDelayed({
                        val bookmarks = initBookmarkList(this.sourceFile)
                        mView.showBookmarkData(bookmarkList, getLoadState(bookmarks))
                        isLock = false
                    }, 500)
                }
            }
        }
    }

    /**
     * 下拉刷新
     */
    override fun pullRefreshDown() {
        currentPage = 1
        isLock = false
        handler?.postDelayed({
            bookmarkList!!.clear()
            val bookmarks = initBookmarkList(this.sourceFile)
            if (mView != null) {
                mView.showBookmarkData(bookmarkList, getLoadState(bookmarks))
            }
        }, 1000)
    }

    /**
     * @param bookmarks 查询返回集合
     * @return
     */
    fun getLoadState(bookmarks: List<Bookmark>?): Int {
        return if (bookmarks != null && bookmarks.size > 0) {
            if (bookmarks.size < PAGE_SIZE) {
                LogUtils.i("没有更多数据了")
                isHasMore = false
                BookmarkAdapter.NO_LOAD_MORE
            } else {
                isHasMore = true
                BookmarkAdapter.LOADING_MORE
            }
        } else {
            isHasMore = false
            BookmarkAdapter.NO_LOAD_MORE
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}