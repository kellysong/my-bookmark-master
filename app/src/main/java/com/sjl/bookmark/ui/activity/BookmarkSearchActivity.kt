package com.sjl.bookmark.ui.activity

import android.content.*
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkSearchActivity.java
 * @time 2018/2/9 17:54
 * @copyright(C) 2018 song
 */
class BookmarkSearchActivity : BaseActivity<NoPresenter>(), View.OnClickListener {

    private lateinit var mInflater: LayoutInflater

    /**
     * 搜索标签，暂时写死
     */
    private val mVals: Array<String> = arrayOf(
        "JavaSE",
        "Android",
        "JavaEE",
        "Mui",
        I18nUtils.getString(R.string.label_database),
        I18nUtils.getString(R.string.label_front_end),
        I18nUtils.getString(R.string.label_app_store),
        I18nUtils.getString(R.string.label_other),
        I18nUtils.getString(R.string.label_basic_knowledge),
        I18nUtils.getString(R.string.label_push),
        I18nUtils.getString(R.string.label_css),
        I18nUtils.getString(R.string.label_h5),
        I18nUtils.getString(R.string.label_website)
    ) //数据模拟，实际应从网络获取此数据

    private lateinit var mPref //使用SharedPreferences记录搜索历史
            : SharedPreferences
    private lateinit var mHistoryKeywords //记录文本
            : MutableList<String>
    private lateinit var mArrAdapter //搜索历史适配器
            : ArrayAdapter<String>

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun initView() {
        setSupportActionBar(search_toolbar)
        cet_search_word.requestFocus() //获取焦点 光标出现
        initHistoryView()
    }

    override fun initListener() {
        iv_search.setOnClickListener(this)
        clear_history_btn.setOnClickListener(this)
        search_toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })
    }

    override fun initData() {
        mInflater = LayoutInflater.from(this)
        for (i in mVals.indices) {
            val tv: TextView = mInflater.inflate(
                R.layout.search_label_tv, flowlayout, false
            ) as TextView
            tv.text = mVals.get(i)
            val str: String = tv.text.toString()
            //点击事件
            tv.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    //加入搜索历史纪录记录
                    showSearchList(str)
                }
            })
            flowlayout.addView(tv)
        }
    }

    /************
     * 以上为流式标签相关
     */
    private fun initHistoryView() {
        mHistoryKeywords = ArrayList()
        cet_search_word.addTextChangedListener(object : TextWatcher {
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
                if (s.length == 0) {
                    if (mHistoryKeywords.size > 0) {
                        search_history_ll.visibility = View.VISIBLE
                    } else {
                        search_history_ll.visibility = View.GONE
                    }
                } else {
                    search_history_ll.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        cet_search_word.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(
                v: TextView,
                actionId: Int,
                event: KeyEvent
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val keywords: String = cet_search_word.text.toString()
                    if (!TextUtils.isEmpty(keywords)) {
                        save()
                        showSearchList(keywords)
                    }
                    return true
                }
                return false
            }
        })
        initSearchHistory()
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
                showSearchList(mHistoryKeywords[i])
            }
        }
        mArrAdapter.notifyDataSetChanged()
    }

    /**
     * 储存搜索历史
     */
    fun save() {
        val text: String = cet_search_word!!.text.toString()
        val oldText: String = mPref.getString(KEY_SEARCH_HISTORY_KEYWORD, "")
        LogUtils.i(
            "oldText:$oldText,$text,oldText.contains(text):" + oldText.contains(
                text
            )
        )
        if (!TextUtils.isEmpty(text) && !(oldText.contains(text))) {
            if (mHistoryKeywords.size > 5) { //最多保存条数
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_search -> {
                val keywords: String = cet_search_word.text.toString()
                if (!TextUtils.isEmpty(keywords)) {
                    save()
                    showSearchList(keywords)
                } else {
                    Toast.makeText(
                        this@BookmarkSearchActivity,
                        R.string.input_search_content,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            R.id.clear_history_btn -> cleanHistory()
            else -> {}
        }
    }

    /**
     * 显示搜索列表
     * @param keywords 搜索关键字
     */
    private fun showSearchList(keywords: String) {
        val intent: Intent = Intent(this, BookmarkActivity::class.java)
        val bundle: Bundle = Bundle()
        bundle.putString(KEY_SEARCH_TEXT, keywords)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    companion object {
        val KEY_SEARCH_HISTORY_KEYWORD: String = "key_search_history_keyword"
        val KEY_SEARCH_TEXT: String = "key_search_text" //搜索文本
    }
}