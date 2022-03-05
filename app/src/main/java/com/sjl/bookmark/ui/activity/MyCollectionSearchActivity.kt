package com.sjl.bookmark.ui.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.Collection
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.BrowserActivity
import com.sjl.bookmark.ui.activity.MyCollectionSearchActivity
import com.sjl.bookmark.ui.adapter.MyCollectionAdapter
import com.sjl.bookmark.ui.contract.MyCollectionContract
import com.sjl.bookmark.ui.presenter.MyCollectionPresenter
import com.sjl.core.app.BaseApplication
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.*
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.head_search_view.*
import kotlinx.android.synthetic.main.my_collection_search_activity.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionSearchActivity.java
 * @time 2018/4/10 15:51
 * @copyright(C) 2018 song
 */
class MyCollectionSearchActivity : BaseActivity<MyCollectionPresenter>(),
    MyCollectionContract.View, BaseQuickAdapter.OnItemClickListener, RequestLoadMoreListener,
    View.OnClickListener {

    lateinit var myCollectionAdapter: MyCollectionAdapter
    override fun getLayoutId(): Int {
        return R.layout.my_collection_search_activity
    }

    override fun initView() {}
    override fun initListener() {
        btn_search.setOnClickListener(this)
        cet_word.addTextChangedListener(object : TextWatcher {
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
                LogUtils.i("onTextChanged")
                val keywords: String = s.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(keywords)) { //没有输入
                    tv_msg!!.visibility = View.VISIBLE
                    tv_msg!!.text = "搜索收藏内容"
                    rv_search_collection_content!!.visibility = View.GONE
                } else {
                    //有输入，实时搜索
                    mPresenter!!.queryMyCollection(keywords)
                }
            }

            override fun afterTextChanged(s: Editable) {
                LogUtils.i("afterTextChanged")
            }
        })
        cet_word.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(
                v: TextView,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val keywords: String = cet_word.text.toString().trim { it <= ' ' }
                    if (!TextUtils.isEmpty(keywords)) { //回车搜索
                        ViewUtils.hideKeyBoard(this@MyCollectionSearchActivity, cet_word)
                        mPresenter.queryMyCollection(keywords)
                    }
                    return true
                }
                return false
            }
        })
    }

    override fun initData() {
        /**设置RecyclerView */
        val linearLayoutManager: LinearLayoutManager =
            LinearLayoutManager(BaseApplication.getContext())
        rv_search_collection_content.layoutManager = linearLayoutManager
        myCollectionAdapter = MyCollectionAdapter(R.layout.my_collection_recycle_item, null)
        /**隐藏文章类型 */
        rv_search_collection_content.adapter = myCollectionAdapter
        /**设置事件监听 */
        myCollectionAdapter.setOnItemClickListener(this)
        myCollectionAdapter.setOnLoadMoreListener(this, rv_search_collection_content)
    }

    override fun setMyCollection(collections: List<Collection>, loadType: Int) {
        when (loadType) {
            HttpConstant.LoadType.TYPE_REFRESH_SUCCESS -> if (collections != null && collections.size > 0) {
                rv_search_collection_content.visibility = View.VISIBLE
                tv_msg.visibility = View.GONE
                myCollectionAdapter.setNewData(collections)
                myCollectionAdapter.loadMoreComplete() //加载完成
            } else {
                rv_search_collection_content.visibility = View.GONE
                tv_msg.visibility = View.VISIBLE
                tv_msg.setText(R.string.no_item)
            }
            HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS -> if (collections != null && collections.size > 0) {
                myCollectionAdapter.addData(collections)
                myCollectionAdapter.loadMoreComplete() //加载完成
            } else {
                LogUtils.i("搜索收藏分页完毕")
                myCollectionAdapter.loadMoreEnd(false) //数据全部加载完毕
            }
            else -> {}
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_search -> finish()
            else -> {}
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val item: Collection? = myCollectionAdapter.getItem(position)
        if (item!!.type == 0) {
            BrowserActivity.Companion.startWithParams(this, item.title, item.href)
        } else if (item.type == 1) {
        }
    }

    override fun onLoadMoreRequested() {
        mPresenter.loadMore()
    }
}