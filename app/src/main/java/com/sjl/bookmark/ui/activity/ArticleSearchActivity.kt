package com.sjl.bookmark.ui.activity

import android.content.*
import android.os.Parcelable
import android.text.*
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Article.DatasBean
import com.sjl.bookmark.entity.Category.ChildrenBean
import com.sjl.bookmark.entity.HotKey
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.activity.BrowserActivity
import com.sjl.bookmark.ui.adapter.ArticleAdapter
import com.sjl.bookmark.ui.contract.ArticleSearchContract
import com.sjl.bookmark.ui.presenter.ArticleSearchPresenter
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.*
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.article_search_activity.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.head_search_view.*
import java.util.*

/**
 * 文章搜索
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchActivity.java
 * @time 2018/3/30 16:00
 * @copyright(C) 2018 song
 */
class ArticleSearchActivity : BaseActivity<ArticleSearchPresenter>(),
    ArticleSearchContract.View, BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, RequestLoadMoreListener, View.OnClickListener {

    private lateinit var mArticleAdapter: ArticleAdapter
    private lateinit var mInflater: LayoutInflater
    private lateinit var mPref //使用SharedPreferences记录搜索历史
            : SharedPreferences
    private lateinit var mHistoryKeywords //记录文本
            : MutableList<String>
    private lateinit var mArrAdapter //搜索历史适配器
            : ArrayAdapter<String>

    override fun getLayoutId(): Int {
        return R.layout.article_search_activity
    }

    override fun initView() {}
    override fun initListener() {
        btn_search.setOnClickListener(this)
        clear_history_btn.setOnClickListener(this)
        cet_word!!.addTextChangedListener(object : TextWatcher {
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
                if (s.length == 0) { //没有输入
                    sv_content.visibility = View.VISIBLE
                    ll_hot_key.visibility = View.VISIBLE //显示热门搜索
                    rv_content.visibility = View.GONE
                    ll_empty_view.visibility = View.GONE
                    if (mHistoryKeywords.size > 0) {
                        search_history_ll.visibility = View.VISIBLE
                    } else {
                        search_history_ll.visibility = View.GONE
                    }
                } else {
                    //有输入，不管是否进行了搜索操作，保持搜索框下面原貌
                    search_history_ll.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        cet_word.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(
                v: TextView,
                actionId: Int,
                event: KeyEvent
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val keywords: String = cet_word!!.text.toString().trim({ it <= ' ' })
                    if (!TextUtils.isEmpty(keywords)) { //回车搜索
                        save()
                        ViewUtils.hideKeyBoard(this@ArticleSearchActivity, cet_word)
                        mPresenter.searchData(keywords)
                    }
                    return true
                }
                return false
            }
        })
    }

    override fun initData() {
        mHistoryKeywords = ArrayList()
        initSearchHistory()
        mInflater = LayoutInflater.from(this)
        rv_content.layoutManager = LinearLayoutManager(this)
        mArticleAdapter = ArticleAdapter(R.layout.home_article_recycle_item, null)
        rv_content.adapter = mArticleAdapter
        /**设置事件监听 */
        mArticleAdapter.onItemClickListener = this
        mArticleAdapter.onItemChildClickListener = this
        mArticleAdapter.setOnLoadMoreListener(this, rv_content)
        mPresenter.getHotKeyData()
    }

    override fun getHotKeySuccess(data: List<HotKey>) {
        val size: Int = data.size
        for (i in 0 until size) {
            val tv: TextView = mInflater.inflate(
                R.layout.search_label_tv, layout_hot_key, false
            ) as TextView
            tv.text = data.get(i).name
            val str: String = tv.text.toString()
            val finalI: Int = i
            //点击事件
            tv.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    cet_word.setText(data.get(finalI).name)
                    // 将光标移至字符串尾部
                    val charSequence: CharSequence = cet_word!!.text
                    if (charSequence is Spannable) {
                        Selection.setSelection(charSequence, charSequence.length)
                    }
                    ViewUtils.hideKeyBoard(this@ArticleSearchActivity, cet_word)
                    mPresenter.searchData(cet_word.text.toString().trim({ it <= ' ' }))
                }
            })
            layout_hot_key.addView(tv)
        }
    }

    /**
     * 初始化搜索历史记录
     */
    fun initSearchHistory() {
        mPref = getSharedPreferences("search_config", MODE_PRIVATE)
        val history: String = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "")
        if (!TextUtils.isEmpty(history)) {
            val list: MutableList<String> = ArrayList()
            for (o: Any in history.split(",").toTypedArray()) {
                list.add(o as String)
            }
            mHistoryKeywords = list
        }
        if (mHistoryKeywords.size > 0) {
            search_history_ll.visibility = View.VISIBLE
        } else {
            search_history_ll.visibility = View.GONE
        }
        mArrAdapter = ArrayAdapter(this, R.layout.item_search_history, mHistoryKeywords)
        search_history_lv.adapter = mArrAdapter
        search_history_lv.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                cet_word.setText(mHistoryKeywords[i])
                val charSequence: CharSequence = cet_word.text
                if (charSequence is Spannable) {
                    Selection.setSelection(charSequence, charSequence.length)
                }
                ViewUtils.hideKeyBoard(this@ArticleSearchActivity, cet_word)
                mPresenter.searchData(mHistoryKeywords[i])
            }
        }
        mArrAdapter.notifyDataSetChanged()
    }

    /**
     * 储存搜索历史
     */
    fun save() {
        val text: String = cet_word.text.toString()
        val oldText: String = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "")
        LogUtils.i(
            "oldText:$oldText,$text,oldText.contains(text):" + oldText.contains(
                text
            )
        )
        if (!TextUtils.isEmpty(text) && !(oldText.contains(text))) {
            if (mHistoryKeywords.size > 20) { //最多保存条数
                return
            }
            val editor: SharedPreferences.Editor = mPref.edit()
            editor.putString(KEY_SEARCH_HISTORY_KEYWORD, "$text,$oldText")
            editor.commit()
            mHistoryKeywords.add(0, text)
        }
        mArrAdapter.notifyDataSetChanged()
    }

    /**
     * 清除历史纪录
     */
    fun cleanHistory() {
        // 创建构建器
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // 设置参数
        builder.setTitle(R.string.nb_common_tip)
            .setMessage(R.string.delete_hint3)
            .setPositiveButton(R.string.sure, object : DialogInterface.OnClickListener {
                // 积极
                override fun onClick(dialog: DialogInterface, which: Int) {
                    val editor: SharedPreferences.Editor = mPref.edit()
                    editor.remove(KEY_SEARCH_HISTORY_KEYWORD).commit()
                    mHistoryKeywords.clear()
                    mArrAdapter.notifyDataSetChanged()
                    search_history_ll.visibility = View.GONE
                }
            }).setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                // 消极
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                }
            })
        builder.create().show()
    }

    override fun showFailMsg(message: String) {
        SnackbarUtils.makeShort(window.decorView, message).danger()
    }

    override fun searchDataSuccess(data: List<DatasBean>) {
        if (data == null || data.size == 0) { //没有搜索到数据
//            llHotKey.setVisibility(BaseView.GONE);
//            historySearchKey.setVisibility(BaseView.GONE);
            sv_content.visibility = View.GONE
            rv_content.visibility = View.GONE
            ll_empty_view.visibility = View.VISIBLE
        } else {
//            llHotKey.setVisibility(BaseView.GONE);
//            historySearchKey.setVisibility(BaseView.GONE);
            sv_content.visibility = View.GONE
            rv_content.visibility = View.VISIBLE
            ll_empty_view.visibility = View.GONE
            mArticleAdapter.setNewData(data)
        }
    }

    override fun loadMoreDataSuccess(data: List<DatasBean>) {
        if (data == null || data.size == 0) {
            mArticleAdapter.loadMoreEnd()
        } else {
            mArticleAdapter.addData(data)
            mArticleAdapter.loadMoreComplete()
        }
    }

    override fun onLoadMoreRequested() {
        val keyWord: String = cet_word!!.text.toString().trim({ it <= ' ' })
        if (!TextUtils.isEmpty(keyWord)) {
            mPresenter!!.getMoreData(keyWord)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_search -> finish()
            R.id.clear_history_btn -> cleanHistory()
            else -> {}
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        BrowserActivity.Companion.startWithParams(
            this, mArticleAdapter.getItem(position)!!.title,
            mArticleAdapter.getItem(position)!!.link
        )
    }

    override fun onItemChildClick(
        adapter: BaseQuickAdapter<*, *>?,
        view: View,
        position: Int
    ) {
        if (view.id == R.id.tvChapterName) {
            val intent: Intent = Intent(this, ArticleTypeActivity::class.java)
            intent.putExtra(
                HttpConstant.CONTENT_TITLE_KEY,
                mArticleAdapter.getItem(position)!!.chapterName
            )
            val children: MutableList<ChildrenBean?> = ArrayList()
            children.add(
                ChildrenBean(
                    mArticleAdapter.getItem(position)!!.chapterId,
                    mArticleAdapter.getItem(position)!!.chapterName
                )
            )
            intent.putParcelableArrayListExtra(
                HttpConstant.CONTENT_CHILDREN_DATA_KEY,
                children as ArrayList<out Parcelable?>?
            )
            intent.putExtra(HttpConstant.CONTENT_OPEN_FLAG, "0")
            startActivity(intent)
        }
    }

    companion object {
        val KEY_SEARCH_HISTORY_KEYWORD: String = "key_search_history_keyword2"
    }
}