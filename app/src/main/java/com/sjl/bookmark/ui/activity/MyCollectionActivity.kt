package com.sjl.bookmark.ui.activity

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.table.Collection
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.MyNoteActivity
import com.sjl.bookmark.ui.adapter.MyCollectionAdapter
import com.sjl.bookmark.ui.contract.MyCollectionContract
import com.sjl.bookmark.ui.presenter.MyCollectionPresenter
import com.sjl.bookmark.widget.PopWindow
import com.sjl.bookmark.widget.PopWindow.PopWindowOnClickListener
import com.sjl.bookmark.widget.RecyclerViewLocation
import com.sjl.core.app.BaseApplication
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.VibrateHelper
import com.sjl.core.util.log.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.my_collection_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionActivity.java
 * @time 2018/3/25 17:07
 * @copyright(C) 2018 song
 */
class MyCollectionActivity : BaseActivity<MyCollectionPresenter>(),
    MyCollectionContract.View, BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemLongClickListener, RequestLoadMoreListener, View.OnClickListener {

    lateinit var myCollectionAdapter: MyCollectionAdapter
    private var popWindow //长按popWindow
            : PopWindow? = null
    private var mEditMode: Boolean = false //false非编辑模式，true编辑模式
    private var isSelectAll: Boolean = false //false非全选,true全选
    private var index: Int = 0 //通统计选中选项
    private var position //点击条目索引
            : Int = 0

    override fun getLayoutId(): Int {
        return R.layout.my_collection_activity
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.my_collection))
        btn_delete.setOnClickListener(this)
        select_all.setOnClickListener(this)
    }

    override fun initData() {
        /**设置RecyclerView */
        val linearLayoutManager: LinearLayoutManager =
            LinearLayoutManager(BaseApplication.getContext())
        rv_collection.layoutManager = linearLayoutManager
        myCollectionAdapter = MyCollectionAdapter(R.layout.my_collection_recycle_item, null)
        /**隐藏文章类型 */
        rv_collection.adapter = myCollectionAdapter
        /**设置事件监听 */
        myCollectionAdapter.onItemClickListener = this
        myCollectionAdapter.onItemLongClickListener = this //长按点击事件
        myCollectionAdapter.setOnLoadMoreListener(this, rv_collection)
        mPresenter.loadMyCollection()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_collection_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSearch) {
            openActivity(MyCollectionSearchActivity::class.java)
        } else if (item.itemId == R.id.menuAdd) {
            openActivityForResult(MyNoteActivity::class.java, AppConstant.REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setMyCollection(collections: List<Collection>, loadType: Int) {
        when (loadType) {
            HttpConstant.LoadType.TYPE_REFRESH_SUCCESS -> if (collections != null && collections.size > 0) {
                fl_content.visibility = View.VISIBLE
                ll_empty_view.visibility = View.GONE
                myCollectionAdapter.setNewData(collections)
                myCollectionAdapter.loadMoreComplete() //加载完成
            } else {
                fl_content.visibility = View.GONE
                ll_empty_view.visibility = View.VISIBLE
                mPresenter.recoverCollectionDataFromServer() //没有数据，从服务器加载
            }
            HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS -> if (collections != null && collections.size > 0) {
                myCollectionAdapter.addData(collections)
                myCollectionAdapter.loadMoreComplete() //加载完成
            } else {
                LogUtils.i("收藏分页完毕")
                myCollectionAdapter.loadMoreEnd(false) //数据全部加载完毕
            }
            else -> {}
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (mEditMode) { //编辑模式
            val checkBox: CheckBox? = adapter.getViewByPosition(position, R.id.cb_item) as CheckBox?
            val item: Collection? = myCollectionAdapter.getItem(position)
            if (checkBox!!.isChecked) {
                checkBox.isChecked = false
                item!!.isSelectItem = false
                index--
            } else {
                checkBox.isChecked = true
                item!!.isSelectItem = true
                index++
            }
            val size: Int = myCollectionAdapter.data.size
            if (index == size) {
                isSelectAll = true
                select_all!!.text = getString(R.string.select_all_cancel)
            } else {
                isSelectAll = false
                select_all!!.text = getString(R.string.select_all)
            }
            setBtnBackground(index)
            return
        }
        val item: Collection? = myCollectionAdapter.getItem(position)
        if (item!!.type == 0) {
            BrowserActivity.startWithParams(this, item.title, item.href)
        } else if (item.type == 1) {
            this.position = position
            MyNoteActivity.startWithParams(this, item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("笔记回调成功,requestCode:" + requestCode + ",resultCode:" + resultCode)
        if (requestCode == AppConstant.REQUEST_CODE && resultCode == AppConstant.RESULT_CODE) {
            val bundle: Bundle? = data!!.extras //笔记添加或者修改回调
            if (bundle != null) {
                val noteFlag: Boolean = bundle.getBoolean("noteFlag", false)
                val collection: Collection? = bundle.getSerializable("collection") as Collection?
                if (collection == null) {
                    return
                }
                if (noteFlag) {
                    myCollectionAdapter.data.set(position, collection)
                    myCollectionAdapter.refreshNotifyItemChanged(position) //局部刷新
                } else { //添加
                    myCollectionAdapter.addData(0, collection)
                    showContentView()
                    //滚动到顶部
                    RecyclerViewLocation.moveToPosition(
                        rv_collection!!.layoutManager as LinearLayoutManager?,
                        0,
                        false
                    )
                }
            }
        }
    }

    override fun onLoadMoreRequested() {
        mPresenter.loadMore() //加载更多
    }

    override fun onItemLongClick(
        adapter: BaseQuickAdapter<*, *>?,
        view: View,
        position: Int
    ): Boolean {
        if (mEditMode) {
            return true //编辑模式
        }
        VibrateHelper.vSimple(this, 80) //震动80毫秒
        val item: Collection? = myCollectionAdapter.getItem(position)
        popWindow = PopWindow(this, object : PopWindowOnClickListener {
            override fun onClick(v: View) {
                popWindow!!.dismiss()
                popWindow!!.backgroundAlpha(this@MyCollectionActivity, 1f)
                when (v.id) {
                    R.id.tv_share -> {
                        val intent: Intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            getString(
                                R.string.share_article_url,
                                getString(R.string.app_name),
                                item!!.title,
                                item.href
                            )
                        )
                        intent.type = "text/plain"
                        startActivity(intent)
                    }
                    R.id.tv_delete -> {
                        index--
                        mPresenter!!.deleteCollection((item)!!)
                        myCollectionAdapter.remove(position)
                        showEmptyView()
                    }
                    R.id.tv_more -> {
                        mEditMode = true
                        ll_mycollection_bottom_dialog!!.visibility = View.VISIBLE
                        myCollectionAdapter.setEditMode(mEditMode)
                    }
                    R.id.tv_top -> {
                        if (popWindow!!.isExecuteTopFlag) { //置顶
                            //置顶
                            item!!.setTop(ON_TOP)
                            item.setTime(System.currentTimeMillis())
                        } else {
                            //取消
                            item!!.setTop(CANCEL_TOP)
                            item.setTime(0)
                        }
                        Observable.just(item)
                            .map(object : Function<Collection, Boolean> {
                                @Throws(Exception::class)
                                override fun apply(collection: Collection): Boolean {
                                    //更新数据库，后期分页会查询出来，要不查询出来可以在页面退出的时候再更新数据库
                                    mPresenter!!.updateCollection(item)
                                    val data: List<Collection> = myCollectionAdapter.data
                                    Collections.sort(data)
                                    return true
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .`as`(bindLifecycle())
                            .subscribe(object : Consumer<Boolean> {
                                @Throws(Exception::class)
                                override fun accept(next: Boolean) {
                                    myCollectionAdapter.notifyDataSetChanged()
                                }
                            }, object : Consumer<Throwable?> {
                                @Throws(Exception::class)
                                override fun accept(throwable: Throwable?) {
                                }
                            })
                    }
                    else -> {}
                }
            }
        })
        popWindow!!.setTopStates(item!!.getTop())
        popWindow!!.showPopupWindow(view)
        return true //这样不会触发点击事件
    }

    /**
     * 显示空布局
     */
    private fun showEmptyView() {
        val data: List<Collection>? = myCollectionAdapter.data
        if (data == null || data.size == 0) {
            fl_content.visibility = View.GONE
            ll_empty_view.visibility = View.VISIBLE
        }
    }

    /**
     * 显示内容布局
     */
    private fun showContentView() {
        val data: List<Collection>? = myCollectionAdapter.data
        if (data != null || data!!.size > 0) {
            fl_content.visibility = View.VISIBLE
            ll_empty_view.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VibrateHelper.stop()
    }

    override fun onBackPressed() {
        if (mEditMode) {
            index = 0
            mEditMode = false
            myCollectionAdapter.setEditMode(mEditMode)
            setBtnBackground(index)
            ll_mycollection_bottom_dialog!!.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_delete -> batchDeleteCollection()
            R.id.select_all -> selectAllCollection()
            else -> {}
        }
    }

    /**
     * 批量删除
     */
    private fun batchDeleteCollection() {
        val builder: AlertDialog = AlertDialog.Builder(this)
            .create()
        builder.show()
        if (builder.window == null) return
        builder.window?.setContentView(R.layout.dialog_confirm) //设置弹出框加载的布局
        val msg: TextView = builder.findViewById<View>(R.id.tv_msg) as TextView
        val cancel: Button = builder.findViewById<View>(R.id.btn_cancle) as Button
        val sure: Button = builder.findViewById<View>(R.id.btn_sure) as Button
        if (index == 1) {
            msg.setText(R.string.delete_hint)
        } else {
            msg.text = resources.getString(R.string.delete_hint2, index)
        }
        cancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                builder.dismiss()
            }
        })
        //确认删除
        sure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val size: Int = myCollectionAdapter.data.size
                var i: Int = size
                val j: Int = 0
                while (i > j) {
                    val collection: Collection = myCollectionAdapter.data.get(i - 1)
                    if (collection.isSelectItem) {
                        mPresenter!!.deleteCollection(collection)
                        myCollectionAdapter.data.remove(collection)
                        index--
                    }
                    i--
                }
                setBtnBackground(index)
                if (index == 0) {
                    ll_mycollection_bottom_dialog!!.visibility = View.GONE
                }
                mEditMode = false
                myCollectionAdapter.setEditMode(mEditMode)
                builder.dismiss()
                showEmptyView()
            }
        })
    }

    /**
     * 全选和反选
     */
    private fun selectAllCollection() {
        val size: Int = myCollectionAdapter.data.size
        if (!isSelectAll) {
            var i: Int = 0
            val j: Int = size
            while (i < j) {
                myCollectionAdapter.data.get(i).isSelectItem = true
                i++
            }
            index = myCollectionAdapter.data.size
            btn_delete!!.isEnabled = true
            select_all!!.text = getString(R.string.select_all_cancel)
            isSelectAll = true
        } else {
            var i: Int = 0
            val j: Int = size
            while (i < j) {
                myCollectionAdapter.data.get(i).isSelectItem = false
                i++
            }
            index = 0
            btn_delete!!.isEnabled = false
            select_all!!.text = getString(R.string.select_all)
            isSelectAll = false
        }
        myCollectionAdapter.notifyDataSetChanged()
        setBtnBackground(index)
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private fun setBtnBackground(size: Int) {
        if (size != 0) {
            btn_delete!!.setBackgroundResource(R.drawable.button_shape)
            btn_delete!!.isEnabled = true
            btn_delete!!.setTextColor(Color.WHITE)
        } else {
            btn_delete!!.setBackgroundResource(R.drawable.button_noclickable_shape)
            btn_delete!!.isEnabled = false
            btn_delete!!.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd))
        }
        tv_select_num!!.text = size.toString()
    }

    companion object {
        private val ON_TOP: Int = 1
        private val CANCEL_TOP: Int = 0
    }
}