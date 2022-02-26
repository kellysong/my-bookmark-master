package com.sjl.bookmark.ui.activity

import android.content.*
import android.os.*
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhuishu.table.CollectBook
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.activity.BookReadActivity
import com.sjl.bookmark.ui.activity.BookSearchActivity
import com.sjl.bookmark.ui.activity.BookShelfActivity
import com.sjl.bookmark.ui.adapter.ShelfAdapter
import com.sjl.bookmark.ui.adapter.ShelfAdapter.OnDeleteItemListener
import com.sjl.bookmark.ui.contract.BookShelfContract
import com.sjl.bookmark.ui.presenter.BookShelfPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.net.RxBus
import com.sjl.core.util.log.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.book_shelf_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.common_toolbar
import java.io.File
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfActivity.java
 * @time 2018/11/30 14:39
 * @copyright(C) 2018 song
 */
class BookShelfActivity : BaseActivity<BookShelfPresenter>(), BookShelfContract.View {


    private var bookLists: List<CollectBook>? = null
    private lateinit var shelfAdapter: ShelfAdapter

    /**
     * 点击书本的位置
     */
    private var itemPosition = 0
    override fun getLayoutId(): Int {
        setStatusBar(-0x1000000)
        return R.layout.book_shelf_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.tool_novel_read))
        fab.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@BookShelfActivity, FileSystemActivity::class.java)
            startActivity(intent)
        })
        /**
         * 点击书籍跳转到阅读页面
         */
        bookShelf.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                itemPosition = position
                val bookList: List<CollectBook>? = shelfAdapter.bookList //持有引用
                if ((bookList == null) || (bookList.size == 0) || (itemPosition > bookList.size - 1)) { //说明是绘制的书架背景
                    return
                }
                val collectBook: CollectBook = bookList.get(position)
                //如果是本地文件，首先判断这个文件是否存在
                if (collectBook.isLocal()) {
                    //id表示本地文件的路径
                    val path: String = collectBook.cover
                    val file: File = File(path)
                    //判断这个本地文件是否存在
                    if (file.exists() && file.length() != 0L) {
                        shelfAdapter.setItemToFirst(itemPosition)
                        BookReadActivity.Companion.startActivity(
                            this@BookShelfActivity,
                            collectBook,
                            true
                        ) //此时collectBook的排序id已经是最大
                    } else {
                        val tip: String =
                            this@BookShelfActivity.getString(R.string.nb_bookshelf_book_not_exist)
                        //提示(从目录中移除这个文件)
                        AlertDialog.Builder(this@BookShelfActivity)
                            .setTitle(resources.getString(R.string.nb_common_tip))
                            .setMessage(tip)
                            .setPositiveButton(
                                resources.getString(R.string.nb_common_sure),
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface, which: Int) {
                                        mPresenter!!.deleteBook(collectBook)
                                    }
                                })
                            .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                            .show()
                    }
                } else {
                    shelfAdapter.setItemToFirst(itemPosition)
                    BookReadActivity.Companion.startActivity(
                        this@BookShelfActivity,
                        collectBook,
                        true
                    )
                }
            }
        }
        bookShelf.setSwipeRefreshLayout(srl_bookShelf)
        srl_bookShelf.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        refreshShelfBook()
                        srl_bookShelf.isRefreshing = false
                    }
                }, 1000)
            }
        })
        //监听书架添加书籍
        val subscribe = RxBus.getInstance()
            .toObservable(Boolean::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<Boolean> {
                @Throws(Exception::class)
                override fun accept(s: Boolean) {
                    if (s) {
                        LogUtils.i("书架更新，新增收藏$s")
                    } else {
                        LogUtils.i("书架更新，移除收藏$s")
                    }
                    mPresenter.getRecommendBook()
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                }
            })
        addDisposable(subscribe)
    }

    override fun initData() {
        bookLists = ArrayList()
        shelfAdapter = ShelfAdapter(this, bookLists)
        bookShelf.adapter = shelfAdapter
        shelfAdapter.setItemDeleteListener(object : OnDeleteItemListener {
            override fun item(deletePosition: Int) {
                itemPosition = deletePosition
                val collectBook = shelfAdapter.bookList[deletePosition]
                mPresenter.deleteBook(collectBook)
            }
        })
        shelfAdapter.setDragGridView(bookShelf)
        showLoadingDialog()
        mPresenter.getRecommendBook()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.book_shelf_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            val intent = Intent(this, BookSearchActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.action_clear) {
            AlertDialog.Builder(this@BookShelfActivity)
                .setTitle(resources.getString(R.string.nb_common_tip))
                .setMessage(R.string.book_shelf_hint)
                .setPositiveButton(
                    resources.getString(R.string.nb_common_sure),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            showLoadingDialog(getString(R.string.book_delete_hint))
                            mPresenter.deleteAllBook()
                            dialog.dismiss()
                        }
                    })
                .setNegativeButton(resources.getString(R.string.nb_common_cancel), null)
                .show()
        } else if (id == R.id.action_refresh) {
            refreshShelfBook()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 刷新书架书籍
     */
    private fun refreshShelfBook() {
        val bookList = shelfAdapter.bookList
        showLoadingDialog(getString(R.string.book_update_hint))
        if (bookList != null && !bookList.isEmpty()) { //书架已经有数据
            mPresenter.getRecommendBook()
        } else {
            mPresenter.refreshCollectBooks()
        }
    }

    override fun showErrorMsg(msg: String) {
        showToast(msg)
        hideLoadingDialog()
    }

    override fun showRecommendBook(collBookBeans: List<CollectBook>) {
        shelfAdapter.setItems(collBookBeans) //刷新图书
        hideLoadingDialog()
    }

    override fun refreshBook() {
        val remove = shelfAdapter.bookList.removeAt(itemPosition)
        LogUtils.i("删除的书本是:" + remove.title)
        if (shelfAdapter.bookList.size == 0) { //没有书籍了隐藏删除按钮，防止重新加载书籍时显示删除按钮
            bookShelf.isShowDeleteButton = false
        }
        shelfAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        bookShelf.isShowDeleteButton = false
        shelfAdapter.notifyDataSetChanged()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bookShelf.isShowDeleteButton) {
                bookShelf.isShowDeleteButton = false
                shelfAdapter.notifyDataSetChanged()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}